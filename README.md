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
val user32 = EasyFFM[User32::class]
val keyState = user32.GetKeyState(KeyEvent.VK_SPACE)
println("Key state: $keyState")
```
