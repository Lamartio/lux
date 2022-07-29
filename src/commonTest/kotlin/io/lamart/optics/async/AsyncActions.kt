package io.lamart.optics.async

import io.lamart.optics.sourced.SourcedSetter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AsyncActions<P, T>(
    private val source: SourcedSetter<*, Async<T>>,
    private val strategy: Strategy<P, T>,
    private val scope: CoroutineScope = GlobalScope,
    private val effect: (Async<T>) -> Unit = {}
) {

    private val flow = MutableSharedFlow<P>()
    private var job: Job = scope.async(start = CoroutineStart.LAZY, block = {}).apply { cancel() }

    fun cancel() {
        val reason = Cancelled()

        if (job.isActive)
            job.cancel(reason)

        source.set(failure(reason))
    }

    fun reset() {
        if (!job.isActive)
            source.set(idle())
    }

    fun next(payload: P) {
        if (scope.isActive) {
            if (!job.isActive)
                job = launch()

            flow.tryEmit(payload)
        }
    }

    private fun launch(): Job =
        flow
            .strategy()
            .map(::success)
            .catch { emit(failure(it)) }
            .onStart { emit(executing()) }
            .onEach(source::set)
            .onEach(effect)
            .launchIn(scope)

    class Cancelled : CancellationException("User cancelled current strategy")
}

fun <P, T> SourcedSetter<*, Async<T>>.toAsyncActions(
    strategy: Strategy<P, T>,
    scope: CoroutineScope = GlobalScope,
    effect: (Async<T>) -> Unit = {}
): AsyncActions<P, T> = AsyncActions(this, strategy, scope, effect)