package io.lamart.lux

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.optics.Getter
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.Prism
import arrow.optics.Setter
import io.lamart.tenx.lux.focus.FocusedLens
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf

fun <S, A> lensOf(get: S.() -> A, set: S.(A) -> S): Lens<S, A> {
    return Lens.invoke(get, set)
}

fun <S : Any> prismOfNull(): Prism<S?, S> =
    Prism(
        getOrModify = { Option.fromNullable(it).toEither { it } },
        reverseGet = { it } // the use of ::identity will cause the compiler to get stuck (linkdebugframework)
    )

fun <S, A : S> prismOf(cast: (source: S) -> A?): Prism<S, A> = Prism(
    getOrModify = { it.let(cast).let(Option.Companion::fromNullable).toEither { it } },
    reverseGet = { it }
)

inline fun <S : Any, reified A : S> prism(): Prism<S, A> =
    object : Prism<S, A> {
        override fun getOrModify(source: S): Either<S, A> =
            (source as? A)?.right() ?: source.left()

        override fun reverseGet(focus: A): S = focus
    }

fun <S, A> ((S) -> A).lens(copy: S.(A) -> S): Lens<S, A> {
    return PLens(
        get = this,
        set = copy
    )
}

fun <S : Any, A> MutableStateFlow<S>.compose(lens: Lens<S, A>): FocusedLens<S, A> =
    object : FocusedLens<S, A> {
        override val source: Mutable<S> = Mutable(
            get = { this@compose.value },
            set = { this@compose.value = it }
        )
        override val getter: Getter<S, A> = lens
        override val setter: Setter<S, A> = lens
        override val optional: Optional<S, A> = lens
        override val lens: Lens<S, A> = lens
    }


@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Flow<T>.prepend(vararg values: T): Flow<T> =
    listOf(flowOf(*values), this).asFlow().flattenConcat()

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Flow<T>.append(vararg values: T): Flow<T> =
    listOf(this, flowOf(*values)).asFlow().flattenConcat()