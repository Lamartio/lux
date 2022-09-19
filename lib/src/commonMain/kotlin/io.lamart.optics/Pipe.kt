package io.lamart.optics

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class Pipe<T>(
    val input: suspend (T) -> Unit,
    val output: Flow<T>
)

fun <T> MutableSharedFlow<T>.toPipe(): Pipe<T> = Pipe(::emit, asSharedFlow())