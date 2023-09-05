package io.lamart.lux.sample

import io.lamart.lux.Machine
import io.lamart.lux.toMutable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

class AppMachine : Machine<AppState, AppActions>(initial) {
    val counter = compose(state = AppState::count, actions = AppActions::counter)
    val clock = compose(state = { it.clock.result ?: 0 }, actions = AppActions::clock)

    private companion object {
        private val initial: Machine<AppState, AppActions>
            get() {
                val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
                val state = MutableStateFlow(AppState())
                val mutable = state.toMutable()
                val actions = AppActions(scope, mutable.lens)

                return Machine(scope, state, actions)
            }
    }
}