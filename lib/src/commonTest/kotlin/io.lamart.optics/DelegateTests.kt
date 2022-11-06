package io.lamart.optics

import arrow.core.getOrElse
import arrow.core.some
import io.lamart.optics.source.asNullableProperty
import io.lamart.optics.source.asProperty
import io.lamart.optics.source.toSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class DelegateTests {

    private data class State(val person: Person)
    private data class Person(val name: String)

    private val source = State(person = Person(name = "Danny"))
        .let(::MutableStateFlow)
        .toSource()

    @Test
    fun getter() {
        val person by source.compose(getterOf { person }).asProperty()

        assertEquals("Danny", person.name)
    }

    @Test
    fun lens() {
        var person by source.compose(lensOf({ person }, { copy(person = it) })).asProperty()

        assertEquals("Danny", person.name)
        person = Person(name = person.name.reversed())
        assertEquals("ynnaD", person.name)
    }

    @Test
    fun optional() {
        var person by source.compose(optionalOf({ person }, { copy(person = it) })).asProperty()
        val getName: () -> String = { person.map { it.name }.getOrElse { "" } }

        assertEquals("Danny", getName())
        person = person.map { Person(name = it.name.reversed()) }
        assertEquals("ynnaD", getName())
    }

    @Test
    fun nullable() {
        var person by source.compose(optionalOf({ person }, { copy(person = it) })).asNullableProperty()

        assertEquals("Danny", person?.name)
        person = Person(name = person?.name?.reversed().orEmpty())
        assertEquals("ynnaD", person?.name)
    }
}