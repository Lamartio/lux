package io.lamart.optics

import kotlinx.coroutines.flow.*

data class Comparison<T>(val old: T, val new: T = old) {
    inline fun compare(predicate: (old:T, new:T) -> Boolean): Boolean = predicate(old, new)
}

inline fun <T> Flow<Comparison<T>>.compare(crossinline predicate: (old: T, new: T) -> Boolean): Flow<Comparison<T>> =
    filter { it.compare(predicate) }

inline fun <T> Flow<T>.compare(crossinline predicate: (old: T, new: T) -> Boolean = { _,_ -> true }): Flow<Comparison<T>> =
    this
        .map(::Comparison)
        .runningReduce { (_, old), (new) -> Comparison(old, new) }
        .drop(1)
        .filter { it.compare(predicate) }

inline fun <T> Flow<T>.compare(initial: T, crossinline predicate: (old: T, new: T) -> Boolean = { _,_ -> true }): Flow<Comparison<T>> =
    this
        .runningFold(Comparison(initial)) { (_, old), new -> Comparison(old, new) }
        .filter { it.compare(predicate) }