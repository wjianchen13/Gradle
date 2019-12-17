package com.cold.asm.learn

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import static org.objectweb.asm.Opcodes.V1_5

class ChangeVersionAdapter extends ClassVisitor {  
    
    public ChangeVersionAdapter(ClassVisitor cv) {   
        super(Opcodes.ASM5, cv)  
    }  
    
    @Override  
    public void visit(int version, int access, String name,   String signature, String superName, String[] interfaces) {   
        cv.visit(V1_5, access, "pkg/test", signature, superName, interfaces);  
    } 
} 