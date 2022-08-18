package io.lamart.optics

import arrow.core.Either
import arrow.core.Option
import arrow.core.firstOrNone
import arrow.optics.*
import io.lamart.optics.source.Source
import io.lamart.optics.source.invoke

operator fun <S, A> PLens.Companion.get(get: S.() -> A, set: S.(A) -> S): Lens<S, A> = invoke(get, set)

operator fun <S, A> POptional.Companion.get(getOrModify: (source: S) -> Either<S, A>, set: S.(A) -> S): Optional<S, A> =
    invoke(getOrModify, set)

operator fun <A> PPrism.Companion.invoke(): Prism<A?, A> =
    PPrism(
        getOrModify = { Option.fromNullable(it).toEither { it } },
        reverseGet = ::identity
    )

operator fun <A, B : A> PPrism.Companion.invoke(transform: (A) -> B?): Prism<A, B> =
    PPrism(
        getOrModify = { it.let(transform).let(Option.Companion::fromNullable).toEither { it } },
        reverseGet = ::identity
    )

fun <T> MutableList<T>.toSource(): Source<T> = Source(::last, ::add)

fun <A> POptional.Companion.first(predicate: (focus: A) -> Boolean): Optional<List<A>, A> =
    POptional(
        getOrModify = { source -> source.firstOrNone(predicate).toEither { source } },
        set = { source, focus ->
            source
                .indexOfFirst(predicate)
                .let { index ->
                    when (index) {
                        -1 -> source
                        else -> source.toMutableList().apply { set(index, focus) }
                    }
                }
        }
    )

fun <A> PEvery.Companion.list(predicate: (focus: A) -> Boolean): Every<List<A>, A> =
    Lens
        .invoke<List<A>, List<A>, List<A>, List<A>>(
            get = { source -> source.filter(predicate) },
            set = { source, focus ->
                focus
                    .toMutableList()
                    .run { source.map { if (predicate(it)) removeFirst() else it } }
            }
        )
        .compose(Every.list())