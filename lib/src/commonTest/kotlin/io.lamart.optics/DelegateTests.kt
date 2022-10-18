package io.lamart.optics

import arrow.core.getOrElse
import arrow.core.some
import arrow.optics.Getter
import arrow.optics.Lens
import arrow.optics.Optional
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
        val person by source.compose(Getter { it.person }).asProperty()

        assertEquals("Danny", person.name)
    }

    @Test
    fun lens() {
        var person by source.compose(Lens({ it.person }, { s, f -> s.copy(person = f) })).asProperty()

        assertEquals("Danny", person.name)
        person = Person(name = person.name.reversed())
        assertEquals("ynnaD", person.name)
    }

    @Test
    fun optional() {
        val personSource = source.compose(Optional({ it.person.some() }, { s, f -> s.copy(person = f) }))
        var person by personSource.asProperty()
        val getName: () -> String = { person.map { it.name }.getOrElse { "" } }

        assertEquals("Danny", getName())
        person = person.map { it.name.reversed() }.getOrElse { "" }.let { Person(name = it) }.some()
        assertEquals("ynnaD", getName())
    }

    @Test
    fun nullable() {
        val personSource = source.compose(Optional({ it.person.some() }, { s, f -> s.copy(person = f) }))
        var person by personSource.asNullableProperty()

        assertEquals("Danny", person?.name)
        person = Person(name = person?.name?.reversed().orEmpty())
        assertEquals("ynnaD", person?.name)
    }
}
