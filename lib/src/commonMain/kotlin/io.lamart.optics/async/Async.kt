package io.lamart.optics.async

import io.lamart.optics.Pipe
import io.lamart.optics.source.SourcedSetter
import io.lamart.optics.toPipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach

sealed class Async<out T> {
    val isIdle: Boolean get() = this is Idle
    val isExecuting: Boolean get() = this is Executing
    val isFailure: Boolean get() = this is Failure
    val isSuccess: Boolean get() = this is Success

    companion object {
        fun <T> idle(): Async<T> = Idle
        fun <T> executing(): Async<T> = Executing
        fun <T> failure(reason: Throwable): Async<T> = Failure(reason)
        fun <T> success(result: T): Async<T> = Success(result)
    }
}

object Idle : Async<Nothing>()
object Executing : Async<Nothing>()
data class Failure(val reason: Throwable) : Async<Nothing>()
data class Success<T>(val result: T) : Async<T>()

fun <P, T> Async.Companion.actionsOf(
    source: SourcedSetter<*, Async<T>>,
    behavior: Behavior<P, T>,
    scope: CoroutineScope,
    effect: Effect<Async<T>>,
    pipe: Pipe<P>
): Actions<P> =
    Actions(
        scope,
        pipe,
        onCancel = { source.set(failure(it)) },
        onReset = { source.modify { if (it.isSuccess or it.isFailure) idle() else it } },
        onExecute = { output ->
            output
                .let(behavior)
                .onEach(source::set)
                .let { effect(scope, it) }
        }
    )

fun <P, T> SourcedSetter<*, Async<T>>.toActions(
    behavior: Behavior<P, T>,
    scope: CoroutineScope,
    effect: Effect<Async<T>> = effectOf()
): Actions<P> =
    Async.actionsOf(this, behavior, scope, effect, MutableSharedFlow<P>().toPipe())