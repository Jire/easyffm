# easyFFM

_Easy declarative foreign function & memory API, utilizing Java 19's FFM preview_

Currently a work-in-progress. Snapshots only. Not for use in production.

### Define a library and functions

```kotlin
interface User32 {
    fun GetKeyState(virtKey: Int): Short
}
```

### Use a library's functions

```kotlin
val user32 = foreignLibrary(User32::class)
val keyState = user32.GetKeyState(KeyEvent.VK_SPACE)
println("Key state: $keyState")
```

### Define a struct

```kotlin
@FieldOrder(["x", "y"])
interface Point : ForeignStruct {
    var x: Int
    var y: Int
}
```

### Create a struct and use it

```kotlin
val point = foreignStruct(Point::class)
point.y = 6
println("x=${point.x}   y=${point.y}") // x=0   y=6
```

### You can use structs in functions, too

```kotlin
interface Mouse {
    fun position(): Point
    fun move(point: Point)
}
```

### Structs support arrays and nesting

```kotlin
@FieldOrder(["points"])
interface Points : ForeignStruct {
    @StructArray(10) // array size
    val points: Array<Point>
}
```
