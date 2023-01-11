package org.jire.easyffm

import javassist.ClassPool
import javassist.CtClass
import kotlin.reflect.KClass

internal object ClassPoolHelper {

    fun ClassPool.match(classname: String): CtClass {
        return when (classname) {
            "boolean" -> CtClass.booleanType
            "char" -> CtClass.charType
            "byte" -> CtClass.byteType
            "short" -> CtClass.shortType
            "int" -> CtClass.intType
            "long" -> CtClass.longType
            "float" -> CtClass.floatType
            "double" -> CtClass.doubleType
            "void" -> CtClass.voidType
            else -> get(classname)
        }
    }

    fun ClassPool.match(jClass: Class<*>) = match(jClass.name)

    fun ClassPool.match(kClass: KClass<*>) = match(kClass.java)

}
