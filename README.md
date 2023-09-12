# Lux ✨

###### Shiny state management for Kotlin, Android & iOS

Applications running on immutable state have proven themselves to be predictable and stable. The web pioneered with Flux
and Redux with great success, but I found that both consists of more pieces then necessary.

It should be a state, a function that mutates/copies the state and a utility for supporting to the underlying graphical
engine (be it Android, iOS, or web).

These need to be segregated/categorized when we are working on big apps or with big teams. And of course we need tools
to deal with asynchronicity.

## Getting started

Each application starts with a state that it can render. With al mighty examples we start with implementing a count that
we can increment and decrement.

```kotlin
data class AppState(val count: Int = 0)
```

Simple and immutable, just what we like in stable and predictable app development. Now we need functions that can make a
copy of our state with an incremented of decremented value for `count`.

```kotlin
class AppActions(focus: FocusedLens<*, AppState>) {

    // Lens is a placeholder that holds info of how to get and set a property
    private val lens: Lens<AppState, Int> = lensOf(
        get = AppState::count,
        set = { copy(count = it) } // expanded: { value -> this.copy(count = value) }
    )

    // apply the Lens to another to create a 'path' to the desired property
    private val countFocus: FocusedLens<*, Int> = focus.compose(lens)

    // get, set, or modify the property and it will update the owner
    fun increment() = countFocus.modify { it + 1 }
    fun decrement() = countFocus.modify { it - 1 }
}
```

The above is an expanded version of what we would use in real scenarios. Check the example below for something more
practical:

```kotlin
class AppActionsCondensed(focus: FocusedLens<*, AppState>) {
    private val countFocus = focus.compose(lensOf(AppState::count, { copy(count = it) }))

    fun increment() = countFocus.modify { it + 1 }
    fun decrement() = countFocus.modify { it - 1 }
}
```

Having defined both state and actions, it is good practise to join them into a single object which we can use in our
applications. The `Machine` provides us this functionality and include the batteries for operating in Android and iOS.

```kotlin
class AppMachine() : Machine<AppState, AppActions>(
    value = AppState(),
    actionsFactory = { _, focus -> AppActions(focus) },
)
```

Still not that hard right? To prove all is working we can write tests in a synchronous matter or, for a more practical
representation, in an asynchronous matter.

```kotlin
@Test
fun incrementAndDecrement() {
    val machine = AppMachine()

    assertEquals(machine.value.count, 0)
    machine.actions.increment()
    assertEquals(machine.value.count, 1)
    machine.actions.decrement()
    assertEquals(machine.value.count, 0)
}

@Test
fun incrementAndDecrementWithFlow() = runTest {
    val machine = AppMachine(this)
    val results = mutableListOf<AppState>()
    val job = launch { machine.toList(results) } // Machine implements StateFlow 🚀

    advanceUntilIdle()
    machine.actions.increment()
    advanceUntilIdle()
    machine.actions.decrement()
    advanceUntilIdle()
    job.cancel()

    assertEquals(results.map(AppState::count), listOf(0, 1, 0))
}
```

That... is... it! We created our application's state, the actions to mutate the state and a container that is usable
within our application. By now you probably wonder how this scales and if asynchronicity is supported. Head over to the
wiki to find out!

P.S. These examples are available as tests, so you can run them yourself ;)

# Backlog

| TODO                              | DOING | DONE                                               |
|-----------------------------------|-------|----------------------------------------------------|
|                                   |       | Fix synchronicity bug in default Machine and tests |
| Implement collectible in Machine. |       |                                                    |
