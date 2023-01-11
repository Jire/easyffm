package org.jire.easyffm

import java.lang.foreign.MemorySession

interface ForeignLibraryMapper {

    fun <T : Any> map(
        jClass: Class<T>,
        libraryName: String,
        memorySession: MemorySession
    ): T

    companion object {

        @JvmStatic
        val default: ForeignLibraryMapper = DefaultForeignLibraryMapper

    }

}
