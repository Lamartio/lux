package io.lamart.optics

fun <T> identity(value: T): T {
    return value
}

fun <T, R> it(transform: (T) -> R): T.() -> R = transform

fun <T, R> `this`(transform: T.() -> R): (T) -> R = transform