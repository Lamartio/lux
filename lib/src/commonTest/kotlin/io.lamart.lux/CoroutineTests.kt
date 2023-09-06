package io.lamart.lux

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTests {

    @Test
    fun `An error in the middle of a flow can be caught externally`() = runTest {
        var result: Throwable? = null
        val scope = this
            .plus(Job()) // required to let coroutines know that error should not be delegated to parent
            .plus(CoroutineExceptionHandler { _, throwable -> result = throwable })

        flowOf(0, 1, 2)
            .map { if (it < 2) it else throw Throwable("TOO BIG!") }
            .launchIn(scope)

        advanceUntilIdle()
        assertNotNull(result)
    }
}