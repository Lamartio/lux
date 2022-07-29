package io.lamart.optics.source

import arrow.core.Either
import arrow.optics.Getter
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Setter

interface SourcedLens<S, A> : Sourced<S>, SourcedOptional<S, A>, SourcedGetter<S, A>, SourcedSetter<S, A> {
    val lens: Lens<S, A>

    override fun getOrModify(): Either<S, A> =
        Either.Right(get())

    companion object
}

operator fun <S, A> SourcedLens.Companion.invoke(source: Source<S>, lens: Lens<S, A>): SourcedLens<S, A> =
    object : SourcedLens<S, A> {
        override val source: Source<S> = source
        override val lens: Lens<S, A> = lens
        override val optional: Optional<S, A> = lens
        override val setter: Setter<S, A> = lens
        override val getter: Getter<S, A> = lens
    }