package io.lamart.lux

import arrow.core.Option
import arrow.core.none

data class Stream<I : Any, O : Any>(
    val state: Async<I, Unit> = Async.Idle(),
    val result: Option<O> = none()
)
