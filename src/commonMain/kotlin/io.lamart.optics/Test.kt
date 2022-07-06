package io.lamart.optics

fun interface Getter<S, A> {
    fun get(source: S): A

    infix fun <B> compose(other: Getter<A, B>): Getter<S, B> =
        Getter { it.let(::get).let(other::get) }
}

fun interface Setter<S, A> {
    fun modify(source: S, transform: (focus: A) -> A): S
    fun set(source: S, focus: A): S =
        modify(source) { focus }

    fun lift(map: (focus: A) -> A): (source: S) -> S =
        { s -> modify(s) { map(it) } }

    infix fun <B> compose(other: Setter<A, B>): Setter<S, B> =
        Setter { s, fb -> modify(s) { a -> other.modify(a, fb) } }
}