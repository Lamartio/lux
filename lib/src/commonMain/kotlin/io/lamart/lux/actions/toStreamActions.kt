package io.lamart.lux.actions

import io.lamart.lux.Async
import io.lamart.lux.Signal
import io.lamart.lux.Stream
import io.lamart.lux.focus.FocusedSetter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold

val <I : Any, O : Any> FocusedSetter<*, Stream<I, O>>.toStreamActions: ActionsFactory<I, O, Stream<I, O>>
    get() = ActionsFactory(
        onStart = { flow ->
            flow
                .runningFold<Signal<I, O>, Stream<I, O>>(Stream()) { stream, signal ->
                    when (signal) {
                        is Signal.Start -> Stream(state = Async.Executing(signal.input))
                        is Signal.Next -> stream.copy(result = signal.output)
                        is Signal.End -> signal.reason
                            ?.let { stream.copy(state = Async.Failure(it)) }
                            ?: stream.copy(state = Async.Success(Unit))
                    }
                }
                .onEach(::set)
        },
        onStop = { reason ->
            modify { stream ->
                when (stream.state) {
                    is Async.Executing -> stream.copy(state = Async.Failure(reason))
                    else -> stream
                }
            }
        },
        onReset = { set(Stream()) }
    )