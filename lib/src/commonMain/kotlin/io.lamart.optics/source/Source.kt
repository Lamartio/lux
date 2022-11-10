package io.lamart.optics.source

import arrow.optics.Getter
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Setter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

interface Source<S> {

    fun get(): S

    fun modify(map: (source: S) -> S): S

    fun <A> compose(getter: Getter<S, A>): SourcedGetter<S, A> =
        SourcedGetter(this, getter)

    fun <A> compose(setter: Setter<S, A>): SourcedSetter<S, A> =
        SourcedSetter(this, setter)

    fun <A> compose(optional: Optional<S, A>): SourcedOptional<S, A> =
        SourcedOptional(this, optional)

    fun <A> compose(lens: Lens<S, A>): SourcedLens<S, A> =
        SourcedLens(this, lens)

    operator fun <A> plus(getter: Getter<S, A>): SourcedGetter<S, A> =
        compose(getter)

    operator fun <A> plus(setter: Setter<S, A>): SourcedSetter<S, A> =
        compose(setter)

    operator fun <A> plus(optional: Optional<S, A>): SourcedOptional<S, A> =
        compose(optional)

    operator fun <A> plus(lens: Lens<S, A>): SourcedLens<S, A> =
        compose(lens)

    companion object
}

operator fun <S> Source.Companion.invoke(get: () -> S, modify: (map: (S) -> S) -> S): Source<S> =
    object : Source<S> {
        override fun get(): S = get()
        override fun modify(map: (S) -> S): S = modify(map)
    }

fun <T> MutableStateFlow<T>.toSource(): Source<T> =
    Source(get = this::value, modify = this::updateAndGet)