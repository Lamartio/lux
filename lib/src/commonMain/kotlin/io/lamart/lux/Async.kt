package io.lamart.lux

sealed class Async<out I, out O> {
    object Idle : Async<Nothing, Nothing>()
    data class Executing<I>(val input: I) : Async<I, Nothing>()
    data class Failure(val reason: Throwable) : Async<Nothing, Nothing>()
    data class Success<O>(val result: O) : Async<Nothing, O>()
}