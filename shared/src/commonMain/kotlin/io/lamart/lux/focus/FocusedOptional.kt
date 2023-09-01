package io.lamart.tenx.lux.focus

import arrow.core.Either
import arrow.optics.Optional
import arrow.optics.Setter
import io.lamart.lux.Mutable

interface FocusedOptional<S: Any, A> : Focused<S>, FocusedSetter<S, A> {
    val optional: Optional<S, A>

    fun getOrModify(): Either<S, A> = optional.getOrModify(source.get())

    fun <B> compose(other: Optional<A, B>): FocusedOptional<S, B> {
        val optional = this@FocusedOptional.optional.compose(other)

        return object : FocusedOptional<S, B> {
            override val source: Mutable<S> = this@FocusedOptional.source
            override val optional: Optional<S, B> = optional
            override val setter: Setter<S, B> = optional
        }
    }

    operator fun <B> plus(other: Optional<A,B>) = compose(other)

}