package com.cold.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.cold.asm.CreateClass
import com.cold.asm.TestClassVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import com.cold.utils.Log
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.util.TraceClassVisitor
import org.objectweb.asm.Opcodes

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

class MyTransform extends Transform {

    Project project
    
    /**
     * 构造函数，我们将Project保存下来备用
     * @param project
     */
    MyTransform(Project project) {
        this.project = project
    }
    
    /**
     * 设置我们自定义的Transform对应的Task名称
     * 类似：TransformClassesWithPreDexForXXX
     */
    @Override
    String getName() {
        return "cold"
    }
    
    /**
     * 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
     * 这样确保其他类型的文件不会传入
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }
    
    /**
     * 指定Transform的作用范围
     */
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }
    
    /**
     * 具体的处理
     */
    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        println "transform start ..." 

        def startTime = System.currentTimeMillis()

        inputs.each { TransformInput input ->

            input.directoryInputs.each { DirectoryInput directoryInput ->
                println "imput path: " + directoryInput.file.getAbsolutePath()
                new CreateClass().test(directoryInput.file.getAbsolutePath())
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->  // dir.eachFileRecurse()方法会递归显示该目录下所有的文件和目录
                        
                        def name = file.name
//                        println 'file: ' + name
                        if (name.endsWith(".class") && !name.startsWith("R\$") && // 排除R.class和BuildConfig.class
                                !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {

                            println 'modify file: ' + name

                            ClassReader cr = new ClassReader(file.bytes)
                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            ClassVisitor cv = new TestClassVisitor(cw)

                            cr.accept(cv, EXPAND_FRAMES)

                            byte[] code = cw.toByteArray()

                            FileOutputStream fos = new FileOutputStream(
                                    file.parentFile.absolutePath + File.separator + name)
                            fos.write(code)
                            fos.close()
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)


                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        def time = (System.currentTimeMillis() - startTime) / 1000

        println "transform time: $time s"
        println "transform end ..."
    }
    
    private void println(String str) {
        Log.println(str)
    }
}
