package io.lamart.optics.source

import arrow.core.Either
import arrow.optics.*
import io.lamart.optics.readWritePropertyOf
import kotlin.properties.ReadWriteProperty

interface SourcedLens<S, A> : Sourced<S>, SourcedOptional<S, A>, SourcedGetter<S, A>, SourcedSetter<S, A> {

    val lens: Lens<S, A>

    override fun getOrModify(): Either<S, A> =
        lens.getOrModify(source.get())

    infix fun <B> compose(other: Lens<A, B>): SourcedLens<S, B> =
        SourcedLens(source, lens.compose(other))

    companion object
}

operator fun <S, A> SourcedLens.Companion.invoke(source: Source<S>, lens: Lens<S, A>): SourcedLens<S, A> =
    object : SourcedLens<S, A> {
        override val source: Source<S> = source
        override val lens: Lens<S, A> = lens
        override val optional: Optional<S, A> = lens
        override val setter: Setter<S, A> = lens
        override val getter: Getter<S, A> = lens
        override val fold: Fold<S, A> = lens
    }

fun <T, S, A> SourcedLens<S, A>.asProperty(): ReadWriteProperty<T, A> =
    readWritePropertyOf(this::get, this::set)