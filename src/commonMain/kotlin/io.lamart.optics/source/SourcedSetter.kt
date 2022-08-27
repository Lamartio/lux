package io.lamart.optics.source

import arrow.optics.Setter
import io.lamart.optics.async.Async
import io.lamart.optics.async.Stream

interface SourcedSetter<S, A> : Sourced<S> {
    val setter: Setter<S, A>
    fun modify(map: (focus: A) -> A): S =
        setter.modify(source.get(), map).also(source::set)

    fun set(focus: A): S =
        setter.set(source.get(), focus).also(source::set)

    fun lift(map: (focus: A) -> A): () -> S =
        { setter.lift(map).invoke(source.get()).also(source::set) }

     infix fun <B> compose(other: Setter<A, B>): SourcedSetter<S, B> =
         SourcedSetter(source, setter.compose(other))

    companion object
}

operator fun <S, A> SourcedSetter.Companion.invoke(source: Source<S>, setter: Setter<S, A>): SourcedSetter<S, A> {
    return object : SourcedSetter<S, A> {
        override val source: Source<S> = source
        override val setter: Setter<S, A> = setter
    }
}