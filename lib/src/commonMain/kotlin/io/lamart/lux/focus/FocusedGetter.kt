package io.lamart.tenx.lux.focus

import arrow.optics.Getter
import io.lamart.lux.Mutable

interface FocusedGetter<S: Any, A> : Focused<S> {
    val getter: Getter<S, A>

    fun get(): A = source.get().let(getter::get)

    fun <B> compose(other: Getter<A, B>): FocusedGetter<S, B> {
        return object : FocusedGetter<S, B> {
            override val source: Mutable<S> = this@FocusedGetter.source
            override val getter: Getter<S, B> = this@FocusedGetter.getter.compose(other)
        }
    }

    operator fun <B> plus(other: Getter<A, B>) = compose(other)

}

fun <A, T : FocusedGetter<*, A>> T.record(block: T.() -> Unit): Pair<A, A> {
    val before = get()
    block(this)
    val after = get()

    return before to after
}