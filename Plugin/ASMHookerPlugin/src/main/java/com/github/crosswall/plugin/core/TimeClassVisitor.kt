package com.github.crosswall.plugin.core

import com.github.crosswall.plugin.bean.TraceMethod
import com.github.crosswall.plugin.option.HookerOption
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class TimeClassVisitor(api: Int, clzVisitor: ClassVisitor, private val option: HookerOption) :
    ClassVisitor(api, clzVisitor) {

    private var className: String? = null
    private var isABSClass = false
    private var isBeatClass = false
    private var isTraceClass = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)

        this.className = name

        isABSClass = access and Opcodes.ACC_ABSTRACT > 0 || access and Opcodes.ACC_INTERFACE > 0

        isBeatClass = name?.replace("/", ".").equals(option.mBeatClass)

        name?.let {
            isTraceClass = option.isConfigTraceClass(it.replace("/", "."))
        }
    }


    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val isConstructor = isConstructor(name)

        return when {
            isABSClass || isBeatClass || !isTraceClass || isConstructor -> super.visitMethod(
                access,
                name,
                desc,
                signature,
                exceptions
            )
            else -> {
                val mv = cv.visitMethod(access, name, desc, signature, exceptions)
                TimeMethodVisitor(api, mv, access, name, desc, className)
            }
        }
    }

    private fun isConstructor(methodName: String?): Boolean {
        return methodName?.contains("<init>") ?: false
    }

    inner class TimeMethodVisitor(
        api: Int,
        mv: MethodVisitor?,
        access: Int,
        name: String?,
        desc: String?,
        className: String?
    ) : AdviceAdapter(api, mv, access, name, desc) {

        private var methodName = ""
        private var beatClass = ""

        init {
            val traceMethod = TraceMethod.create(0, access, className, name, desc)
            this.methodName = traceMethod.getMethodNameText()
            option.mBeatClass?.let { beatClass = it.replace(".", "/") }
        }


        override fun onMethodEnter() {
            super.onMethodEnter()
            println("onMethodEnter...$methodName  ===  $beatClass/start")
            mv.visitLdcInsn(methodName)
            mv.visitMethodInsn(INVOKESTATIC, beatClass, "start", "(Ljava/lang/String;)V", false)

        }


        override fun onMethodExit(opcode: Int) {
            super.onMethodExit(opcode)
            println("onMethodExit...$methodName  ===  $beatClass/end")
            mv.visitLdcInsn(methodName)
            mv.visitMethodInsn(INVOKESTATIC, beatClass, "end", "(Ljava/lang/String;)V", false)
        }
    }
}