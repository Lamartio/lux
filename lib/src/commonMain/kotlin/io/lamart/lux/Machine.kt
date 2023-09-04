package io.lamart.lux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.jvm.JvmName

open class Machine<S : Any, A : Any>(
    private val scope: CoroutineScope,
    private val state: StateFlow<S>,
    val actions: A
) : StateFlow<S> by state {

    constructor(machine: Machine<S, A>) : this(
        machine.scope,
        machine.state,
        machine.actions
    )

    @JvmName("composeState")
    fun <T : Any> compose(state: (S) -> T): Machine<T, A> =
        Machine(scope, this.state.compose(state), actions)

    @JvmName("composeActions")
    fun <B : Any> compose(actions: (A) -> B): Machine<S, B> =
        Machine(scope, state, this.actions.let(actions))

    fun <T : Any, B : Any> compose(state: (S) -> T, actions: (A) -> B): Machine<T, B> =
        Machine(scope, this.state.compose(state), this.actions.let(actions))

    /**
     * Errors can be collected through CoroutineExceptionHandler in the CoroutineScope
     * @return a cancel function
     */
    fun collect(onEach: (S) -> Unit, onCompletion: () -> Unit): () -> Unit =
        state
            .onEach(onEach)
            .onCompletion { onCompletion() }
            .launchIn(scope)
            .run { { cancel(null) } }

    companion object
}

private fun <T, R> StateFlow<T>.compose(transform: (value: T) -> R): StateFlow<R> =
    object : StateFlow<R>, Flow<R> by this@compose.map(transform) {
        override val replayCache: List<R>
            get() = this@compose.replayCache.map(transform)

        override val value: R
            get() = this@compose.value.let(transform)

        override suspend fun collect(collector: FlowCollector<R>): Nothing =
            this@compose.collect { value -> value.let(transform).let { collector.emit(it) } }
    }

