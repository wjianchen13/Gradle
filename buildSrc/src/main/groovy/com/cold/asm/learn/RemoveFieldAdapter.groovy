package com.cold.asm.learn

import com.cold.utils.Log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes

class RemoveFieldAdapter extends ClassVisitor {  
    
    private String mName;  
    private String mDesc;  
    
    public RemoveFieldAdapter(ClassVisitor cv, String mName, String mDesc) {
        super(Opcodes.ASM5, cv)
        this.mName = mName;   
        this.mDesc = mDesc;  
    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        Log.println("name: " + name + "  desc: " + desc);
        if (name.equals(mName) && desc.equals(mDesc)) {
            // do not delegate to next visitor -> this removes the method    
            return null;
        }
        return cv.visitField(access, name, desc, signature, value);
    }
}