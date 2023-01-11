package org.jire.easyffm

internal object MemoryLayoutHelper {

    val Class<*>.memoryLayoutString: String
        get() = when (this) {
            Boolean::class.javaPrimitiveType -> "ValueLayout.JAVA_BOOLEAN"
            Char::class.javaPrimitiveType -> "ValueLayout.JAVA_CHAR"
            Byte::class.javaPrimitiveType -> "ValueLayout.JAVA_BYTE"
            Short::class.javaPrimitiveType -> "ValueLayout.JAVA_SHORT"
            Int::class.javaPrimitiveType -> "ValueLayout.JAVA_INT"
            Long::class.javaPrimitiveType -> "ValueLayout.JAVA_LONG"
            Float::class.javaPrimitiveType -> "ValueLayout.JAVA_FLOAT"
            Double::class.javaPrimitiveType -> "ValueLayout.JAVA_DOUBLE"
            //Void::class.javaPrimitiveType -> "ValueLayout.ADDRESS"
            else -> throw IllegalArgumentException()
        }

}
