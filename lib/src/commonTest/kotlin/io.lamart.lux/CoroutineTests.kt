package io.lamart.lux

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTests {

    @Test
    fun `An error in the middle of a flow can be caught externally`() = runTest {
        var result: Throwable? = null

        this
            .plus(Job()) // required to let coroutines know that error should not be delegated to parent
            .plus(CoroutineExceptionHandler { context, throwable ->
                result = throwable
                println(throwable)
            })
            .launch {
                flowOf(0, 1, 2)
                    .map { if (it < 2) it else throw Throwable("TOO BIG!") }
                    .collect()
            }

        advanceUntilIdle()
        assertNotNull(result)
    }
}