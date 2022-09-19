@file:OptIn(ExperimentalCoroutinesApi::class)
package io.lamart.optics

import arrow.core.Option
import arrow.core.identity
import arrow.core.none
import arrow.core.some
import arrow.optics.PLens
import io.lamart.optics.async.*
import io.lamart.optics.source.Source
import io.lamart.optics.source.SourcedLens
import io.lamart.optics.source.invoke
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamTests {

    data class State<T>(val state: Stream<T> = Stream()) {

        companion object {
            fun <T> test(): Pair<List<State<T>>, SourcedLens<State<T>, Stream<T>>> {
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
    fun test() = runTest {
        val (states, lens) = State.test<Int>()
        val actions: Actions<Int> = Stream.actionsOf(
            source = lens,
            behavior = merging(suspension = ::identity),
            scope = this,
            effect = effectOf(),
            pipe = Pipe({}, emptyFlow())
        )

        actions.execute(1)
        advanceUntilIdle()

        assertEquals(
            expected = states.map { it.state },
            actual = listOf(
                Stream.idle(),
                Stream.executing(value = none()),
                Stream.executing(value = Option(1)),
                Stream.success(value = Option(1))
            )
        )
    }
}