package com.cold.asm.learn

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import com.cold.utils.Log
import org.objectweb.asm.Opcodes

public class ClassPrinter extends ClassVisitor {

    public ClassPrinter(final ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }
    

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) { 
        Log.println(name + " extends " + superName + " {");  
    }  
    public void visitSource(String source, String debug) { 
        
    }  
    
    public void visitOuterClass(String owner, String name, String desc) { 
        
    }  
    
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {   
        return null;  
    }  
    
    public void visitAttribute(Attribute attr) {

    }
    
    public void visitInnerClass(String name, String outerName,   String innerName, int access) {
    
    }  
    
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {   
        Log.println(" " + desc + " " + name);   
        return null;  
    }  
    
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {  
        Log.println(" " + name + desc);   
        return null;  
    }  
    
    public void visitEnd() {   
        Log.println("}");  
    } 
}