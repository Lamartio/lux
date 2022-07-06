package io.lamart.optics

sealed class Source<T>(open val modify: ((T) -> T) -> Unit) {

    data class Certain<T>(val get: () -> T, val set: (T) -> Unit) : Source<T>({ get().let(it).let(set) }) {
        fun <R> compose(lens: Optic.Lens<T, R>): Certain<R> =
            Certain(
                get = { get().let(lens.select) },
                set = { r -> get().let { t -> lens.clone(t, r) }.let(set) }
            )

        fun <R> compose(mask: Optic.Mask<T, R>): Uncertain<R> =
            Uncertain(
                get = { mask.select(get()) },
                set = { r -> get().let { t -> mask.clone(t, r) }.let(set) }
            )
    }

    data class Uncertain<T>(val get: () -> Option<T>, val set: (T) -> Unit) : Source<T>({ get().map(it).map(set) }) {

        fun <R> compose(lens: Optic.Lens<T, R>): Uncertain<R> =
            Uncertain(
                get = { get().map(lens.select) },
                set = { r -> get().map { t -> lens.clone(t, r) }.map(set) }
            )

        fun <R> compose(mask: Optic.Mask<T, R>): Uncertain<R> =
            Uncertain(
                get = { get().flatMap(mask.select) },
                set = { r -> get().map { t -> mask.clone(t, r) }.map(set) }
            )

        fun <R> compose(test: Optic.Test<T, R>): Traversal<R> {
            return Traversal(
                get = { get().map(test.transform).get(emptySequence()) },
                modify = { transform ->
                    get().map(test.transform).get(emptySequence()).map(transform).let { set(it) }
                }
            )
        }
    }
    data class Traversal<T>(val get: () -> Sequence<T>, override val modify: ((T) -> T) -> Unit) : Source<T>(modify)

}
