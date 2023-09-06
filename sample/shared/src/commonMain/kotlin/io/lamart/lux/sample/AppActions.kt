package io.lamart.lux.sample

import io.lamart.lux.Behavior
import io.lamart.lux.actions.Actions
import io.lamart.lux.actions.toStreamActions
import io.lamart.lux.focus.FocusedLens
import io.lamart.lux.focus.FocusedSetter
import io.lamart.lux.focus.lens
import io.lamart.lux.focus.lensOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach

class AppActions internal constructor(scope: CoroutineScope, focus: FocusedLens<*, AppState>) {

    val counter = focus
        .compose(lensOf(AppState::count, { copy(count = it) }))
        .let(::CounterActions)

    val clock = focus
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