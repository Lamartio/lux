package io.lamart.lux

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.jvm.JvmName

interface Behavior<I, O> : (Flow<I>) -> Flow<Signal<I, O>> {

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    companion object {

        fun <I, O> create(flow: suspend (I) -> Flow<O>, transform: Flow<I>.(suspend (I) -> Flow<Signal<I, O>>) -> Flow<Signal<I, O>>): Behavior<I, O> =
            object : Behavior<I, O> {
                override fun invoke(inputFlow: Flow<I>): Flow<Signal<I, O>> =
                    transform(inputFlow) { input ->
                        flow(input)
                            .map<O, Signal<I, O>> { Signal.Next(it) }
                            .catch { emit(Signal.End(it)) }
                            .prepend(Signal.Start(input))
                            .append(Signal.End(null))
                            .distinctUntilChanged { old, new -> old is Signal.End && new is Signal.End } // make sure only 1 end is sent.
                    }
            }

        fun <I, O> from(suspension: suspend (I) -> O): suspend (I) -> Flow<O> =
            { flowOf(it).map(suspension) }

        @JvmName("exhaustingSuspension")
        fun <I, O> exhausting(suspension: suspend (I) -> O): Behavior<I, O> =
            exhausting(flow = from(suspension))

        @JvmName("exhaustingFlow")
        fun <I, O> exhausting(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            create(flow, { flatMapMerge(concurrency = 1, it) })

        @JvmName("mergingSuspension")
        fun <I, O> merging(suspension: suspend (I) -> O): Behavior<I, O> =
            merging(flow = from(suspension))

        @JvmName("mergingFlow")
        fun <I, O> merging(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            create(flow, { flatMapMerge(concurrency = DEFAULT_CONCURRENCY, it) })

        @JvmName("concattingSuspension")
        fun <I, O> concatting(suspension: suspend (I) -> O): Behavior<I, O> =
            concatting(flow = from(suspension))

        @JvmName("concattingFlow")
        fun <I, O> concatting(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            create(flow, { flatMapConcat(it) })

        @JvmName("switchingSuspension")
        fun <I, O> switching(suspension: suspend (I) -> O): Behavior<I, O> =
            switching(flow = from(suspension))

        @JvmName("switchingFlow")
        fun <I, O> switching(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            create(flow, { flatMapLatest(it) })

    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun <T> Flow<T>.prepend(vararg values: T): Flow<T> =
    listOf(flowOf(*values), this).asFlow().flattenConcat()

@OptIn(ExperimentalCoroutinesApi::class)
private fun <T> Flow<T>.append(vararg values: T): Flow<T> =
    listOf(this, flowOf(*values)).asFlow().flattenConcat()