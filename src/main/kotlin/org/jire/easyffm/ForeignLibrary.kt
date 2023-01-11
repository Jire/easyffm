package org.jire.easyffm

import java.lang.foreign.MemorySession
import kotlin.reflect.KClass

interface ForeignLibrary {

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T : Any> foreignLibrary(
            jClass: Class<T>,
            libraryName: String = jClass.simpleName,
            memorySession: MemorySession = defaultMemorySession()
        ): T = ForeignLibraryMapper.default.map(jClass, libraryName, memorySession)

    }

}

fun <T : Any> foreignLibrary(
    kClass: KClass<T>,
    libraryName: String = kClass.simpleName!!,
    memorySession: MemorySession = defaultMemorySession()
): T = ForeignLibrary.foreignLibrary(kClass.java, libraryName, memorySession)

internal fun defaultMemorySession() = MemorySession.global()
