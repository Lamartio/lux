package io.lamart.optics.source

interface Sourced<S> {

    val source: Source<S>

    companion object
}

operator fun <S> Sourced.Companion.invoke(source: Source<S>): Sourced<S> =
    object : Sourced<S> {
        override val source: Source<S> = source
    }