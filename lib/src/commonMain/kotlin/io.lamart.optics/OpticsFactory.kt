package io.lamart.optics

import arrow.core.*
import arrow.optics.*
import kotlin.reflect.KClass

interface OpticsFactory<S> {

    fun <A> lensOf(get: S.() -> A, set: S.(A) -> S): Lens<S, A> =
        Lens(get, set)

    fun <A> optionalOf(get: S.() -> A?, set: S.(A) -> S): Optional<S, A> =
        Optional(getOption = { it.let(get).toOption() }, set)

    @Suppress("UNCHECKED_CAST")
    fun <A : S> prismOf(): Prism<S, A> =
        Prism(getOption = { Option.fromNullable(it as? A) }, ::identity)

}

fun <S, A> getterOf(get: S.() -> A): Getter<S, A> =
    Getter(get)

fun <S, A> lensOf(get: S.() -> A, set: S.(A) -> S): Lens<S, A> =
    Lens(get, set)

fun <S, A> optionalOf(get: S.() -> A?, set: S.(A) -> S): Optional<S, A> =
    Optional(getOption = { it.let(get).toOption() }, set)

fun <S, A : S> prismOf(get: (S) -> A?): Prism<S, A> =
    Prism(getOption = { it.let(get).toOption() }, ::identity)

inline fun <T, reified R : T> cast(): (T) -> R? =
    { it as? R }

@Suppress("UNCHECKED_CAST")
fun <T : Any, R : T> cast(type: KClass<R>): (T) -> R? =
    { value -> value.takeIf(type::isInstance)?.let { it as? R } }

fun <A> listOptionalOf(predicate: A.() -> Boolean): Optional<List<A>, A> =
    Optional(
        getOption = { source -> source.firstOrNone(predicate) },
        set = { list, element ->
            list
                .firstOrNone(predicate)
                .map(list::indexOf)
                .filter { it != -1 }
                .map { index -> list.toMutableList().apply { set(index, element) } }
                .getOrElse { list }
        }
    )

fun <K, A> mapOptionalOf(predicate: Map.Entry<K, A>.() -> Boolean): Optional<Map<K, A>, A> =
    Optional(
        getOption = { source -> source.entries.firstOrNone(predicate).map { it.value } },
        set = { map, element ->
            map
                .entries
                .firstOrNone(predicate)
                .map { (key) -> map.toMutableMap().apply { put(key, element) } }
                .getOrElse { map }
        }
    )

fun <A> setOptionalOf(predicate: A.() -> Boolean): Optional<Set<A>, A> =
    Optional({ source -> source.firstOrNone(predicate) }, { collection, element ->
        collection
            .firstOrNone(predicate)
            .map {
                collection
                    .toMutableSet()
                    .apply {
                        remove(it)
                        add(element)
                    }
            }
            .getOrElse { collection }
    })

fun <A> listTraversalOf(transform: List<A>.() -> List<A> = ::identity): Traversal<List<A>, A> =
    Traversal { source, map -> source.let(transform).map(map) }

fun <A> setTraversalOf(transform: Set<A>.() -> Set<A> = ::identity): Traversal<Set<A>, A> =
    Traversal { source, map -> source.let(transform).map(map).toSet() }

fun <K, A> mapTraversalOf(transform: Map<K, A>.() -> Map<K, A> = ::identity): Traversal<Map<K, A>, A> =
    Traversal { source, map -> source.let(transform).mapValues { (_, value) -> map(value) } }