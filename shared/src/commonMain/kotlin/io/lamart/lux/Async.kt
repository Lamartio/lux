package io.lamart.lux

import arrow.optics.Optional

sealed class Async<I, O> {
    data class Idle<I, O>(val unit: Unit = Unit) : Async<I, O>()
    data class Executing<I, O>(val input: I) : Async<I, O>() {
        companion object {
            fun <I, O> input() = Executing<I, O>::input.lens { copy(input = it) }
        }
    }

    data class Success<I, O>(val result: O) : Async<I, O>() {
        companion object {
            fun <I, O> result() = Success<I, O>::result.lens { copy(result = it) }
        }
    }

    data class Failure<I, O>(val reason: Throwable) : Async<I, O>() {
        companion object {
            fun <I, O> reason() = Failure<I, O>::reason.lens { copy(reason = it) }
        }
    }

    companion object {
        fun <I, O> idle() = prism<Async<I, O>, Idle<I, O>>()
        fun <I, O> executing() = prism<Async<I, O>, Executing<I, O>>()
        fun <I, O> success() = prism<Async<I, O>, Success<I, O>>()
        fun <I, O> failure() = prism<Async<I, O>, Failure<I, O>>()
    }
}

fun <I, O> Optional<Async<I, O>, Async.Executing<I, O>>.input(): Optional<Async<I, O>, I> =
    this.compose(Async.Executing.input())

fun <I, O> Optional<Async<I, O>, Async.Success<I, O>>.result(): Optional<Async<I, O>, O> =
    this.compose(Async.Success.result())

fun <I, O> Optional<Async<I, O>, Async.Failure<I, O>>.reason(): Optional<Async<I, O>, Throwable> =
    this.compose(Async.Failure.reason())
