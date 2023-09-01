package io.lamart.lux

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.jvm.JvmName

interface Behavior<I, O> : (Flow<I>) -> Flow<Signal<I, O>> {

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    companion object {

        @JvmName("exhaustingSuspension")
        fun <I, O> exhausting(suspension: suspend (I) -> O): Behavior<I, O> =
            exhausting(flow = from(suspension))

        @JvmName("exhaustingFlow")
        fun <I, O> exhausting(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            flow.compose({ flatMapMerge(concurrency = 1, it) })

        @JvmName("mergingSuspension")
        fun <I, O> merging(suspension: suspend (I) -> O): Behavior<I, O> =
            merging(flow = from(suspension))

        @JvmName("mergingFlow")
        fun <I, O> merging(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            flow.compose({ flatMapMerge(concurrency = DEFAULT_CONCURRENCY, it) })

        @JvmName("concattingSuspension")
        fun <I, O> concatting(suspension: suspend (I) -> O): Behavior<I, O> =
            concatting(flow = from(suspension))

        @JvmName("concattingFlow")
        fun <I, O> concatting(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            flow.compose({ flatMapConcat(it) })

        @JvmName("switchingSuspension")
        fun <I, O> switching(suspension: suspend (I) -> O): Behavior<I, O> =
            switching(flow = from(suspension))

        @JvmName("switchingFlow")
        fun <I, O> switching(flow: suspend (I) -> Flow<O>): Behavior<I, O> =
            flow.compose({ flatMapLatest(it) })

    }
}

private fun <I, O> from(suspension: suspend (I) -> O): suspend (I) -> Flow<O> =
    { flowOf(it).map(suspension) }

private fun <I, O> (suspend (I) -> Flow<O>).compose(transform: Flow<I>.(suspend (I) -> Flow<Signal<I, O>>) -> Flow<Signal<I, O>>): Behavior<I, O> =
    object : Behavior<I, O> {
        override fun invoke(inputFlow: Flow<I>): Flow<Signal<I, O>> =
            transform(inputFlow) { input ->
                this@compose(input)
                    .map<O, Signal<I, O>> { Signal.Next(it) }
                    .catch { emit(Signal.End(it)) }
                    .prepend(Signal.Start(input))
                    .append(Signal.End(null))
                    .distinctUntilChanged { old, new -> old is Signal.End && new is Signal.End } // make sure only 1 end is sent.
            }
    }

