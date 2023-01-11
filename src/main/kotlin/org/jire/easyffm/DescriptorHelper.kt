package org.jire.easyffm

import java.lang.reflect.Method

internal object DescriptorHelper {

    val Class<*>.descriptor: String
        get() {
            if (isPrimitive) {
                return when (name) {
                    "boolean", "java.lang.Boolean" -> "Z"
                    "char", "java.lang.Character" -> "C"
                    "byte", "java.lang.Byte" -> "B"
                    "short", "java.lang.Short" -> "S"
                    "int", "java.lang.Integer" -> "I"
                    "float", "java.lang.Float" -> "F"
                    "long", "java.lang.Long" -> "J"
                    "double", "java.lang.Double" -> "D"
                    "void", "java.lang.Void" -> "V"
                    else -> throw UnsupportedOperationException()
                }
            }

            val replacedName = name.replace('.', '/')
            if (isArray) return replacedName
            return "L${replacedName};"
        }

    val Method.descriptor: String
        get() {
            val sb = StringBuilder("(")
            for (type in parameterTypes) {
                sb.append(type.descriptor)
            }
            sb.append(')')

            sb.append(returnType.descriptor)

            return sb.toString()
        }

}
