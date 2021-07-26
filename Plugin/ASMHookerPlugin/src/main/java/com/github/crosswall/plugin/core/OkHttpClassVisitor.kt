package com.github.crosswall.plugin.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter


class OkHttpClassVisitor(api: Int, classVisitor: ClassVisitor?) : ClassVisitor(api, classVisitor) {

    private var className: String? = null
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
     //   println("$className")
    }

    override fun visitMethod(
        access: Int, name: String,
        desc: String?, signature: String?, exceptions: Array<String?>?
    ): MethodVisitor? {
//        return super.visitMethod(
//            access,
//            name,
//            desc,
//            signature,
//            exceptions
//        )
        //

        return when{
            className == "okhttp3/OkHttpClient\$Builder" && name == "<init>" -> {
                val mv =
                    cv.visitMethod(access, name, desc, signature, exceptions)
                OkHttpMethodVisitor(api, mv, access, name, desc)
            }
            else -> super.visitMethod(access, name, desc, signature, exceptions)
        }
    }


    inner class OkHttpMethodVisitor(
        api: Int,
        mv: MethodVisitor?,
        access: Int,
        name: String?,
        desc: String?
    ) : AdviceAdapter(api, mv, access, name, desc), Opcodes {


        override fun visitInsn(opcode: Int) {
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode == Opcodes.ATHROW) {

                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/github/crosswall/inject/OkHttpHooker",
                    "globalEventFactory",
                    "Lokhttp3/EventListener\$Factory;"
                )
                mv.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    "okhttp3/OkHttpClient\$Builder",
                    "eventListenerFactory",
                    "Lokhttp3/EventListener\$Factory;"
                )

            }
            super.visitInsn(opcode)
        }

    }

}