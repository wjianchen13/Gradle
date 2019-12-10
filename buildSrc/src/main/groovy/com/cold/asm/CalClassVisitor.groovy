package com.cold.asm

import com.cold.annotation.Cal

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import com.cold.utils.Log

/**
 * 使用ClassVisitor生成和改变字节码
 */
class CalClassVisitor extends ClassVisitor {

    CalClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
    }
    
    @Override
    public MethodVisitor visitMethod(int access, final String name, String desc, String signature, String[] exceptions) {
        Log.println("access: " + access + "  name: " + name + "  desc: " + desc + "  signature: " + signature)
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {

            private boolean inject = false;

            @Override
            public AnnotationVisitor visitAnnotation(String desc1, boolean visible) {
//                Log.println("AdviceAdapter visitAnnotation desc1: " + desc1)
                if (Type.getDescriptor(Cal.class).equals(desc1)) {
                    inject = true;
                }
                return super.visitAnnotation(desc1, visible);
            }

            @Override
            protected void onMethodEnter() {
//                Log.println("AdviceAdapter onMethodEnter: ")
                if (inject) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("========start=========");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                            "(Ljava/lang/String;)V", false);

                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "me/wangyuwei/costtime/TimeCache", "setStartTime",
                            "(Ljava/lang/String;J)V", false);
                }
            }

            @Override
            protected void onMethodExit(int opcode) {
//                Log.println("AdviceAdapter onMethodExit opcode: " + opcode)
                if (inject) {
                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "me/wangyuwei/costtime/TimeCache", "setEndTime",
                            "(Ljava/lang/String;J)V", false);

                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKESTATIC, "me/wangyuwei/costtime/TimeCache", "getCostTime",
                            "(Ljava/lang/String;)Ljava/lang/String;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                            "(Ljava/lang/String;)V", false);

                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("========end=========");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                            "(Ljava/lang/String;)V", false)
                }
            }
        }
        return mv
    }
}