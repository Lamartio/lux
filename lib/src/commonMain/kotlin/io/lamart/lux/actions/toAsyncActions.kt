package io.lamart.lux.actions

import io.lamart.lux.Async
import io.lamart.lux.Signal
import io.lamart.lux.focus.FocusedSetter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

val <I : Any, O : Any> FocusedSetter<*, Async<I, O>>.toAsyncActions: ActionsFactory<I, O, Async<I, O>>
    get() = ActionsFactory(
        onStart = { flow ->
            flow
                .map { signal ->
                    when {
                        signal is Signal.Start -> Async.Executing(signal.input)
                        signal is Signal.Next -> Async.Success(signal.output)
                        signal is Signal.End && signal.reason != null -> Async.Failure(signal.reason)
                        else -> null
                    }
                }
                .filterNotNull()
                .onEach(::set)
        },
        onStop = { reason ->
            modify { async ->
                if (async is Async.Executing) Async.Failure(reason)
                else async
            }
        },
        onReset = { set(Async.Idle()) }
    )