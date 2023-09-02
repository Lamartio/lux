package io.lamart.lux.sample

import io.lamart.lux.Async
import io.lamart.lux.Mutable
import io.lamart.lux.lens
import io.lamart.lux.focus.FocusedSetter

data class AppState(val fetching: Async<Int, String> = Async.Idle, val count: Int = 0) {
    companion object {
        val initial = AppState()
    }
}

class AppActions(get: () -> AppState, set: (AppState) -> Unit) {
    private val mutable = Mutable(get, set)
    val counter = mutable
        .compose(AppState::count.lens { copy(count = it) })
        .let(::CounterActions)
}

class CounterActions(private val focus: FocusedSetter<*, Int>) {
    fun increment() = focus.modify { it + 1 }
    fun decrement() = focus.modify { it - 1 }
}
