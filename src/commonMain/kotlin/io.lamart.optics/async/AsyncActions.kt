package io.lamart.optics.async

import io.lamart.optics.identity
import io.lamart.optics.source.SourcedSetter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AsyncActions<P, T> internal constructor(
    val source: SourcedSetter<*, Async<T>>,
    val behavior: AsyncBehavior<P, T>,
    val scope: CoroutineScope,
    val effect: (output: Flow<Async<T>>) -> Flow<*>,
    val emit: suspend (P) -> Unit,
    val getFlow: () -> Flow<P>
) {
    private var job: Job = scope.launch(start = CoroutineStart.LAZY, block = {}).apply { cancel() }

    fun cancel() {
        val reason = BehaviorCancelled()

        if (job.isActive)
            job.cancel(reason)

        source.set(failure(reason))
    }

    fun reset() {
        if (!job.isActive)
            source.set(idle())
    }

    fun execute(payload: P) {
        if (scope.isActive) {
            if (job.isActive) {
                scope.launch {
                    emit(payload)
                }
            } else {
                job = getFlow()
                    .onStart { emit(payload) }
                    .let(behavior)
                    .onEach(source::set)
                    .let(effect)
                    .launchIn(scope)
            }
        }
    }

    class BehaviorCancelled internal constructor(): CancellationException("User cancelled current behavior")
}

fun <P, T> SourcedSetter<*, Async<T>>.toAsyncActions(
    behavior: AsyncBehavior<P, T>,
    scope: CoroutineScope = GlobalScope,
    effect: (output: Flow<Async<T>>) -> Flow<*> = ::identity
): AsyncActions<P, T> =
    MutableSharedFlow<P>().let { flow -> AsyncActions(this, behavior, scope, effect, flow::emit, flow::asSharedFlow) }