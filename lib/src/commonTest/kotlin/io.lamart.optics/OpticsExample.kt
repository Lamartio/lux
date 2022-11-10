package io.lamart.optics

import arrow.core.identity
import io.lamart.optics.async.Async
import io.lamart.optics.async.switching
import io.lamart.optics.async.toActions
import io.lamart.optics.source.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest

class OpticsExample {
    private data class State(
        val person: Person = Person(),
        val auth: Auth = Auth.NotAuthenticated,
        val authenticating: Async<String> = Async.idle()
    ) {
        companion object : OpticsFactory<State> {
            val person = lensOf({ person }, { copy(person = it) })
            val auth = lensOf({ auth }, { copy(auth = it) })
        }
    }

    private data class Person(
        val name: String? = null,
        val age: Int = -1,
        val friends: List<Person> = emptyList()
    ) {
        companion object : OpticsFactory<Person> {
            val name = lensOf({ name }, { copy(name = it) })
            val friends = lensOf({ friends }, { copy(friends = it) })
        }
    }

    private sealed class Auth {
        object NotAuthenticated : Auth()
        data class Authenticated(val user: Person) : Auth() {
            companion object : OpticsFactory<Authenticated> {
                val user = lensOf({ user }, { copy(user = it) })
            }
        }

        companion object : OpticsFactory<Auth> {
            val notAuthenticated = prismOf<NotAuthenticated>()
            val authenticated = prismOf<Authenticated>()
        }
    }

    private val source = State()
        .let(::MutableStateFlow)
        .toSource()

    private val name by source.compose(State.person).compose(Person.name).toProperty()

    fun name() {
        val source = source + State.person + Person.name
        val name by source.toProperty()
    }

    fun friend() {
        val friend: SourcedOptional<State, Person> = source
            .compose(State.person)
            .compose(Person.friends)
            .compose(listOptionalOf { name == "Danny" })
    }

    fun friends() {
        val friends: SourcedSetter<State, Person> = source
            .plus(State.person)
            .plus(Person.friends)
            .compose(listTraversalOf { filter { it.name == "Danny" } })
    }

    fun userName() {
        var userName by source
            .compose(State.auth)
            .compose(Auth.authenticated)
            .compose(Auth.Authenticated.user)
            .compose(Person.name)
            .toNullableProperty()
    }

    fun authenticating() = runTest {
        val action = source
            .compose(lensOf({ authenticating }, { copy(authenticating = it) }))
            .toActions(
                behavior = switching(suspension = ::identity),
                scope = this
            )

        action.execute()
    }
}