package io.lamart.optics.source

interface Source<S> {
    fun get(): S
    fun set(source: S)
}

