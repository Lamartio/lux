package io.lamart.optics.async

import arrow.core.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

typealias Effect<T> = CoroutineScope.(output: Flow<T>) -> Flow<*>

fun <T> effectOf(vararg effects: Effect<T>): Effect<T> =
    when (effects.size) {
        0 -> { output -> output }
        1 -> effects[0]
        else -> { output ->
            output
                .shareIn(this, SharingStarted.Lazily)
                .let { flow -> effects.map { effect -> effect(flow) } }
                .merge()
        }
    }

fun <T> compare(compare: suspend (old: T, new: T) -> Unit): Effect<T> =
    { output -> output.compare(compare) }

fun compareState(compare: suspend (old: Async<Unit>, new: Async<Unit>) -> Unit): Effect<Stream<*>> =
    { output -> output.map { it.state }.compare(compare) }

fun <T> compareValue(compare: suspend (old: Option<T>, new: Option<T>) -> Unit): Effect<Stream<T>> =
    { output -> output.map { it.value }.compare(compare) }

private fun <T> Flow<T>.compare(compare: suspend (old: T, new: T) -> Unit): Flow<Unit> =
    this
        .map { it to it }
        .runningReduce { (_, old), (new) -> old to new }
        .map { (old, new) -> compare(old, new) }