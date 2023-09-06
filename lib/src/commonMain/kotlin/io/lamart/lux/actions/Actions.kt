package io.lamart.lux.actions

interface Actions<I: Any> {
    fun start(input: I)
    fun stop()
    fun reset()
    fun cancel()

    open class Instance<I : Any>(actions: Actions<I>): Actions<I> by actions
}

