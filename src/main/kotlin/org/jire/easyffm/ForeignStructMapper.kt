package org.jire.easyffm

import org.jire.easyffm.EasyFFM.defaultMemorySession
import java.lang.foreign.MemorySession

interface ForeignStructMapper {

    fun <T : Any> map(
        jClass: Class<T>,
        memorySession: MemorySession = defaultMemorySession()
    ): T

}
