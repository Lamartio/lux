package io.lamart.lux.actions

import io.lamart.lux.Behavior
import io.lamart.lux.Signal
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow

class ActionsFactory<I : Any, O, A> internal constructor(
    val onStart: (Flow<Signal<I, O>>) -> Flow<A>,
    val onStop: (Throwable) -> Unit,
    val onReset: () -> Unit
) {

    operator fun invoke(
        scope: CoroutineScope,
        behavior: Behavior<I, O>,
        effect: (Flow<A>) -> Flow<*> = { it }
    ): Actions<I> {
        val channel = Channel<I>()
        val actionsJob = scope.coroutineContext[Job].let(::SupervisorJob).let { parent ->
            object : Job by parent {
                override fun cancel(cause: CancellationException?) {
                    parent.cancel(cause)
                    channel.cancel(cause)
                }
            }
        }

        return object : Actions<I>, Job by actionsJob {
            val scope = scope + actionsJob
            var job: Job = Job().apply { cancel() }

            override fun start(input: I) {
                if (!job.isActive) {
                    job = channel
                        .receiveAsFlow()
                        .let(behavior)
                        .let(onStart)
                        .let(effect)
                        .launchIn(this.scope)
                }

                this.scope.plus(job).launch {
                    channel.send(input)
                }
            }

            override fun stop() {
                if (job.isActive) {
                    val reason = CancellationException("Action is stopped")

                    job.cancel(reason)
                    onStop(reason)
                }
            }

            override fun reset() {
                if (!job.isActive)
                    onReset()
            }
        }
    }

}