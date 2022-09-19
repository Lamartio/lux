package io.lamart.optics.source

import arrow.optics.Getter
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Setter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Source<S> {

    fun get(): S

    fun set(source: S)

    fun <A> compose(getter: Getter<S, A>): SourcedGetter<S, A> =
        SourcedGetter(this, getter)

    fun <A> compose(setter: Setter<S, A>): SourcedSetter<S, A> =
        SourcedSetter(this, setter)

    fun <A> compose(optional: Optional<S, A>): SourcedOptional<S, A> =
        SourcedOptional(this, optional)

    fun <A> compose(lens: Lens<S, A>): SourcedLens<S, A> =
        SourcedLens(this, lens)

    companion object
}

operator fun <S> Source.Companion.invoke(get: () -> S, set: (source: S) -> Unit): Source<S> =
    object : Source<S> {
        override fun get(): S = get()
        override fun set(source: S) = set(source)
    }

operator fun <T, S> Source<S>.getValue(thisRef: T, property: KProperty<*>): S = get()
operator fun <T,S> Source<S>.setValue(thisRef: T, property: KProperty<*>, value: S) = set(value)
