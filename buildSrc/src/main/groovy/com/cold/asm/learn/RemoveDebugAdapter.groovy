package com.cold.asm.learn

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

import static org.objectweb.asm.Opcodes.V1_5

class RemoveDebugAdapter extends ClassVisitor {  
    
    public RemoveDebugAdapter(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

    /**
     * 添加这个方法不会报异常
     * 注释这个方法会报异常：
     * com.android.builder.dexing.DexArchiveBuilderException: 
     * com.android.builder.dexing.DexArchiveBuilderException: Failed to process F:\mygithub\Gradle\app\build\intermediates\transforms\cold\c_vivoV_test1\debug\27
     */
    @Override
    public void visit(int version, int access, String name,   String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
    }

//    @Override  
//    public void visitSource(String source, String debug) {  
//
//    }  
//
//    @Override  
//    public void visitOuterClass(String owner, String name, String desc) {  
//
//    }  
//
//    @Override  
//    public void visitInnerClass(String name, String outerName,   String innerName, int access) {
//
//    } 
}