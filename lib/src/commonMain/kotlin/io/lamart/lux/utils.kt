package io.lamart.lux

import kotlinx.coroutines.flow.*

fun <T> Flow<T>.record(initial: T): Flow<Pair<T, T>> =
    this
        .map { it to it }
        .runningFold(initial to initial) { (_, old), (new) -> old to new }
        .drop(1)

fun <T> Flow<T>.record(): Flow<Pair<T, T>> =
    this
        .map { it to it }
        .runningReduce { (_, old), (new) -> old to new }
        .drop(1)
