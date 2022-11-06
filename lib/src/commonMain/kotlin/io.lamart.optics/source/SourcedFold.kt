package io.lamart.optics.source

import arrow.optics.Fold

interface SourcedFold<S, A> : Sourced<S> {

    val fold: Fold<S, A>

    fun size(): Int = fold.size(source.get())

    fun all(predicate: (focus: A) -> Boolean): Boolean =
        fold.all(source.get(), predicate)

    fun any(predicate: (focus: A) -> Boolean): Boolean =
        fold.any(source.get(), predicate)

    fun isEmpty(): Boolean =
        fold.isEmpty(source.get())

    fun isNotEmpty(): Boolean =
        fold.isNotEmpty(source.get())
}