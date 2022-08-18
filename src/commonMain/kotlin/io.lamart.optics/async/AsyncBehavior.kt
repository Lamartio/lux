package io.lamart.optics.async

import kotlinx.coroutines.flow.*
import kotlin.jvm.JvmName

typealias AsyncBehavior<P, T> = (input: Flow<P>) -> Flow<Async<T>>

@JvmName("concattingSuspension")
fun <P, T> concatting(suspension: suspend (P) -> T): AsyncBehavior<P, T> =
    concatting(flow = { flowOf(suspension(it)) })

@JvmName("concattingFlow")
fun <P, T> concatting(flow: suspend (P) -> Flow<T>): AsyncBehavior<P, T> =
    asyncBehaviorOf { flatMapConcat(flow) }

@JvmName("mergingSuspension")
fun <P, T> merging(suspension: suspend (P) -> T): AsyncBehavior<P, T> =
    merging(flow = { flowOf(suspension(it)) })

@JvmName("mergingFlow")
fun <P, T> merging(flow: suspend (P) -> Flow<T>): AsyncBehavior<P, T> =
    asyncBehaviorOf { flatMapMerge(transform = flow) }

@JvmName("switchingSuspension")
fun <P, T> switching(suspension: suspend (P) -> T): AsyncBehavior<P, T> =
    switching(flow = { flowOf(suspension(it)) })

@JvmName("switchingFlow")
fun <P, T> switching(flow: suspend (P) -> Flow<T>): AsyncBehavior<P, T> =
    asyncBehaviorOf { flatMapLatest(flow) }

@JvmName("exhaustingSuspension")
fun <P, T> exhausting(suspension: suspend (P) -> T): AsyncBehavior<P, T> =
    exhausting(flow = { flowOf(suspension(it)) })

@JvmName("exhaustingFlow")
fun <P, T> exhausting(flow: suspend (P) -> Flow<T>): AsyncBehavior<P, T> =
    asyncBehaviorOf {
        val isBusy = MutableStateFlow(false)

        this@asyncBehaviorOf
            .filter { !isBusy.getAndUpdate { true } }
            .flatMapMerge(
                concurrency = 1,
                transform = { flow(it).onCompletion { isBusy.update { false } } }
            )
    }

fun <P, T> asyncBehaviorOf(block: Flow<P>.() -> Flow<T>): AsyncBehavior<P, T> {
    return { input ->
        input
            .run(block)
            .map(::success)
            .onFailure { emit(failure(it)) }
            .onStart { emit(executing()) }
    }
}

private fun <T> Flow<T>.onFailure(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit): Flow<T> =
    onCompletion { error ->
        error?.let { action(this, it) }
    }