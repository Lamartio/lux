package io.lamart.lux

import arrow.optics.Lens
import io.lamart.lux.focus.FocusedLens
import io.lamart.lux.focus.lensOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

data class AppState(val count: Int = 0)

class AppActions(focus: FocusedLens<*, AppState>) {

    // Lens is a placeholder that holds info of how to get and set a property
    private val lens: Lens<AppState, Int> = lensOf(
        get = AppState::count,
        set = { copy(count = it) } // expanded: { value -> this.copy(count = value) }
    )

    // apply the Lens to another to create a 'path' to the desired property
    private val countFocus: FocusedLens<*, Int> = focus.compose(lens)

    // get, set, or modify the property and it will update the owner
    fun increment() = countFocus.modify { it + 1 }
    fun decrement() = countFocus.modify { it - 1 }
}

class AppActionsCondensed(focus: FocusedLens<*, AppState>) {
    private val countFocus = focus.compose(lensOf(AppState::count, { copy(count = it) }))

    fun increment() = countFocus.modify { it + 1 }
    fun decrement() = countFocus.modify { it - 1 }
}

class AppMachine : Machine<AppState, AppActions>(
    value = AppState(),
    actionsFactory = { _, focus -> AppActions(focus) },
)

class TestAppMachine(scope: CoroutineScope) : Machine<AppState, AppActions>(
    value = AppState(),
    scope = scope,
    actionsFactory = { _, focus -> AppActions(focus) }
)

@OptIn(ExperimentalCoroutinesApi::class)
class Readme {
    @Test
    fun incrementAndDecrement() {
        val machine = AppMachine()

        assertEquals(machine.value.count, 0)
        machine.actions.increment()
        assertEquals(machine.value.count, 1)
        machine.actions.decrement()
        assertEquals(machine.value.count, 0)
    }

    @Test
    fun incrementAndDecrementWithFlow() = runTest {
        val machine = TestAppMachine(this)
        val results = mutableListOf<AppState>()
        val job = launch { machine.toList(results) } // Machine implements StateFlow 🚀

        machine.actions.increment()
        advanceUntilIdle()
        machine.actions.decrement()
        advanceUntilIdle()
        job.cancel()

        assertEquals(results.map(AppState::count), listOf(0, 1, 0))
    }
}