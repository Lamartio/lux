package io.lamart.optics

import io.lamart.optics.async.Effect
import io.lamart.optics.async.effectOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EffectTests {

    @Test
    fun test1() = runTest {
        val results = mutableListOf<Int>()
        val scope = this + Dispatchers.Unconfined
        val elements = listOf(1, 2)
        val flow = elements.asFlow().shareIn(this, SharingStarted.Lazily)

        flow.take(elements.size).onEach(results::add).launchIn(scope)
        flow.take(elements.size).onEach(results::add).launchIn(scope)
        advanceUntilIdle()

        assertEquals(expected = listOf(1, 1, 2, 2), results)
    }

    @Test
    fun test2() = runTest {
        val results = mutableListOf<Int>()
        val scope = this + Dispatchers.Unconfined
        val elements = listOf(1, 2)
        val flow = elements.asFlow().shareIn(this, SharingStarted.Lazily)
        val effect = testEffect(elements.size, results)

        flow.take(elements.size).onEach(results::add).launchIn(scope)
        flow.let { effect(scope,it) }.launchIn(scope)
        advanceUntilIdle()

        assertEquals(expected = listOf(1, 1, 2, 2), results)
    }

    @Test
    fun test3() = runTest {
        val results = mutableListOf<Int>()
        val scope = this + Dispatchers.Unconfined
        val elements = listOf(1, 2)
        val flow = elements.asFlow().shareIn(this, SharingStarted.Lazily)
        val effect = testEffect(elements.size, results)

        flow.let { effect(scope,it) }.launchIn(scope)
        flow.let { effect(scope,it) }.launchIn(scope)
        advanceUntilIdle()

        assertEquals(expected = listOf(1, 1, 2, 2), results)
    }

    private fun testEffect(count: Int, results: MutableCollection<Int>): Effect<Int> =
        { it.take(count).onEach(results::add) }

    @Test
    fun test4() = runTest {
        val results = mutableListOf<Int>()
        val scope = this + Job() + Dispatchers.Unconfined
        val elements = listOf(1, 2)
        val flow = elements.asFlow().shareIn(this, SharingStarted.Lazily)
        val effect: Effect<Int> = effectOf(
            testEffect(elements.size, results),
            testEffect(elements.size, results),
        )

        flow.let { effect(scope,it) }.launchIn(scope)
        advanceUntilIdle()

        assertEquals(expected = listOf(1, 1, 2, 2), results)
        scope.cancel()
    }

}