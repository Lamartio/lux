@file:Suppress("NON_EXPORTABLE_TYPE", "OPT_IN_USAGE")
@file:JsExport

package io.lamart.optics

import kotlin.js.JsExport
import kotlin.js.JsName

sealed class Source<F>(open val set: (F) -> Unit) {

    fun getOr(): Option<F> =
        when (this) {
            is Certain -> optionOf(get())
            is Uncertain -> get()
        }

    fun modify(with: (F) -> F) {
        getOr()
            .map(with)
            .map(set)
    }

    operator fun component3(): (with: (F) -> F) -> Unit = ::modify

    fun <R> fold(ifCertain: (certain: Certain<F>) -> R, ifUncertain: (uncertain: Uncertain<F>) -> R): R =
        when (this) {
            is Certain -> ifCertain(this)
            is Uncertain -> ifUncertain(this)
        }

    data class Certain<F>(val get: () -> F, override val set: (F) -> Unit) : Source<F>(set) {
        @JsName("composeLens")
        fun <R> compose(lens: Optic.Lens<F, R>): Certain<R> =
            Certain(
                get = { get().let(lens.select) },
                set = { r -> get().let { f -> lens.clone(f, r) }.let(set) }
            )

        @JsName("composeMask")
        fun <R> compose(mask: Optic.Mask<F, R>): Uncertain<R> =
            Uncertain(
                get = { mask.select(get()) },
                set = { r -> get().let { f -> mask.clone(f, r) }.let(set) }
            )
    }

    data class Uncertain<F>(val get: () -> Option<F>, override val set: (F) -> Unit) : Source<F>(set) {
        @JsName("composeLens")
        fun <R> compose(lens: Optic.Lens<F, R>): Uncertain<R> =
            Uncertain(
                get = { get().map(lens.select) },
                set = { r -> get().map { f -> lens.clone(f, r) }.map(set) }
            )

        @JsName("composeMask")
        fun <R> compose(mask: Optic.Mask<F, R>): Uncertain<R> =
            Uncertain(
                get = { get().flatMap(mask.select) },
                set = { r -> get().map { f -> mask.clone(f, r) }.map(set) }
            )
    }

}
