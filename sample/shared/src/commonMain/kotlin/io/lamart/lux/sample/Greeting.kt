package io.lamart.lux.sample

import io.lamart.lux.Mutable

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}