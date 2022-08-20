package io.lamart.optics.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn

typealias Effect<T> = CoroutineScope.(output: Flow<T>) -> Flow<*>

fun <T> effectOf(vararg effects: Effect<T>): Effect<T> {
    return when (effects.size) {
        0 -> { output -> output }
        1 -> effects[0]
        else -> { output ->
            output
                .shareIn(this, SharingStarted.Lazily)
                .let { flow -> effects.map { effect -> effect(flow) } }
                .merge()
        }
    }
}
