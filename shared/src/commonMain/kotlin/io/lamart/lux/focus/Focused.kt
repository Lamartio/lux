package io.lamart.tenx.lux.focus

import io.lamart.lux.Mutable

interface Focused<S: Any> {
    val source: Mutable<S>
}