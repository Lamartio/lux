package io.lamart.optics.async

sealed class Async<out T>

fun <T> idle(): Async<T> = Idle
fun <T> executing(): Async<T> = Executing
fun <T> failure(reason: Throwable): Async<T> = Failure(reason)
fun <T> success(result: T): Async<T> = Success(result)

object Idle : Async<Nothing>()
object Executing : Async<Nothing>()
data class Failure(val reason: Throwable) : Async<Nothing>()
data class Success<T>(val result: T) : Async<T>()