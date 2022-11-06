package io.lamart.optics

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal fun <T, V> readOnlyPropertyOf(get: () -> V): ReadOnlyProperty<T, V> =
    ReadOnlyProperty { _, _ -> get() }

internal fun <T, V> readWritePropertyOf(get: () -> V, set: (value: V) -> Unit): ReadWriteProperty<T, V> =
    object : ReadWriteProperty<T, V> {
        override fun getValue(thisRef: T, property: KProperty<*>): V = get()
        override fun setValue(thisRef: T, property: KProperty<*>, value: V) = set(value)
    }