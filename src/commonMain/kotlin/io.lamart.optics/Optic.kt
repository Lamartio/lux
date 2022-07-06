package io.lamart.optics

import kotlin.js.JsName

sealed class Optic<S, F> {
    data class Test<S, F>(val transform: (S) -> Sequence<F>) : Optic<S, S>()

    data class Lens<S, F>(val select: S.() -> F, val clone: S.(focus: F) -> S) : Optic<S, F>() {

        @JsName("composeLens")
        fun <R> compose(lens: Lens<F, R>): Lens<S, R> {
            return Lens(
                select = { this.let(select).let(lens.select) },
                clone = { lens.clone(select(this), it).let { this.clone(it) } }
            )
        }

        @JsName("composeMask")
        fun <R> compose(mask: Mask<F, R>): Mask<S, R> =
            Mask(
                select = { this.let(select).let(mask.select) },
                clone = { r ->
                    select(this)
                        .let { f -> mask.clone(f, r) }
                        .let { f -> clone(this, f) }
                }
            )
    }

    data class Mask<S, F>(val select: S.() -> Option<F>, val clone: S.(focus: F) -> S) : Optic<S, F>() {

        @JsName("composeLens")
        fun <R> compose(lens: Lens<F, R>): Mask<S, R> =
            Mask(
                select = { this.let(select).map(lens.select) },
                clone = { r ->
                    select(this)
                        .map { f -> lens.clone(f, r) }
                        .map { f -> clone(this, f) }
                        .get(or = this)
                }
            )

        @JsName("composeMask")
        fun <R> compose(mask: Mask<F, R>): Mask<S, R> =
            Mask(
                select = { this.let(select).flatMap(mask.select) },
                clone = { r ->
                    select(this)
                        .map { f -> mask.clone(f, r) }
                        .map { f -> clone(this, f) }
                        .get(or = this)
                }
            )
    }


}
