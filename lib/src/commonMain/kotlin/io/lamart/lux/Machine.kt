package io.lamart.lux

import io.lamart.lux.focus.FocusedLens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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

    constructor(
        value: S,
        actionsFactory: (CoroutineScope, FocusedLens<S, S>) -> A,
        scope: CoroutineScope? = null,
    ) : this(initialize(value, scope, actionsFactory))

    @JvmName("composeState")
    fun <T : Any> compose(state: (S) -> T): Machine<T, A> =
        Machine(scope, this.state.compose(state), actions)

    @JvmName("composeActions")
    fun <B : Any> compose(actions: (A) -> B): Machine<S, B> =
        Machine(scope, state, this.actions.let(actions))

    fun <T : Any, B : Any> compose(state: (S) -> T, actions: (A) -> B): Machine<T, B> =
        Machine(scope, this.state.compose(state), this.actions.let(actions))

    fun collect(onEach: (S) -> Unit, onCompletion: (Throwable?) -> Unit): () -> Unit =
        state
            .onEach { onEach(it) }
            .onCompletion { onCompletion(it) }
            .launchIn(scope)
            .asCancel()

    companion object
}

private fun Job.asCancel(): () -> Unit = { cancel(null) }

private fun <S : Any, A : Any> initialize(
    value: S,
    scope: CoroutineScope?,
    actionsFactory: (CoroutineScope, FocusedLens<S, S>) -> A
): Machine<S, A> {
    val state = MutableStateFlow(value)
    val mutable = state.toMutable()
    val scope = scope ?: CoroutineScope(Dispatchers.Main + SupervisorJob())
    val actions = actionsFactory(scope, mutable.lens)

    return Machine(scope, state, actions)
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
