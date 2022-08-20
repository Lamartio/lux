package io.lamart.optics.async

import io.lamart.optics.source.SourcedSetter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class Actions<P, T> internal constructor(
    val source: SourcedSetter<*, State<T>>,
    val behavior: Behavior<P, T>,
    val scope: CoroutineScope,
    val effect: Effect<State<T>>,
    val emit: suspend (P) -> Unit,
    val getFlow: () -> Flow<P>
) {
    private var job: Job = Job().apply { cancel() }

    fun cancel() {
        if (scope.isActive and job.isActive) {
            val reason = BehaviorCancelled()

            job.cancel(reason)
            source.set(failure(reason))
        }
    }

    fun reset() {
        if (scope.isActive and !job.isActive)
            source.modify { if (it.isSuccess or it.isFailure) idle() else it }
    }

    fun execute(vararg payloads: P) {
        if (scope.isActive) {
            if (job.isActive) {
                scope.launch {
                    payloads.forEach { emit(it) }
                }
            } else {
                job = getFlow()
                    .onStart { payloads.forEach { emit(it) } }
                    .let(behavior)
                    .onEach(source::set)
                    .let { effect(scope, it) }
                    .launchIn(scope)
            }
        }
    }

    class BehaviorCancelled internal constructor() : CancellationException("User cancelled current behavior") {
        override fun equals(other: Any?): Boolean =
            other is BehaviorCancelled || super.equals(other)

        override fun hashCode(): Int =
            super.hashCode()

    }
}

fun <P, T> SourcedSetter<*, State<T>>.toAsyncActions(
    behavior: Behavior<P, T>,
    scope: CoroutineScope = GlobalScope,
    effect: Effect<State<T>> = effectOf()
): Actions<P, T> =
    MutableSharedFlow<P>().let { flow -> Actions(this, behavior, scope, effect, flow::emit, flow::asSharedFlow) }

