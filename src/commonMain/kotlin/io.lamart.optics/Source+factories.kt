package io.lamart.optics

fun <F> certainOf(get: () -> F, set: (F) -> Unit): Source.Certain<F> =
    Source.Certain(get, set)

fun <F> uncertainOf(get: () -> Option<F>, set: (F) -> Unit): Source.Uncertain<F> =
    Source.Uncertain(get, set)