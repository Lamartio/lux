package io.lamart.lux

data class Stream<I : Any, O : Any>(
    val state: Async<I, Unit> = Async.Idle(),
    val result: O? = null
)
