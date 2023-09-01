package io.lamart.lux

import arrow.core.Option
import arrow.core.none

data class Stream<I, O>(
    val state: Async<I, Unit> = Async.Idle,
    val result: Option<O> = none()
) {
    companion object {
        fun <I, O> state() = Stream<I, O>::state.lens { copy(state = it) }
        fun <I, O> result() = Stream<I, O>::result.lens { copy(result = it) }
    }
}
