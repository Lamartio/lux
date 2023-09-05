package io.lamart.lux.sample

import io.lamart.lux.Async
import io.lamart.lux.Stream

data class AppState internal constructor(
    val count: Int = 0,
    val fetching: Async<Int, String> = Async.Idle(),
    val clock: Stream<Unit, Int> = Stream()
)
