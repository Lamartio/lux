package io.lamart.optics.source

import arrow.core.Either
import arrow.core.identity
import arrow.optics.Optional
import arrow.optics.Setter

interface SourcedOptional<S, A> : Sourced<S>, SourcedSetter<S, A> {
    val optional: Optional<S, A>

    fun getOrModify(): Either<S, A> =
        optional.getOrModify(source.get())

    override fun modify(map: (focus: A) -> A): S =
        optional.getOrModify(source.get()).fold(::identity) { a -> set(map(a)) }

    fun getOrNull(): A? =
        optional.getOrNull(source.get())

    fun setNullable(focus: A): S? =
        optional.setNullable(source.get(), focus)

    fun modifyNullable(map: (focus: A) -> A): S? =
        optional.modifyNullable(source.get(), map)

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