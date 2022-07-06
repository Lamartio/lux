package io.lamart.optics

fun <T> optionOf(value: T): Option<T> = Option.Some(value)

fun <T> optionOfNone(): Option<T> = Option.None()

fun <T> optionOfNullable(value: T?): Option<T> =
    when (value) {
        null -> optionOfNone()
        else -> optionOf(value)
    }