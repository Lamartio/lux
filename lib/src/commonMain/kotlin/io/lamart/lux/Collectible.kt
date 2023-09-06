package io.lamart.lux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class Collectible<S : Any>(
    private val scope: CoroutineScope,
    private val flow: Flow<S>
) {

    fun collect(onEach: (S) -> Unit, onCompletion: (Throwable?) -> Unit): () -> Unit =
        flow
            .onEach { onEach(it) }
            .onCompletion { onCompletion(it) }
            .launchIn(scope)
            .asCancel()

}

private fun Job.asCancel(): () -> Unit = { cancel(null) }