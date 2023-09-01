package io.lamart.lux

import arrow.optics.Optional

sealed class Async<out I, out O> {
    object Idle : Async<Nothing, Nothing>()

    data class Executing<I>(val input: I) : Async<I, Nothing>() {
        companion object {
            fun <I> input() = Executing<I>::input.lens { copy(input = it) }
        }
    }

    data class Success<O>(val result: O) : Async<Nothing, O>() {
        companion object {
            fun <O> result() = Success<O>::result.lens { copy(result = it) }
        }
    }

    data class Failure(val reason: Throwable) : Async<Nothing, Nothing>() {
        companion object {
            fun reason() = Failure::reason.lens { copy(reason = it) }
        }
    }

    companion object {
        fun <I, O> idle() = prism<Async<I, O>, Idle>()
        fun <I, O> executing() = prism<Async<I, O>, Executing<I>>()
        fun <I, O> success() = prism<Async<I, O>, Success<O>>()
        fun <I, O> failure() = prism<Async<I, O>, Failure>()
    }
}
