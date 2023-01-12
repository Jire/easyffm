package org.jire.easyffm

import javassist.*
import javassist.bytecode.AccessFlag
import javassist.bytecode.Bytecode
import javassist.bytecode.MethodInfo
import org.jire.easyffm.ClassPoolHelper.match
import org.jire.easyffm.DescriptorHelper.descriptor
import org.jire.easyffm.MemoryLayoutHelper.memoryLayoutString
import java.lang.foreign.MemorySession
import java.lang.invoke.MethodHandle
import java.lang.reflect.Method
import java.util.concurrent.ThreadLocalRandom

internal object DefaultForeignLibraryMapper : ForeignLibraryMapper {

    private const val GENERATED_CLASS_SUFFIX = "$\$EasyFFM$$"

    private val defaultImports = arrayOf(
        "java.lang.foreign",
        "java.lang.invoke"
    )

    private val cp = ClassPool.getDefault().apply {
        for (import in defaultImports) importPackage(import)
    }
    private val methodHandleClass = cp.match(MethodHandle::class)

    override fun <T : Any> map(
        jClass: Class<T>,
        libraryName: String,
        memorySession: MemorySession
    ): T {
        val cp = cp

        val newClassName = "${jClass.packageName}.$libraryName$GENERATED_CLASS_SUFFIX${ThreadLocalRandom.current().nextLong()}"
        val ctClass = cp.makeClass(newClassName).apply make@{
            addInterface(cp.match(jClass))

            addField(CtField.make("private final Linker linker;", this))
            addField(CtField.make("private final MemorySession memorySession;", this))
            addField(CtField.make("private final SymbolLookup symbolLookup;", this))

            val conBody = StringBuilder("{")
                .append("this.linker = Linker.nativeLinker();")
                .append("this.memorySession = \$1;")
                .append("this.symbolLookup = SymbolLookup.libraryLookup(\"$libraryName\", memorySession);")

            for (method in jClass.declaredMethods) {
                if (method.isDefault) continue
                if (!Modifier.isAbstract(method.modifiers)) continue

                addMethod(method, conBody)
            }

            conBody.append('}')
            addConstructor(
                CtNewConstructor.make(
                    arrayOf(cp.match(MemorySession::class)),
                    emptyArray(),
                    conBody.toString(),
                    this
                )
            )
        }

        val loadedClass = ctClass.toClass(jClass)
        val constructor = loadedClass.getDeclaredConstructor(MemorySession::class.java)
        val instance = constructor.newInstance(memorySession)

        @Suppress("UNCHECKED_CAST")
        return instance as T
    }

    private fun CtClass.addMethod(method: Method, conBody: StringBuilder) {
        val methodName = method.name
        val fieldName = "field$\$$methodName"
        val returnType = method.returnType
        val returnTypeCtClass = cp.match(returnType)
        val parameterTypes = method.parameterTypes
        val parameterCount = parameterTypes.size

        addField(CtField.make("public final MethodHandle $fieldName;", this))

        conBody.append(
            "this.$fieldName = linker.downcallHandle(" +
                    "(Addressable) symbolLookup.lookup(\"$methodName\").get()," +
                    "FunctionDescriptor.of(ValueLayout.${returnType.memoryLayoutString}," +
                    "new MemoryLayout[] { ${parameterTypes.joinToString(",") { "ValueLayout.${it.memoryLayoutString}" }} })" +
                    ");"
        )

        val constPool = classFile2.constPool
        val bytecode = Bytecode(
            constPool,
            1 + parameterCount,
            1 + parameterCount
        ).apply {
            addAload(0)
            addGetfield(this@addMethod, fieldName, "Ljava/lang/invoke/MethodHandle;")
            addLoadParameters(method.parameterTypes.map { cp.match(it) }.toTypedArray(), 1)
            addInvokevirtual(
                methodHandleClass, "invokeExact", returnTypeCtClass,
                parameterTypes.map { cp.match(it) }.toTypedArray()
            )
            addReturn(returnTypeCtClass)
        }
        val methodInfo = MethodInfo(constPool, methodName, method.descriptor).apply {
            codeAttribute = bytecode.toCodeAttribute()
            accessFlags = AccessFlag.PUBLIC or AccessFlag.FINAL
        }
        addMethod(CtMethod.make(methodInfo, this))
    }

}
