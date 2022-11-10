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
    val state: Async<Unit> = Async.idle(),
    val value: Option<T> = none()
) {
    companion object {
        fun <T> idle(value: Option<T> = none()): Stream<T> = Stream(state = Async.idle(), value = value)
        fun <T> executing(value: Option<T> = none()): Stream<T> = Stream(state = Async.executing(), value = value)
        fun <T> failure(reason: Throwable, value: Option<T> = none()): Stream<T> =
            Stream(state = Async.failure(reason), value = value)

        fun <T> success(value: Option<T> = none()): Stream<T> = Stream(state = Async.success(Unit), value = value)
    }
}

fun <P, T> Stream.Companion.actionsOf(
    source: SourcedSetter<*, Stream<T>>,
    behavior: Behavior<P, T>,
    scope: CoroutineScope,
    effect: Effect<Stream<T>>,
    pipe: Pipe<P>
): Actions<P> =
    Actions(
        scope,
        pipe,
        onCancel = { source.compose { stream, map -> stream.copy(state = map(stream.state)) }.set(Async.failure(it)) },
        onReset = { source.modify { if (it.state.isSuccess or it.state.isFailure) idle() else it } },
        onExecute = { output ->
            output
                .let(behavior)
                .map(Signal.Companion::next)
                .onCompletion { reason ->
                    reason
                        .let<Throwable?, Signal<T>>(Signal.Companion::complete)
                        .let { emit(it) }
                }
                .runningFold(Stream(), ::combine)
                .drop(1)
                .onEach(source::set)
                .let { effect(scope, it) }
        }
    )

private fun <T> combine(stream: Stream<T>, signal: Signal<T>): Stream<T> =
    when (signal) {
        is Signal.Complete -> stream.copy(state = signal.state)
        is Signal.Next -> {
            when (val state = signal.state) {
                Idle -> stream.copy(state = Async.idle(), value = none())
                Executing -> stream.copy(state = Async.executing(), value = none())
                is Failure -> stream.copy(state = Async.failure(state.reason))
                is Success -> stream.copy(value = state.result.some())
            }
        }
    }

private sealed class Signal<T> {
    data class Next<T>(val state: Async<T>) : Signal<T>()
    data class Complete<T>(val state: Async<Unit>) : Signal<T>()

    companion object {
        fun <T> next(value: Async<T>): Signal<T> = Next(value)
        fun <T> complete(reason: Throwable?): Signal<T> =
            when (reason) {
                null -> Complete(Async.success(Unit))
                else -> Complete(Async.failure(reason))
            }
    }
}

fun <P, T> SourcedSetter<*, Stream<T>>.toActions(
    behavior: Behavior<P, T>,
    scope: CoroutineScope = GlobalScope,
    effect: Effect<Stream<T>> = effectOf()
): Actions<P> =
    Stream.actionsOf(this, behavior, scope, effect, MutableSharedFlow<P>().toPipe())