package org.jire.easyffm

import javassist.*
import javassist.bytecode.*
import org.jire.easyffm.ClassPoolHelper.match
import org.jire.easyffm.DescriptorHelper.descriptor
import org.jire.easyffm.ForeignStruct.StructArray
import org.jire.easyffm.MemoryLayoutHelper.memoryLayout
import org.jire.easyffm.MemoryLayoutHelper.memoryLayoutString
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySession
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.invoke.MutableCallSite
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max

object DefaultForeignStructMapper : ForeignStructMapper {

    private const val GENERATED_CLASS_SUFFIX = "$\$EasyFFM$$"

    private val defaultImports = arrayOf(
        "java.lang.foreign",
        "java.lang.invoke"
    )

    private val cp = ClassPool.getDefault().apply {
        for (import in defaultImports) importPackage(import)
    }

    override fun <T : Any> map(
        jClass: Class<T>,
        memorySession: MemorySession
    ): T {
        val cp = DefaultForeignStructMapper.cp

        val fieldOrder = jClass.getAnnotation(ForeignStruct.FieldOrder::class.java)

        val segment: MemorySegment
        val newClassName =
            "${DefaultForeignStructMapper::class.java.packageName}.${jClass.simpleName}$GENERATED_CLASS_SUFFIX${
                ThreadLocalRandom.current().nextLong()
            }"
        val ctClass = cp.makeClass(newClassName).apply make@{
            addInterface(cp.match(jClass))

            val fieldNames = fieldOrder.fields
            val fields: MutableList<ForeignStructField> = ArrayList(fieldNames.size)

            for (i in 0..fieldNames.lastIndex) {
                val name = fieldNames[i]

                val nameSuffix = "${name[0].uppercaseChar()}${if (name.length > 1) name.substring(1) else ""}"

                val getName = "get$nameSuffix"
                val getMethod = jClass.getDeclaredMethod(getName)

                val returnType = getMethod.returnType

                val setName = "set$nameSuffix"
                val setMethod = jClass.getDeclaredMethod(setName, returnType)

                val layout = if (returnType.isArray) {
                    val arrayInfo = getMethod.getDeclaredAnnotation(StructArray::class.java)
                    MemoryLayout.sequenceLayout(
                        arrayInfo.length.toLong(),
                        returnType.componentType.memoryLayout
                            .withBitAlignment(8)
                    )
                } else returnType.memoryLayout
                    .withName(name)
                    .withBitAlignment(8)

                fields += ForeignStructField(
                    name,
                    nameSuffix,
                    getName,
                    getMethod,
                    setName,
                    setMethod,
                    returnType,
                    layout
                )
            }

            val layout = MemoryLayout.structLayout(*fields.map { it.memoryLayout }.toTypedArray())
            segment = memorySession.allocate(layout)
            val size = layout.byteSize()

            addField(CtField.make("public final MemorySegment segment;", this))
            addConstructor(
                CtNewConstructor.make(
                    arrayOf(cp.match(MemorySegment::class)),
                    emptyArray(),
                    "{ this.segment = $1; }",
                    this
                )
            )

            addMethod(CtMethod.make("public final MemorySegment getSegment() { return segment; }", this))

            segment.setAtIndex(ValueLayout.JAVA_INT, 0, size.toInt())

            val constPool = classFile2.constPool

            for (i in 0..fields.lastIndex) {
                val field = fields[i]
                val returnTypeCt = cp.match(field.returnType)

                addGetMethod(i, constPool, field, returnTypeCt)
                addSetMethod(i, constPool, field, returnTypeCt)
            }
        }

        val lookup = MethodHandles.lookup()
        val definedClass = lookup.defineClass(ctClass.toBytecode())
        val methodType = MethodType.methodType(Void::class.javaPrimitiveType, MemorySegment::class.java)
        val con = lookup.findConstructor(
            definedClass,
            methodType
        )

        val site = MutableCallSite(MethodType.methodType(definedClass, MemorySegment::class.java))
        val invoker = site.dynamicInvoker()
        site.target = con

        @Suppress("UNCHECKED_CAST")
        return invoker.invoke(segment) as T
    }

    private fun CtClass.addGetMethod(
        i: Int,
        constPool: ConstPool,
        field: ForeignStructField,
        returnTypeCt: CtClass
    ) {
        val bytecode = Bytecode(
            constPool,
            4, 1
        ).apply {
            if (field.returnType.isArray) {
                addOpcode(Opcode.ACONST_NULL)
                addReturn(returnTypeCt)
                return@apply
            }

            addAload(0)
            addGetfield(this@addGetMethod, "segment", "Ljava/lang/foreign/MemorySegment;")

            val memLayoutClassName = field.returnType.memoryLayoutString
            val memoryLayoutName = field.memoryLayout.javaClass.name
            addGetstatic(
                cp.get(ValueLayout::class.java.name),
                memLayoutClassName,
                field.memoryLayout.javaClass.descriptor
            )
            addLconst(i.toLong())

            addInvokeinterface(
                cp.get(MemorySegment::class.java.name),
                "getAtIndex",
                returnTypeCt,
                arrayOf(
                    cp.get(memoryLayoutName),
                    CtClass.longType
                ),
                4 // 1 for "this", 1 for layout, 2 for long (index)
            )

            addReturn(returnTypeCt)
        }
        val methodInfo = MethodInfo(constPool, field.getName, field.getMethod.descriptor).apply {
            codeAttribute = bytecode.toCodeAttribute()
            accessFlags = AccessFlag.PUBLIC or AccessFlag.FINAL
        }
        addMethod(CtMethod.make(methodInfo, this))
    }

    private fun CtClass.addSetMethod(
        i: Int,
        constPool: ConstPool,
        field: ForeignStructField,
        returnTypeCt: CtClass
    ) {
        val bytecode = Bytecode(
            constPool,
            5, 3
        ).apply {
            if (field.returnType.isArray) {
                addReturn(null)
                return@apply
            }

            addAload(0)
            addGetfield(this@addSetMethod, "segment", "Ljava/lang/foreign/MemorySegment;")

            val memLayoutClassName = field.returnType.memoryLayoutString
            val memoryLayoutName = field.memoryLayout.javaClass.name
            addGetstatic(
                cp.get(ValueLayout::class.java.name),
                memLayoutClassName,
                field.memoryLayout.javaClass.descriptor
            )
            addLconst(i.toLong())
            addLoad(1, returnTypeCt) // arg1

            val returnTypeSize = field.returnType.memoryLayout.byteSize().toInt()
            addInvokeinterface(
                cp.get(MemorySegment::class.java.name),
                "setAtIndex",
                CtClass.voidType,
                arrayOf(
                    cp.get(memoryLayoutName),
                    CtClass.longType,
                    returnTypeCt,
                ),
                4 + max(1, returnTypeSize / 4) // 1 for "this", 1 for layout, 2 for long (index), 1-2 for value
            )

            addReturn(null)
        }
        val methodInfo = MethodInfo(constPool, field.setName, field.setMethod.descriptor).apply {
            codeAttribute = bytecode.toCodeAttribute()
            accessFlags = AccessFlag.PUBLIC or AccessFlag.FINAL
        }
        addMethod(CtMethod.make(methodInfo, this))
    }

}
