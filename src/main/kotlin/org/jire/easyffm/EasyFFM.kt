package org.jire.easyffm

import java.lang.foreign.MemorySession

object EasyFFM {

    fun defaultMemorySession() = MemorySession.global()

}
