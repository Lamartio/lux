package io.lamart.optics.async

sealed class State<out T>

val State<*>.isSuccess: Boolean
    get() = when (this) {
        is Success -> true
        else -> false
    }
val State<*>.isFailure: Boolean
    get() = when (this) {
        is Failure -> true
        else -> false
    }
val State<*>.isExecuting: Boolean
    get() = when (this) {
        is Executing -> true
        else -> false
    }

fun <T> idle(): State<T> = Idle
fun <T> executing(): State<T> = Executing
fun <T> failure(reason: Throwable): State<T> = Failure(reason)
fun <T> success(result: T): State<T> = Success(result)

object Idle : State<Nothing>()
object Executing : State<Nothing>()
data class Failure(val reason: Throwable) : State<Nothing>()
data class Success<T>(val result: T) : State<T>()