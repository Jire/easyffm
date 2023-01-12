package org.jire.easyffm

import org.jire.easyffm.EasyFFM.defaultMemorySession
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySession
import kotlin.reflect.KClass

interface ForeignStruct {

    @Target(AnnotationTarget.CLASS)
    annotation class FieldOrder(val fields: Array<String>)

    @Target(AnnotationTarget.PROPERTY_GETTER)
    annotation class StructArray(val length: Int)

    val segment: MemorySegment

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T : Any> foreignStruct(
            jClass: Class<T>,
            memorySession: MemorySession = defaultMemorySession()
        ): T = DefaultForeignStructMapper.map(jClass, memorySession)

    }

}

fun <T : Any> foreignStruct(
    kClass: KClass<T>,
    memorySession: MemorySession = defaultMemorySession()
): T = ForeignStruct.foreignStruct(kClass.java, memorySession)
