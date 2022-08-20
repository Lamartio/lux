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

@OptIn(ExperimentalCoroutinesApi::class)
class AsyncActionsTests {

    data class State<T>(val state: io.lamart.optics.async.State<T> = idle()) {

        companion object {
            fun <T> test(): Pair<List<State<T>>, SourcedLens<State<T>, io.lamart.optics.async.State<T>>> {
                val results = mutableListOf(State<T>())
                val source = Source(get = results::last, set = results::add)
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
        val actions = Actions(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = effectOf(),
            emit = { },
            getFlow = ::emptyFlow
        )

        actions.execute(0)
        advanceUntilIdle()

        assertEquals(
            expected = states.map { it.state },
            actual = listOf(
                idle(),
                executing(),
                success(0)
            )
        )
    }

    @Test
    fun executeMultiplePayloads() = runTest {
        val (states, lens) = State.test<Int>()
        val actions = Actions(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = effectOf(),
            emit = { },
            getFlow = ::emptyFlow
        )

        actions.execute(1, 2, 3)
        advanceUntilIdle()

        assertEquals(
            expected = states.map { it.state },
            actual = listOf(
                idle(),
                executing(),
                success(1),
                success(2),
                success(3)
            )
        )
    }

    @Test
    fun multipleExecuteSinglePayload() = runTest {
        val (states, lens) = State.test<Int>()
        val times = 3
        val flow = MutableSharedFlow<Int>()
        val actions = Actions(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = effectOf(),
            emit = flow::emit,
            getFlow = { flow.take(times-1) }
        )

        repeat(times, actions::execute)
        advanceUntilIdle()

        assertEquals(
            expected = states.map { it.state },
            actual = listOf(
                idle(),
                executing(),
                success(0),
                success(1),
                success(2)
            )
        )
    }

    @Test
    fun checkEffect() = runTest {
        val (states, lens) = State.test<Int>()
        val effects = mutableListOf<io.lamart.optics.async.State<Int>>()
        val actions = Actions(
            source = lens,
            behavior = concatting(suspension = ::identity),
            scope = this,
            effect = { it.onEach(effects::add) },
            emit = { },
            getFlow = ::emptyFlow
        )

        actions.execute(1)
        advanceUntilIdle()

        assertEquals(
            states.map { it.state }.drop(1), // drop the default state
            effects
        )
    }
}

