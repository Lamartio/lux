package io.lamart.lux

interface Actions<I> {
    fun start(input: I)
    fun stop()
    fun reset()
    fun cancel()
}

