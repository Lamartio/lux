package io.lamart.tenx.lux.focus

import arrow.optics.Setter
import io.lamart.lux.Mutable

interface FocusedSetter<S: Any, A> : Focused<S> {
    val setter: Setter<S, A>

    fun set(focus: A) = source.modify { setter.set(it, focus) }

    fun modify(map: (focus: A) -> A) = source.modify { setter.modify(it, map) }

    fun <B> compose(other: Setter<A, B>): FocusedSetter<S, B> {
        return object : FocusedSetter<S, B> {
            override val source: Mutable<S> = this@FocusedSetter.source
            override val setter: Setter<S, B> = this@FocusedSetter.setter.compose(other)
        }
    }

    operator fun <B> plus(other: Setter<A, B>) = compose(other)

}