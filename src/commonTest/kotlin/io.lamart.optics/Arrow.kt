package io.lamart.optics

import arrow.optics.Every
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Traversal
import io.lamart.optics.async.Async
import io.lamart.optics.async.idle
import kotlin.test.Test
import kotlin.test.assertEquals

data class TestState(
    val person: Person = Person(),
    val persons: List<Person> = listOf(Person(name = "Danny", age = 31), Person(name = "Petra")),
    val randomPersons: Collection<Person> = setOf(Person(name = "Danny", age = 31), Person(name = "Petra")),
    val auth: Auth? = Auth.NotSignedIn,
    val signingIn: Async<Person> = idle()
) {
    companion object {
        val person: Lens<TestState, Person> = Lens[{ person }, { copy(person = it) }]
        val persons = Lens.get<TestState, List<Person>>({ persons }, { copy(persons = it) })
    }
}

data class Person(val name: String = "Danny", val age: Int? = null) {
    companion object {
        val name: Lens<Person, String> = Lens[{ name }, { copy(name = it) }]
    }
}

sealed class Auth {
    object NotSignedIn : Auth()
    data class SignedIn(val person: Person = Person()) : Auth()
}

class Arrow {

    @Test
    fun test1() {
        val results = mutableListOf(TestState())
        val name = results
            .toSource()
            .compose(Lens[{ person }, { copy(person = it) }])
            .compose(Lens[{ name }, { copy(name = it) }])

        name.set("Danny!!!")

        assertEquals(2, results.size)
        assertEquals("Danny", results[0].person.name)
        assertEquals("Danny!!!", results[1].person.name)
    }

    @Test
    fun test2() {
        val first = TestState()
        val second = TestState.person.compose(Person.name).set(first, "ynnaD")
        val third = TestState.person.compose(Person.name).modify(first) { "$it!!!" }

        assertEquals("Danny", first.person.name)
        assertEquals("ynnaD", second.person.name)
        assertEquals("Danny!!!", third.person.name)
    }

    @Test
    fun test3() {
        val first = TestState()
        val second = TestState.person.compose(Person.name).set(first, "ynnaD")
        val third = TestState.person.compose(Person.name).modify(first) { "$it!!!" }

        assertEquals("Danny", first.person.name)
        assertEquals("ynnaD", second.person.name)
        assertEquals("Danny!!!", third.person.name)
    }

    @Test
    fun test4() {
        val results = mutableListOf(TestState())
        val source = results.toSource()

        assertEquals("Danny", source.get().person.name)
        source.set(TestState(person = Person(name = "ynnaD")))
        assertEquals("ynnaD", source.get().person.name)

        source.compose(TestState.person).compose(Person.name).set("Danny!!!")
        assertEquals("Danny!!!", source.get().person.name)
        assertEquals(3, results.size)
    }

    @Test
    fun test5() {
        val first = TestState()
        val second = TestState
            .persons
            .compose(Every.list())
            .compose(Person.name)
            .set(first, "Danny!!!")

        assertEquals(listOf(Person("Danny!!!", 31), Person("Danny!!!")), second.persons)
    }

    @Test
    fun test6() {
        val first = TestState()
        val second = TestState
            .persons
            .compose(Traversal { source, map -> source.map(map) })
            .compose(Person.name)
            .set(first, "Danny!!!")
        val third = TestState
            .persons
            .compose(Every.list { it.age != null })
            .compose(Person.name)
            .modify(first, String::reversed)
        val fourth = TestState
            .persons
            .compose(Optional.first { it.age == null })
            .compose(Person.name)
            .modify(first, String::reversed)

        assertEquals(listOf(Person("Danny!!!", 31), Person("Danny!!!")), second.persons)
        assertEquals(listOf(Person("ynnaD", 31), Person("Petra")), third.persons)
        assertEquals(listOf(Person("Danny", 31), Person("arteP")), fourth.persons)
    }
}