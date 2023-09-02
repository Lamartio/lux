package io.lamart.lux.focus

import io.lamart.lux.Mutable

interface Focused<S> {
    val source: Mutable<S>
}