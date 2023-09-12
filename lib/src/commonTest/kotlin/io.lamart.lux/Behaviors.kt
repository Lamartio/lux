package io.lamart.lux

import io.lamart.lux.actions.toAsyncActions
import io.lamart.lux.actions.toStreamActions
import io.lamart.lux.focus.FocusedLens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class Behaviors {

    @Test
    fun asyncBehavior() = runTest {
        val (results, mutable) = (Async.Idle<Int, String>() as Async<Int, String>).asTestMutable()
        val actions = mutable
            .lens
            .toAsyncActions(this, Behavior.exhausting(suspension = { it.toString() }))

        actions.start(0)
        advanceUntilIdle()
        actions.cancel()

        assertEquals(results.size, 3)
    }

    @Test
    fun streamSuspensionBehavior() = runTest {
        val (results, mutable) = Stream<Int, String>().asTestMutable()
        val actions = mutable
            .lens
            .toStreamActions(this, Behavior.exhausting(suspension = { it.toString() }))

        actions.start(0)
        actions.start(1)
        advanceUntilIdle()
        actions.cancel()

        assertEquals(results.size, 8)
    }

    @Test
    fun streamFlowBehavior() = runTest {
        val (results, mutable) = Stream<Int, String>().asTestMutable()
        val actions = mutable
            .lens
            .toStreamActions(this, Behavior.exhausting(flow = { flowOf(it, it + 1).map(Int::toString) }))

        actions.start(0)
        advanceUntilIdle()
        actions.cancel()

        assertEquals(results.size, 6)
    }
}