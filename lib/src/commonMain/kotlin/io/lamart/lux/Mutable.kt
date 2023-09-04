package io.lamart.lux

import arrow.optics.Getter
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Setter
import io.lamart.lux.focus.FocusedLens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Mutable<S>(val get: () -> S, val set: (S) -> Unit) : ReadWriteProperty<Nothing?, S> {

    fun modify(map: (S) -> S): Unit =
        get().let(map).let(set)

    val lens: FocusedLens<S, S>
        get() = Lens.id<S>().let(::compose)

    infix fun <A> compose(lens: Lens<S, A>): FocusedLens<S, A> =
        object : FocusedLens<S, A> {
            override val source: Mutable<S> = this@Mutable
            override val getter: Getter<S, A> = lens
            override val setter: Setter<S, A> = lens
            override val optional: Optional<S, A> = lens
            override val lens: Lens<S, A> = lens
        }

    infix operator fun <A> plus(lens: Lens<S, A>) = compose(lens)

    override operator fun getValue(thisRef: Nothing?, property: KProperty<*>): S = get()

    override operator fun setValue(thisRef: Nothing?, property: KProperty<*>, value: S) = set(value)

    companion object {
        operator fun <S> invoke(value: S): Mutable<S> {
            var state = value
            return Mutable({ state }, { state = it })
        }
    }
}

fun <S> MutableStateFlow<S>.toMutable(): Mutable<S> =
    Mutable(::value, ::tryEmit)