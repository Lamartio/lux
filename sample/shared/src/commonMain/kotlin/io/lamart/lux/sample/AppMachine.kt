package io.lamart.lux.sample

import io.lamart.lux.Machine
import io.lamart.lux.toMutable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

class AppMachine : Machine<AppState, AppActions>(AppState(), ::AppActions) {
    val counter = compose(state = AppState::count, actions = AppActions::counter)
    val clock = compose(state = { it.clock.result ?: 0 }, actions = AppActions::clock)
}