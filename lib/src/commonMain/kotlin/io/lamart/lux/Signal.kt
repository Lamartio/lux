package io.lamart.lux

sealed class Signal<out I, out O> {
    data class Start<I> internal constructor(val input: I) : Signal<I, Nothing>()
    data class Next<O> internal constructor(val output: O) : Signal<Nothing, O>()
    data class End internal constructor(val reason: Throwable?) : Signal<Nothing, Nothing>()
}