package org.jire.easyffm

import java.lang.foreign.Addressable
import java.lang.foreign.MemoryLayout
import java.lang.foreign.ValueLayout

internal object MemoryLayoutHelper {

    val Class<*>.memoryLayout: MemoryLayout
        get() = when (this) {
            Boolean::class.javaPrimitiveType -> ValueLayout.JAVA_BOOLEAN
            Char::class.javaPrimitiveType -> ValueLayout.JAVA_CHAR
            Byte::class.javaPrimitiveType -> ValueLayout.JAVA_BYTE
            Short::class.javaPrimitiveType -> ValueLayout.JAVA_SHORT
            Int::class.javaPrimitiveType -> ValueLayout.JAVA_INT
            Long::class.javaPrimitiveType -> ValueLayout.JAVA_LONG
            Float::class.javaPrimitiveType -> ValueLayout.JAVA_FLOAT
            Double::class.javaPrimitiveType -> ValueLayout.JAVA_DOUBLE
            //Void::class.javaPrimitiveType -> ValueLayout.ADDRESS

            else -> when {
                isArray -> ValueLayout.ADDRESS//this.componentType.memoryLayout
                Addressable::class.java.isAssignableFrom(this) -> ValueLayout.ADDRESS

                else -> throw UnsupportedOperationException("$this")
            }
        }

    val Class<*>.memoryLayoutString: String
        get() = when (this) {
            Boolean::class.javaPrimitiveType -> "JAVA_BOOLEAN"
            Char::class.javaPrimitiveType -> "JAVA_CHAR"
            Byte::class.javaPrimitiveType -> "JAVA_BYTE"
            Short::class.javaPrimitiveType -> "JAVA_SHORT"
            Int::class.javaPrimitiveType -> "JAVA_INT"
            Long::class.javaPrimitiveType -> "JAVA_LONG"
            Float::class.javaPrimitiveType -> "JAVA_FLOAT"
            Double::class.javaPrimitiveType -> "JAVA_DOUBLE"
            //Void::class.javaPrimitiveType -> "ADDRESS"

            else -> when {
                isArray -> "ADDRESS"//this.componentType.memoryLayoutString
                Addressable::class.java.isAssignableFrom(this) -> "ADDRESS"

                else -> throw UnsupportedOperationException("$this")
            }
        }

}
