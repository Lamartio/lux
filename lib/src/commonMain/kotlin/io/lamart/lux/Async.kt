package io.lamart.lux

sealed class Async<I: Any, O: Any> {
    data class Idle<I : Any, O : Any> private constructor(val unit: Unit) : Async<I, O>() {
        constructor() : this(Unit)
    }

    data class Executing<I : Any, O : Any>(val input: I) : Async<I, O>()
    data class Failure<I : Any, O : Any>(val reason: Throwable) : Async<I, O>()
    data class Success<I : Any, O : Any>(val result: O) : Async<I, O>()

    fun asIdle(): Idle<I, O>? = this as? Idle
    fun asExecuting(): Executing<I, O>? = this as? Executing<I, O>
    fun asFailure(): Failure<I, O>? = this as? Failure
    fun asSuccess(): Success<I, O>? = this as? Success<I, O>
}