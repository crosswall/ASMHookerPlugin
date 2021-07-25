package com.github.crosswall.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.github.crosswall.plugin.core.TimeClassVisitor
import com.github.crosswall.plugin.option.TimeClassOption
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.internal.impldep.org.apache.commons.codec.digest.DigestUtils
import org.gradle.internal.impldep.org.apache.commons.io.IOUtils
import org.gradle.internal.impldep.org.apache.tools.zip.ZipEntry
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class ASMTransformer(private val project: Project, private val option: TimeClassOption) :
    Transform() {

    override fun getName(): String = "time-class-transform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    @Throws(TransformException::class, InterruptedException::class, IOException::class)
    override fun transform(transformInvocation: TransformInvocation?) {

        val inputs = transformInvocation?.inputs ?: return
        val outputProvider = transformInvocation?.outputProvider ?: return

        outputProvider.deleteAll()

        inputs.forEach { input ->

            input.directoryInputs.forEach { dirInput ->
                hookSourceSetFiles(dirInput, outputProvider)
            }

            input.jarInputs.forEach { jarInput ->
                hookJarFiles(jarInput, outputProvider)
            }
        }
    }

    private fun hookSourceSetFiles(
        dirInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {

        val dest = outputProvider.getContentLocation(
            dirInput.name,
            dirInput.contentTypes, dirInput.scopes,
            Format.DIRECTORY
        )

        if (dirInput.file.isDirectory) {
            dirInput.file
                .walkTopDown()
                .filter { it.isFile }
                .forEach { f ->
                    if (option.isNeedTraceClass(f.name)) {
                        //字节码插桩
                        val clzReader = ClassReader(f.readBytes())
                        val clzWriter = ClassWriter(clzReader, ClassWriter.COMPUTE_MAXS)
                        val cv = TimeClassVisitor(Opcodes.ASM5, clzWriter, option)
                        clzReader.accept(cv, ClassReader.EXPAND_FRAMES)
                        val byteArray = clzWriter.toByteArray()
                        val fos = FileOutputStream(f.parentFile.absolutePath + File.separator + f.name)
                        fos.write(byteArray)
                        fos.close()
                    }
                }
        }

        FileUtils.copyDirectory(dirInput.file, dest)
    }

    private fun hookJarFiles(jarInput: JarInput, outputProvider: TransformOutputProvider) {


        if (jarInput.file.absolutePath.endsWith(".jar")) {
            var jarName = jarInput.name
            val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)

            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length - 4)
            }

            val dest = outputProvider.getContentLocation(
                jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR
            )

            val jarFile = JarFile(jarInput.file)
            val enumeration = jarFile.entries()

            val tmpFile = File(jarInput.file.parent + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }

            val jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))

            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                val inputStream = jarFile.getInputStream(jarEntry)
                if (option.isNeedTraceClass(entryName)) {
                    jarOutputStream.putNextEntry(zipEntry)
                    //字节码插桩
                    val clzReader = ClassReader(IOUtils.toByteArray(inputStream))
                    val clzWriter = ClassWriter(clzReader, ClassWriter.COMPUTE_MAXS)
                    val cv = TimeClassVisitor(Opcodes.ASM5, clzWriter, option)
                    clzReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    val byteArray = clzWriter.toByteArray()
                    jarOutputStream.write(byteArray)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }

            jarOutputStream.close()
            jarFile.close()


            FileUtils.copyFile(tmpFile, dest)

            tmpFile.delete()

        }
    }

}