@file:OptIn(ExperimentalCoroutinesApi::class)
package io.lamart.optics

import arrow.core.identity
import arrow.optics.PLens
import io.lamart.optics.async.*
import io.lamart.optics.source.Source
import io.lamart.optics.source.SourcedLens
import io.lamart.optics.source.invoke
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActionsTests {

    data class State<T>(val state: Async<T> = Async.idle()) {

        companion object {
            fun <T> test(): Pair<List<State<T>>, SourcedLens<State<T>, Async<T>>> {
                val results = mutableListOf(State<T>())
                val source = Source(
                    get = results::last,
                    modify = { map -> results.last().let(map).also(results::add) }
                )
                val async = source.compose(PLens({ it.state }, { s, f -> s.copy(state = f) }))

                return results to async
            }
        }
    }

    @Test
    fun synchronousBehaviorShouldProduceEqualResults() = runTest {
        val behaviors = listOf<Behavior<Int, Int>>(
            merging(suspension = ::identity),
            concatting(suspension = ::identity),
            switching(suspension = ::identity),
            exhausting(suspension = ::identity)
        )

        behaviors
            .map { behavior ->
                flowOf(1, 2, 3)
                    .let(behavior)
                    .toCollection(mutableListOf())
            }
            .forEach { results ->
                assertEquals(results.size, 4)
                assertTrue(results.first().isExecuting)
                assertTrue(results.last().isSuccess)
            }
    }

    @Test
    fun executeSinglePayload() = runTest {
        val (states, lens) = State.test<Int>()
        val actions = Async.actionsOf(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = effectOf(),
            pipe = Pipe({}, emptyFlow())
        )

        actions.execute(0)
        advanceUntilIdle()

        assertEquals(
            expected = states.map { it.state },
            actual = listOf(
                Async.idle(),
                Async.executing(),
                Async.success(0)
            )
        )
    }

    @Test
    fun executeMultiplePayloads() = runTest {
        val (states, lens) = State.test<Int>()
        val actions = Async.actionsOf(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = effectOf(),
            pipe = Pipe({}, emptyFlow())
        )

        actions.execute(1, 2, 3)
        advanceUntilIdle()

        assertEquals(
            expected = states.map { it.state },
            actual = listOf(
                Async.idle(),
                Async.executing(),
                Async.success(1),
                Async.success(2),
                Async.success(3)
            )
        )
    }

    @Test
    fun multipleExecuteSinglePayload() = runTest {
        val (states, lens) = State.test<Int>()
        val times = 3
        val flow = MutableSharedFlow<Int>()
        val actions = Async.actionsOf(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = effectOf(),
            pipe = Pipe(flow::emit, flow.take(times-1))
        )

        repeat(times, actions::execute)
        advanceUntilIdle()

        assertEquals(
            expected = states.map { it.state },
            actual = listOf(
                Async.idle(),
                Async.executing(),
                Async.success(0),
                Async.success(1),
                Async.success(2)
            )
        )
    }

    @Test
    fun checkEffect() = runTest {
        val (states, lens) = State.test<Int>()
        val effects = mutableListOf<Async<Int>>()
        val actions = Async.actionsOf(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = { it.onEach(effects::add) },
            pipe = Pipe({}, emptyFlow())
        )

        actions.execute(1)
        advanceUntilIdle()

        assertEquals(
            states.map { it.state }.drop(1), // drop the default state
            effects
        )
    }
}

