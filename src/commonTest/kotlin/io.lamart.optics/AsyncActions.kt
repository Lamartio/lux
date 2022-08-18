package io.lamart.optics

import arrow.optics.PLens
import io.lamart.optics.async.*
import io.lamart.optics.async.AsyncActions
import io.lamart.optics.source.Source
import io.lamart.optics.source.SourcedLens
import io.lamart.optics.source.invoke
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AsyncActions {

    data class State<T>(val async: Async<T> = idle()) {

        companion object {
            fun <T> test(): Pair<List<State<T>>, SourcedLens<State<T>, Async<T>>> {
                val results = mutableListOf(State<T>())
                val source = Source(get = results::last, set = results::add)
                val async = source.compose(PLens({ it.async }, { s, f -> s.copy(async = f) }))

                return results to async
            }
        }
    }

    @Test
    fun test() = runTest {
        val scope = this
        val (results, source) = State.test<String>()
        val actions = source
            .toAsyncActions<Int, String>(
                behavior = concatting {
                    flowOf(it.toString(), it.toString())
                },
                scope = scope,
            )

        actions.execute(1)
        delay(3000)
        assertEquals(results.size, 3)
    }

    @Test
    fun synchronousBehaviorShouldProduceEqualResults() = runTest {
        val stringify: suspend (Int) -> String = { it.toString() }
        val behaviors = listOf(
            merging(stringify),
            concatting(stringify),
            switching(stringify),
            exhausting(stringify)
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
    fun effect() = runTest {
        val (states, lens) = State.test<String>()
        val actions = AsyncActions(
            source = lens,
            behavior = concatting<Int,String>(suspension = { it.toString() }),
            scope = this
        )

        actions.execute(1)
        advanceUntilIdle()
        print("result: $states")

        assertEquals(3, states.size)
    }

}