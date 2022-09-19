package io.lamart.optics.source

import arrow.core.Either
import arrow.optics.Optional
import arrow.optics.Setter

interface SourcedOptional<S, A> : Sourced<S>, SourcedSetter<S, A> {

    val optional: Optional<S, A>

    fun getOrModify(): Either<S, A> =
        optional.getOrModify(source.get())

    override fun modify(map: (focus: A) -> A): S =
        source.modify { optional.modify(it, map) }

    fun getOrNull(): A? =
        optional.getOrNull(source.get())

    infix fun <B> compose(other: Optional<A, B>): SourcedOptional<S, B> =
        SourcedOptional(source, optional.compose(other))

    companion object
}

operator fun <S, A> SourcedOptional.Companion.invoke(
    source: Source<S>,
    optional: Optional<S, A>
): SourcedOptional<S, A> =
    object : SourcedOptional<S, A> {
        override val source: Source<S> = source
        override val optional: Optional<S, A> = optional
        override val setter: Setter<S, A> = optional
    }