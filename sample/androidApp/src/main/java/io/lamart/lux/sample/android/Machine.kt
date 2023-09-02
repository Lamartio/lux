package io.lamart.lux.sample.android

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class Machine<S, A>(
    val state: StateFlow<S>,
    val actions: A
) : StateFlow<S> by state {

    @JvmName("composeState")
    fun <T> compose(state: (S) -> T): Machine<T, A> =
        Machine(this.state.compose(state), actions)

    @JvmName("composeActions")
    fun <B> compose(actions: (A) -> B): Machine<S, B> =
        Machine(state, actions(this@Machine.actions))

    fun <T, B> compose(state: (S) -> T, actions: (A) -> B): Machine<T, B> =
        Machine(this.state.compose(state), this.actions.let(actions))

    fun <T, B> compose(both: Pair<(S) -> T, (A) -> B>): Machine<T, B> =
        both.let { (state, actions) -> compose(state, actions) }

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

