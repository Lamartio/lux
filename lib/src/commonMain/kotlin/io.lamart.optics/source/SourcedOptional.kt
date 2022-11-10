package io.lamart.optics.source

import arrow.core.Either
import arrow.core.Option
import arrow.optics.Fold
import arrow.optics.Getter
import arrow.optics.Optional
import arrow.optics.Setter
import io.lamart.optics.readWritePropertyOf
import kotlin.properties.ReadWriteProperty

interface SourcedOptional<S, A> : Sourced<S>, SourcedSetter<S, A>, SourcedFold<S,A> {

    val optional: Optional<S, A>

    fun getOrModify(): Either<S, A> =
        optional.getOrModify(source.get())

    override fun modify(map: (focus: A) -> A): S =
        source.modify { optional.modify(it, map) }

    fun getOrNull(): A? =
        optional.getOrNull(source.get())

    infix fun <B> compose(other: Optional<A, B>): SourcedOptional<S, B> =
        SourcedOptional(source, optional.compose(other))

    operator fun <B> plus(other: Optional<A, B>): SourcedOptional<S, B> =
        compose(other)

    companion object
}

fun <S,A> SourcedOptional<S,A>.get() : Option<A> =
    getOrModify().orNone()

operator fun <S, A> SourcedOptional.Companion.invoke(
    source: Source<S>,
    optional: Optional<S, A>
): SourcedOptional<S, A> =
    object : SourcedOptional<S, A> {
        override val source: Source<S> = source
        override val optional: Optional<S, A> = optional
        override val setter: Setter<S, A> = optional
        override val fold: Fold<S, A> = optional
    }

fun <A> SourcedOptional<*, A>.toProperty(): ReadWriteProperty<Any?, Option<A>> =
    readWritePropertyOf(
        get = this::get,
        set = { it.map(this::set) }
    )

fun <A> SourcedOptional<*, A>.toNullableProperty(): ReadWriteProperty<Any?, A?> =
    readWritePropertyOf(
        get = this::getOrNull,
        set = { it?.let(this::set) }
    )