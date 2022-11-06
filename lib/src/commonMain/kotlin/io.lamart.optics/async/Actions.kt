package io.lamart.optics.async

import io.lamart.optics.Pipe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart

class Actions<P>(
    private val scope: CoroutineScope,
    private val pipe: Pipe<P>,
    private val onCancel: (reason: Throwable) -> Unit,
    private val onReset: () -> Unit,
    private val onExecute: (Flow<P>) -> Flow<*>
) {
    private var job: Job = Job().apply { cancel() }

    fun cancel() {
        if (scope.isActive and job.isActive) {
            val reason = CancellationException("User cancelled current behavior")

            job.cancel(reason)
            onCancel(reason)
        }
    }

    fun reset() {
        if (scope.isActive and !job.isActive)
            onReset()
    }

    fun execute(vararg payload: P) {
        if (scope.isActive) {
            if (job.isActive) {
                scope.launch { payload.forEach { pipe.input(it) } }
            } else {
                job = pipe.output
                    .onStart { payload.forEach { emit(it) } }
                    .let(onExecute)
                    .launchIn(scope)
            }
        }
    }
}