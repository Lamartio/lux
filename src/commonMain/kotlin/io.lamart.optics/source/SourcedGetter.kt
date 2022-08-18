package io.lamart.optics.source

import arrow.optics.Getter

interface SourcedGetter<S, A> : Sourced<S> {
    val getter: Getter<S, A>

    fun get(): A =
        getter.get(source.get())

    infix fun <B> compose(other: Getter<in A, out B>): SourcedGetter<S, B> =
        SourcedGetter(source, getter.compose(other))

    companion object
}

operator fun <S, A> SourcedGetter.Companion.invoke(source: Source<S>, getter: Getter<S, A>): SourcedGetter<S, A> {
    return object : SourcedGetter<S, A>, Sourced<S> by Sourced(source) {
        override val getter: Getter<S, A> = getter
        override fun get(): A = source.get().let { getter.get(it) }
    }
}