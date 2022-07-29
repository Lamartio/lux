package io.lamart.optics

import arrow.optics.Every
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Traversal
import io.lamart.optics.async.Async
import io.lamart.optics.async.exhausting
import io.lamart.optics.async.switching
import io.lamart.optics.async.toAsyncActions
import io.lamart.optics.sourced.SourcedSetter
import io.lamart.optics.sourced.component1
import io.lamart.optics.sourced.component2
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.test.Test
import kotlin.test.assertEquals

data class State(
    val person: Person = Person(),
    val persons: List<Person> = listOf(Person(name = "Danny", age = 31), Person(name = "Petra")),
    val randomPersons: Collection<Person> = setOf(Person(name = "Danny", age = 31), Person(name = "Petra")),
    val auth: Auth? = Auth.NotSignedIn,
    val signingIn: Async<Person> = Async.idle()
) {
    companion object {
        val person: Lens<State, Person> = Lens[{ person }, { copy(person = it) }]
        val persons = Lens.get<State, List<Person>>({ persons }, { copy(persons = it) })
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
        val results = mutableListOf(State())

        val (_, setName) = results
            .toSource()
            .compose(Lens[{ person }, { copy(person = it) }])
            .compose(Lens[{ name }, { copy(name = it) }])

        setName("Danny!!!")

        assertEquals(2, results.size)
        assertEquals("Danny", results[0].person.name)
        assertEquals("Danny!!!", results[1].person.name)
    }

    @Test
    fun test2() {
        val first = State()
        val second = State.person.compose(Person.name).set(first, "ynnaD")
        val third = State.person.compose(Person.name).modify(first) { "$it!!!" }

        assertEquals("Danny", first.person.name)
        assertEquals("ynnaD", second.person.name)
        assertEquals("Danny!!!", third.person.name)
    }

    @Test
    fun test3() {
        val first = State()
        val second = State.person.compose(Person.name).set(first, "ynnaD")
        val third = State.person.compose(Person.name).modify(first) { "$it!!!" }

        assertEquals("Danny", first.person.name)
        assertEquals("ynnaD", second.person.name)
        assertEquals("Danny!!!", third.person.name)
    }

    @Test
    fun test4() {
        val results = mutableListOf(State())
        val source = results.toSource()

        assertEquals("Danny", source.get().person.name)
        source.set(State(person = Person(name = "ynnaD")))
        assertEquals("ynnaD", source.get().person.name)

        source.compose(State.person).compose(Person.name).set("Danny!!!")
        assertEquals("Danny!!!", source.get().person.name)
        assertEquals(3, results.size)
    }

    @Test
    fun test5() {
        val first = State()
        val second = State
            .persons
            .compose(Every.list())
            .compose(Person.name)
            .set(first, "Danny!!!")

        assertEquals(listOf(Person("Danny!!!", 31), Person("Danny!!!")), second.persons)
    }

    @Test
    fun test6() {
        val first = State()
        val second = State
            .persons
            .compose(Traversal { source, map -> source.map(map) })
            .compose(Person.name)
            .set(first, "Danny!!!")
        val third = State
            .persons
            .compose(Every.list { it.age != null })
            .compose(Person.name)
            .modify(first, String::reversed)
        val fourth = State
            .persons
            .compose(Optional.first { it.age == null })
            .compose(Person.name)
            .modify(first, String::reversed)

        assertEquals(listOf(Person("Danny!!!", 31), Person("Danny!!!")), second.persons)
        assertEquals(listOf(Person("ynnaD", 31), Person("Petra")), third.persons)
        assertEquals(listOf(Person("Danny", 31), Person("arteP")), fourth.persons)
    }
}