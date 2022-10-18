package io.lamart.optics

import arrow.core.Option
import arrow.core.identity
import arrow.core.toOption
import arrow.optics.*
import kotlin.reflect.KClass

fun <S, A> getterOf(get: S.() -> A): Getter<S, A> =
    Getter(get)

fun <S, A> lensOf(get: S.() -> A, set: S.(A) -> S): Lens<S, A> =
    Lens(get, set)

fun <S, A> optionalOf(get: S.() -> Option<A>, set: S.(A) -> S): Optional<S, A> =
    Optional(getOrModify = { it.let(get).toEither { it } }, set)

fun <S, A : S> prismOf(getOption: (S) -> Option<A>): Prism<S, A> =
    Prism(getOption, ::identity)

fun <T, R> nullable(map: T.() -> R?): (T) -> Option<R> =
    { map(it).toOption() }

inline fun <T, reified R : T> cast(): (T) -> Option<R> =
    { Option.fromNullable(it as? R) }

@Suppress("UNCHECKED_CAST")
inline fun <T : Any, R : T> cast(type: KClass<R>): (T) -> Option<R> =
    { it -> Option.fromNullable(if (type.isInstance(it)) it as R else null) }

//private data class State(val auth: Auth)
//
//private sealed class Auth {
//    object NotSignedIn : Auth()
//    data class SignedIn(val token: String?) : Auth()
//}
//
//private fun test(source: Source<State>) {
//    var token by source
//        .compose(lensOf({ auth }, { copy(auth = it) }))
//        .compose(prismOf(cast(Auth.SignedIn::class)))
//        .compose(optionalOf(nullable { token }, { copy(token = it) }))
//        .asNullableProperty()
//
//}