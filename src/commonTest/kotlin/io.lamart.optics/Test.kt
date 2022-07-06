package io.lamart.optics

import kotlin.test.assertEquals

class Test {

    @kotlin.test.Test
    fun certainPath() {
        val (source, results) = testCertainOf(State())
        val (getName, setName) = source
            .compose(lensOf({ person }, { copy(person = it) }))
            .compose(lensOf({ name }, { copy(name = it) }))

        assertEquals("Danny", getName())
        setName("Petra")
        assertEquals("Petra", getName())
        assertEquals(2, results.size)
    }

    @kotlin.test.Test
    fun uncertainPath() {
        val (source, results) = testCertainOf(State())
        val (getName, setName) = source
            .compose(lens = lensOf({ auth }, { copy(auth = it) }))
            .compose(mask = prismOfNullable())
            .compose(mask = prismOfNullable { it as? Auth.SignedIn })
            .compose(lens = lensOf({ name }, { copy(name = it) }))

        assertEquals("Danny", getName().get(or = ""))
        setName("Petra")
        assertEquals("Petra", getName().get(or = ""))
        assertEquals(2, results.size)
    }

}

fun <F> testCertainOf(value: F): Pair<Source.Certain<F>, List<F>> {
    val results = mutableListOf(value)
    val source = Source.Certain(get = results::last, set = results::add)

    return source to results
}

data class State(
    val person: Person = Person(),
    val persons: List<Person> = emptyList(),
    val auth: Auth? = Auth.NotSignedIn
)

data class Person(val name: String = "Danny")

sealed class Auth {
    object NotSignedIn : Auth()
    data class SignedIn(val name: String = "Danny") : Auth()
}