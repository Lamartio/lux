package io.lamart.optics

import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.PLens
import kotlin.properties.ReadOnlyProperty
import kotlin.test.assertEquals

operator fun <S, A> PLens.Companion.get(get: S.() -> A, set: S.(A) -> S): Lens<S, A> = invoke(get, set)

class Arrow {

    @kotlin.test.Test
    fun test() {
        val (source, results) = SourcedLens(State())
        val (getName, setName) = source
            .compose(Lens[{ person }, { copy(person = it) }])
            .compose(Lens[{ name }, { copy(name = it) }])

        assertEquals("Danny", getName())
        setName("Petra")
        assertEquals("Petra", getName())
        assertEquals(2, results.size)
    }

}

operator fun <F> SourcedLens.Companion.invoke(value: F): Pair<SourcedLens<F>, List<F>> {
    val results = mutableListOf(value)
    val source = SourcedLens(get = results::last, set = results::add)

    return source to results
}

interface SourcedLens<A> {

    fun get(): A

    fun set(focus: A)

    fun <B> compose(other: Lens<A, B>): SourcedLens<B>

    operator fun component1() = ::get
    operator fun component2() = ::set

    companion object
}

operator fun <A> SourcedLens.Companion.invoke(get: () -> A, set: (A) -> Unit): SourcedLens<A> =
    SourcedLens(get, Iso(::identity, reverseGet = { it.also(set) }))

operator fun <S, A> SourcedLens.Companion.invoke(get: () -> S, lens: Lens<S, A>): SourcedLens<A> {
    return object : SourcedLens<A> {
        val source: S by ReadOnlyProperty { _, _ -> get() }

        override fun get(): A =
            lens.get(source)

        override fun set(focus: A) {
            lens.set(source, focus)
        }

        override fun <B> compose(other: Lens<A, B>): SourcedLens<B> =
            SourcedLens(get, lens.compose(other))
    }
}