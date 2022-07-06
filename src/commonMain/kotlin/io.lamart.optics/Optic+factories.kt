package io.lamart.optics

fun <S, F> lensOf(select: S.() -> F, clone: S.(focus: F) -> S): Optic.Lens<S, F> =
    Optic.Lens(select, clone)

fun <S, F> maskOf(select: S.() -> Option<F>, clone: S.(focus: F) -> S): Optic.Mask<S, F> =
    Optic.Mask(select, clone)

fun <S, F : S> prismOf(select: S.() -> Option<F>): Optic.Mask<S, F> =
    maskOf(select, clone = { it })

fun <S, F : S> prismOfNullable(select: (S) -> F?): Optic.Mask<S, F> =
    prismOf { this.let(select).let(::optionOfNullable) }

fun <S> prismOfNullable(): Optic.Mask<S?, S> =
    prismOfNullable(::identity)
