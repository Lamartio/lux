package io.lamart.optics.source

import arrow.optics.Getter

interface SourcedGetter<S, A> : Sourced<S> {
    val getter: Getter<S, A>

    fun get(): A =
        getter.get(source.get())

    companion object
}

operator fun <S, A> SourcedGetter.Companion.invoke(source: Source<S>, getter: Getter<S, A>): SourcedGetter<S, A> {
    return object : SourcedGetter<S, A>, Sourced<S> by Sourced(source) {
        override val getter: Getter<S, A> = getter
        override fun get(): A = source.get().let { getter.get(it) }
    }
}