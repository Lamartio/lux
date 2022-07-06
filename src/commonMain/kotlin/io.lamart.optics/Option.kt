package io.lamart.optics

sealed class Option<T> {

    fun <R> fold(ifNone: () -> R, ifSome: (value: T) -> R): R =
        when (this) {
            is None -> ifNone()
            is Some -> ifSome(value)
        }

    fun <R> map(transform: (value: T) -> R): Option<R> =
        when (this) {
            is None -> None()
            is Some -> Some(transform(value))
        }

    fun <R> flatMap(transform: (value: T) -> Option<R>): Option<R> =
        when (this) {
            is None -> None()
            is Some -> transform(value)
        }

    operator fun get(or: T): T =
        when (this) {
            is None -> or
            is Some -> value
        }


    class None<T> : Option<T>()

    data class Some<T>(val value: T) : Option<T>()

}


