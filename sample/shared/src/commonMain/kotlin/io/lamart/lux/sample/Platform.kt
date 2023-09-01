package io.lamart.lux.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform