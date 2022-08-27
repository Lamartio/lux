package io.lamart.optics.async

import arrow.core.Option
import arrow.core.none
import arrow.core.some
import io.lamart.optics.Pipe
import io.lamart.optics.source.SourcedSetter
import io.lamart.optics.toPipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*

data class Stream<out T>(
    val state: Async<Unit> = idle(),
    val value: Option<T> = none()
) {
    companion object
}

fun <P, T> Stream.Companion.actions(
    stream: SourcedSetter<*, Stream<T>>,
    behavior: Behavior<P, T>,
    scope: CoroutineScope,
    effect: Effect<Stream<T>>,
    pipe: Pipe<P>
): Actions<P> =
    Actions(
        scope,
        pipe,
        onCancel = { stream.compose { source, map -> source.copy(state = map(source.state)) }.set(failure(it)) },
        onReset = { stream.modify { if (it.state.isSuccess or it.state.isFailure) Stream() else it } },
        onExecute = { output ->
            output
                .let(behavior)
                .map(::next)
                .onCompletion { reason -> emit(reason.toSignal()) }
                .runningFold(Stream(), ::combine)
                .onEach(stream::set)
                .let { effect(scope, it) }
        }
    )

private fun <T> combine(stream: Stream<T>, signal: Signal<T>): Stream<T> =
    when (signal) {
        is Signal.Complete -> stream.copy(state = signal.state)
        is Signal.Next -> {
            when (val state = signal.state) {
                Idle -> stream.copy(state = idle(), value = none())
                Executing -> stream.copy(state = executing(), value = none())
                is Failure -> stream.copy(state = failure(state.reason))
                is Success -> stream.copy(value = state.result.some())
            }
        }
    }

private sealed class Signal<T> {
    data class Next<T>(val state: Async<T>) : Signal<T>()
    data class Complete<T>(val state: Async<Unit>) : Signal<T>()
}

private fun <T> next(value: Async<T>): Signal<T> = Signal.Next(value)

private fun <T> Throwable?.toSignal(): Signal<T> =
    when (this) {
        null -> Signal.Complete(success(Unit))
        else -> Signal.Complete(failure(this))
    }

fun <P, T> SourcedSetter<*, Stream<T>>.toStream(
    behavior: Behavior<P, T>,
    scope: CoroutineScope = GlobalScope,
    effect: Effect<Stream<T>> = effectOf()
): Actions<P> =
    Stream.actions(this, behavior, scope, effect, MutableSharedFlow<P>().toPipe())