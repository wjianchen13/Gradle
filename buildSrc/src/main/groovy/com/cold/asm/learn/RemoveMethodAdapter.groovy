package com.cold.asm.learn

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import com.cold.utils.Log

class RemoveMethodAdapter extends ClassVisitor {  
    
    private String mName;  
    private String mDesc;  
    
    public RemoveMethodAdapter(ClassVisitor cv, String mName, String mDesc) {
        super(Opcodes.ASM5, cv)
        this.mName = mName;   
        this.mDesc = mDesc;  
    }  
    
    @Override  
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {   
        Log.println("name: " + name + "  desc: " + desc)
        if (name.equals(mName) && desc.equals(mDesc)) {    
            // do not delegate to next visitor -> this removes the method    
            return null;   
        }   
        return cv.visitMethod(access, name, desc, signature, exceptions);  
    } 
}