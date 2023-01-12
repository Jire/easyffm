package org.jire.easyffm

import java.lang.foreign.MemoryLayout
import java.lang.reflect.Method

data class ForeignStructField(
    val name: String,
    val nameSuffix: String,
    val getName: String,
    val getMethod: Method,
    val setName: String,
    val setMethod: Method,
    val returnType: Class<*>,
    val memoryLayout: MemoryLayout
)
