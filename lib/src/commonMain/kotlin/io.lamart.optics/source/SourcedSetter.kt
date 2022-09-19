package io.lamart.optics.source

import arrow.optics.Setter

interface SourcedSetter<S, A> : Sourced<S> {

    val setter: Setter<S, A>

    fun modify(map: (focus: A) -> A): S =
        source.modify { setter.modify(it, map) }

    fun set(focus: A): S =
        source.modify { setter.set(it, focus) }

    fun lift(map: (focus: A) -> A): () -> S =
        { source.modify(setter.lift(map)) }

    infix fun <B> compose(other: Setter<A, B>): SourcedSetter<S, B> =
        SourcedSetter(source, setter.compose(other))

    companion object
}

operator fun <S, A> SourcedSetter.Companion.invoke(source: Source<S>, setter: Setter<S, A>): SourcedSetter<S, A> =
    object : SourcedSetter<S, A> {
        override val source: Source<S> = source
        override val setter: Setter<S, A> = setter
    }