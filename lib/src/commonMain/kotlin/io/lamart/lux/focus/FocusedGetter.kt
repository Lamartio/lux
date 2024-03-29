package io.lamart.lux.focus

import arrow.optics.Getter
import io.lamart.lux.Mutable

interface FocusedGetter<S, A> : Focused<S> {
    val getter: Getter<S, A>

    fun get(): A = source.get().let(getter::get)

    fun <B> compose(other: Getter<A, B>): FocusedGetter<S, B> =
        object : FocusedGetter<S, B> {
            override val source: Mutable<S> = this@FocusedGetter.source
            override val getter: Getter<S, B> = this@FocusedGetter.getter.compose(other)
        }

    operator fun <B> plus(other: Getter<A, B>) = compose(other)

}