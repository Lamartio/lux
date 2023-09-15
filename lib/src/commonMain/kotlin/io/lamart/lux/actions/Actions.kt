package io.lamart.lux.actions

interface Actions<I: Any> {

    /**
     * Starts the asynchronous work with the given parameter. The `Behavior` specified through `toAsyncActions` or to `toStreamActions` determines if any running work is being cancelled.
     */
    fun start(input: I)

    /**
     * Stops current running asynchronous work. If any work was running the start will become `Failure` with a `CancellationException` as `.reason`.
     */
    fun stop()

    /**
     * Resets the state back to its `Idle` or initial value, but only if it is not executing.
     */
    fun reset()

    /**
     * Cancels the underlying coroutine, which will abort running asynchronous work and cleans up all resources associated with it.
     *
     * This is meant for destructing an actions and will render it useless! If you whish the just stop current running work, use `.stop()`.
     */
    fun cancel()

    /**
     * Simple wrapper mainly used to create concrete types for the iOS interop.
     */
    open class Instance<I : Any>(actions: Actions<I>): Actions<I> by actions
}

