package io.lamart.lux.docs

import io.lamart.lux.Async
import io.lamart.lux.Behavior
import io.lamart.lux.Machine
import io.lamart.lux.actions.toAsyncActions
import io.lamart.lux.docs.Credentials
import io.lamart.lux.docs.Token
import io.lamart.lux.focus.FocusedLens
import io.lamart.lux.focus.lensOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals


data class Credentials(val name: String, val pass: String)

@JvmInline
value class Token(val value: String?)

data class State(val isSigningIn: Async<Credentials, Token> = Async.Idle())

class AwesomeApi {
   suspend fun signIn(credentials: Credentials): Token {
       delay(2000)
       return Token("MyPrettyToken")
   }
}

class Actions(scope: CoroutineScope, api: AwesomeApi, focus: FocusedLens<State, State>) {
    private val signInActions = focus
        .compose(lensOf(State::isSigningIn, { copy(isSigningIn = it) }))
        .toAsyncActions(
            scope,
            Behavior.switching(suspension = api::signIn)
        )

    fun signIn(name: String, pass: String) =
        signInActions.start(Credentials(name, pass))
}

class Asynchronicity {

    @Test
    fun test() = runTest {
        val job = Job()
        val machine = Machine(
            value = State(),
            actionsFactory = { scope, focus -> Actions(scope, AwesomeApi(), focus) },
            scope = this + job
        )
        val result = mutableListOf<Async<Credentials, Token>>()

        launch(job) {
            machine.map { it.isSigningIn }.toList(result)
        }

        machine.actions.signIn("namey", "passey")
        advanceUntilIdle()
        job.cancel()

        assertEquals(result.size, 3)
    }

    companion object
}