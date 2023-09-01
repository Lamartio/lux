package io.lamart.tenx.lux.focus

import arrow.optics.Getter
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Setter
import io.lamart.lux.Mutable

interface FocusedLens<S : Any , A> : Focused<S>, FocusedGetter<S, A>, FocusedSetter<S, A>,
    FocusedOptional<S, A> {
    val lens: Lens<S, A>

    fun <B> compose(other: Lens<A, B>): FocusedLens<S, B> {
        val lens = this@FocusedLens.lens.compose(other)

        return object : FocusedLens<S, B> {
            override val source: Mutable<S> = this@FocusedLens.source
            override val lens: Lens<S, B> = lens
            override val getter: Getter<S, B> = lens
            override val setter: Setter<S, B> = lens
            override val optional: Optional<S, B> = lens
        }
    }

    operator fun <B> plus(other: Lens<A,B>) = compose(other)

    class Instance<S:Any, A: Any>(focus: FocusedLens<S, A>) : FocusedLens<S, A> by focus
}