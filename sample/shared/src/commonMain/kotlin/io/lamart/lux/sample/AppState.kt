package io.lamart.lux.sample

import io.lamart.lux.Actions
import io.lamart.lux.Async
import io.lamart.lux.Behavior
import io.lamart.lux.Machine
import io.lamart.lux.Mutable
import io.lamart.lux.Stream
import io.lamart.lux.focus.FocusedSetter
import io.lamart.lux.lens
import io.lamart.lux.toMutable
import io.lamart.lux.toStreamActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach

class AppMachine : Machine<AppState, AppActions>(params) {
    val counter = compose(state = AppState::count, actions = AppActions::counter)
    val clock = compose(state = { it.clock.result.getOrNull() ?: 0 }, actions = AppActions::clock)

    private companion object {
        val params: Machine<AppState, AppActions>
            get() {
                val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
                val state = MutableStateFlow(AppState())
                val mutable = state.toMutable()
                val actions = AppActions(scope, mutable)

                return Machine(scope, state, actions)
            }
    }
}

data class AppState(
    val count: Int = 0,
    val fetching: Async<Int, String> = Async.Idle(),
    val clock: Stream<Unit, Int> = Stream()
)

class AppActions(private val scope: CoroutineScope, mutable: Mutable<AppState>) {
    val counter = mutable
        .compose(AppState::count.lens { copy(count = it) })
        .let(::CounterActions)
    val clock = mutable
        .compose(AppState::clock.lens { copy(clock = it) })
        .toStreamActions(scope, Behavior.switching(flow = {
            (1..Int.MAX_VALUE)
                .asFlow()
                .onEach { delay(1000) }
        }))
        .let(::ClockActions)
}

// Workaround to get a clear type in Swift
class ClockActions(actions: Actions<Unit>) : Actions.Instance<Unit>(actions)

class CounterActions(private val focus: FocusedSetter<*, Int>) {
    fun increment() = focus.modify { it + 1 }
    fun decrement() = focus.modify { it - 1 }
}