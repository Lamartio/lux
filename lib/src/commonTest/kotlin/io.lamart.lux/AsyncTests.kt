package io.lamart.lux

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AsyncTests {

    data class State(val fetching: Async<Int, String> = Async.Idle)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun executeAsyncAction() = runTest {
        val (results, mutable) = State().asTestMutable()
        val lens = mutable.lens.compose(State::fetching.lens { copy(fetching = it) })
        val actions = lens.toAsyncActions(this, Behavior.switching(suspension = { it.toString() }))

        actions.start(0)
        advanceUntilIdle()
        actions.cancel()

        assertEquals(results.size, 3)
        assertTrue(results[0].fetching is Async.Idle)
        assertTrue(results[1].fetching is Async.Executing)
        assertTrue(results[2].fetching is Async.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun cancelOngoingAction() = runTest {
        val (results, mutable) = Async.Idle.asTestMutable<Async<Int, String>>()
        val actions = mutable.lens.toAsyncActions(this, Behavior.switching(suspension = {
            delay(1000)
            it.toString()
        }))

        actions.start(0)
        advanceTimeBy(500)
        actions.stop()

        actions.start(1)
        advanceUntilIdle()
        actions.cancel()

        assertEquals(results.size, 5)
        assertTrue(results[0] is Async.Idle)
        assertTrue(results[1] is Async.Executing)
        assertTrue(results[2] is Async.Failure)
        assertTrue(results[3] is Async.Executing)
        assertTrue(results[4] is Async.Success)
    }

    @Test
    fun testy() = runTest {
        val behavior = Behavior.switching<Int, String>(suspension = {
            delay(1000)
            it.toString()
        })
        val results = behavior(flowOf(1, 2)).toList()

        assertEquals(results.size, 4)
    }

}

fun <S : Any> S.asTestMutable(): Pair<MutableList<S>, Mutable<S>> {
    val results = mutableListOf(this@asTestMutable)
    val mutable = Mutable(results::last, results::add)

    return results to mutable
}