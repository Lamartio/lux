package io.lamart.optics.source

import arrow.optics.Fold
import arrow.optics.Getter
import io.lamart.optics.readOnlyPropertyOf
import kotlin.properties.ReadOnlyProperty

interface SourcedGetter<S, A> : Sourced<S>, SourcedFold<S,A> {

    val getter: Getter<S, A>

    fun get(): A =
        getter.get(source.get())

    infix fun <B> compose(other: Getter<A, B>): SourcedGetter<S, B> =
        SourcedGetter(source, getter.compose(other))

    operator fun <B> plus(other: Getter<A, B>): SourcedGetter<S, B> =
        compose(other)

    companion object
}

operator fun <S, A> SourcedGetter.Companion.invoke(source: Source<S>, getter: Getter<S, A>): SourcedGetter<S, A> =
    object : SourcedGetter<S, A>, Sourced<S> by Sourced(source) {
        override val getter: Getter<S, A> = getter
        override val fold: Fold<S, A> = getter
    }

fun <A> SourcedGetter<*, A>.toProperty(): ReadOnlyProperty<Any?, A> =
    readOnlyPropertyOf(this::get)