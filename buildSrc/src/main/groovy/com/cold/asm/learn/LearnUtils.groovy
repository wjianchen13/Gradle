package com.cold.asm.learn

import com.cold.asm.TestClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.util.CheckClassAdapter
import org.objectweb.asm.util.TraceClassVisitor

import static org.objectweb.asm.Opcodes.V1_5
import static org.objectweb.asm.Opcodes.ACC_PUBLIC
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT
import static org.objectweb.asm.Opcodes.ACC_INTERFACE
import static org.objectweb.asm.Opcodes.ACC_FINAL
import static org.objectweb.asm.Opcodes.ACC_STATIC

class LearnUtils{

    private static String path1 = "F:\\mygithub\\Gradle\\app\\build\\intermediates\\javac\\c_vivoV_test1Debug\\compileC_vivoV_test1DebugJavaWithJavac\\classes\\";
    private static String path2 = "F:\\mygithub\\Gradle\\app\\build\\intermediates\\javac\\c_vivoV_test1Debug\\compileC_vivoV_test1DebugJavaWithJavac\\classes\\com\\cold\\gradle\\AsmTest.class"
    
    /**
     * 这里会输出下面结果
     * ========================> java/lang/Runnable extends java/lang/Object {
     * ========================>  run()V
     * ========================> }
     */
    static void readClass() {
        ClassReader cr = new ClassReader("java.lang.Runnable");
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        ClassPrinter cp = new ClassPrinter(cw);
        cr.accept(cp, 0);
    }

    /**
     * 创建类
     */
    static void createClass() {
        ClassWriter cw = new ClassWriter(0); 
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,  "pkg/Comparable", null, "java/lang/Object", null); 
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I",  null, new Integer(-1)).visitEnd(); 
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I",  null, new Integer(0)).visitEnd(); 
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I",  null, new Integer(1)).visitEnd(); 
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",  "(Ljava/lang/Object;)I", null, null).visitEnd(); 
        cw.visitEnd()
        byte[] b = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream(new File(path1 + "LearnCreateClass.class"))
        fos.write(b)
        fos.close()
    }

    /**
     * 转换类
     */
    static void changeClass() {
        byte[] b1 = new File(path2).bytes
        ClassReader cr = new ClassReader(b1)
        ClassWriter cw = new ClassWriter(cr, 0);
        ChangeVersionAdapter ca = new ChangeVersionAdapter(cw) 
        cr.accept(ca, 0)
        byte[] b2 = cw.toByteArray()
        FileOutputStream fos = new FileOutputStream(new File(path1 + "ChangeAsmTest.class"))
        fos.write(b2)
        fos.close()
    }

    /**
     * 移除类的一些东西，这个方法有毒。。
     * 会报异常：com.android.builder.dexing.DexArchiveBuilderException
     */
    static void remove() {
        
        byte[] b1 = new File(path2).bytes
        ClassReader cr = new ClassReader(b1)
        ClassWriter cw = new ClassWriter(cr, 0)
        RemoveDebugAdapter ca = new RemoveDebugAdapter(cw)
        cr.accept(ca, 0)
        byte[] b2 = cw.toByteArray()
        FileOutputStream fos = new FileOutputStream(new File(path1 + "RemoveAsmTest.class"))
        fos.write(b2)
        fos.close()
    }

    /**
     * 移除方法
     */
    static void removeMethod() {
        byte[] b1 = new File(path2).bytes
        ClassReader cr = new ClassReader(b1)
        ClassWriter cw = new ClassWriter(cr, 0)
        RemoveMethodAdapter ca = new RemoveMethodAdapter(cw, "show", "()V")
        cr.accept(ca, 0)
        byte[] b2 = cw.toByteArray()
        FileOutputStream fos = new FileOutputStream(new File(path1 + "RemoveMethodAsmTest.class"))
        fos.write(b2)
        fos.close()
    }

    /**
     * 移除变量
     */
    static void removeField() {
        byte[] b1 = new File(path2).bytes
        ClassReader cr = new ClassReader(b1)
        ClassWriter cw = new ClassWriter(cr, 0)
        RemoveFieldAdapter ca = new RemoveFieldAdapter(cw, "a", "I")
        cr.accept(ca, 0)
        byte[] b2 = cw.toByteArray()
        FileOutputStream fos = new FileOutputStream(new File(path1 + "RemoveFieldAsmTest.class"))
        fos.write(b2)
        fos.close()
    }

    /**
     * 添加变量,
     * 会出现错误：com.android.tools.r8.utils.AbortException: Error: Program type already present: com.cold.gradle.AsmTest
     * clean之后又可以了，AddFieldAdapter修改了文件名
     */
    static void addField() {
        byte[] b1 = new File(path2).bytes
        ClassReader cr = new ClassReader(b1)
        ClassWriter cw = new ClassWriter(cr, 0)
        AddFieldAdapter ca = new AddFieldAdapter(cw, ACC_PUBLIC, "d", "I")
        cr.accept(ca, 0)
        byte[] b2 = cw.toByteArray()
        FileOutputStream fos = new FileOutputStream(new File(path1 + "AddFieldAsmTest.class"))
        fos.write(b2)
        fos.close()
    }

    /**
     * TraceClassVisitor 打印信息，没有成功
     */
    static void testTraceClassVisitor() {
        ClassWriter cw = new ClassWriter(0)
        PrintWriter printWriter = new PrintWriter(path1 + "traceclass.txt")
        TraceClassVisitor cv = new TraceClassVisitor(cw, printWriter)
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,  "pkg/Comparable", null, "java/lang/Object", null);
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I",  null, new Integer(-1)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I",  null, new Integer(0)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I",  null, new Integer(1)).visitEnd();
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",  "(Ljava/lang/Object;)I", null, null).visitEnd();
        cv.visitEnd(); 
        byte[] b = cw.toByteArray();
    }

    /**
     * CheckClassAdapter 测试 没有成功
     * 抛异常：Cannot visit member before visit has been called.
     */
    static void testCheckClassAdapter() {
        ClassWriter cw = new ClassWriter(0);
        PrintWriter printWriter = new PrintWriter(path1 + "traceclass.txt")
        TraceClassVisitor tcv = new TraceClassVisitor(cw, printWriter); 
        CheckClassAdapter cv = new CheckClassAdapter(tcv);
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,  "pkg/Comparable", null, "java/lang/Object", null);
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I",  null, new Integer(-1)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I",  null, new Integer(0)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I",  null, new Integer(1)).visitEnd();
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",  "(Ljava/lang/Object;)I", null, null).visitEnd();
        cv.visitEnd(); 
        byte[] b = cw.toByteArray();
    }
    
    
}









