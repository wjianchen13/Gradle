package com.cold.asm

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.util.TraceClassVisitor

/**
 * 创建一个class
 */
class CreateClass {
    
    CreateClass() {
        
    }

    public void test(String path) {
        ClassWriter cw = new ClassWriter(0);
        PrintWriter printWriter = new PrintWriter(System.out);
        TraceClassVisitor visitor = new TraceClassVisitor(cw, printWriter);

        /**
         * 创建一个类
         * ClassWriter类是ASM中的核心API，用于生成一个类的字节码。ClassWriter的visit方法定义一个类。 
         * 第一个参数V1_1是生成的class的版本号，对应class文件中的主版本号和次版本号，即minor_version和major_version。 
         * 第二个参数ACC_PUBLIC表示该类的访问标识。这是一个public的类。对应class文件中的access_flags。
         * 第三个参数是生成的类的类名。需要注意，这里是类的全限定名。如果生成的class带有包名，如com.jg.zhang.Example， 
         * 那么这里传入的参数必须是com/jg/zhang/Example。对应class文件中的this_class。
         * 第四个参数是和泛型相关的，这里我们不关新，传入null表示这不是一个泛型类。这个参数对应class文件中的Signature属性（attribute） 。
         * 第五个参数是当前类的父类的全限定名。该类直接继承Object。这个参数对应class文件中的super_class。 
         * 第六个参数是String[]类型的，传入当前要生成的类的直接实现的接口。 这里这个类没实现任何接口，所以传入null 。 这个参数对应class文件中的interfaces 。 
         */
        visitor.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "asm/demo1/AddOperImpl", null, "java/lang/Object", null)

        /**
         * 添加构造方法
         * 使用上面创建的ClassWriter对象，调用该对象的visitMethod方法，得到一个MethodVisitor对象，这个对象定义一个方法。对应class文件中的一个method_info 。 
         * 第一个参数是 ACC_PUBLIC，指定要生成的方法的访问标志。这个参数对应method_info 中的access_flags。 
         * 第二个参数是方法的方法名。对于构造方法来说，方法名为<init>。这个参数对应method_info中的name_index，name_index引用常量池中的方法名字符串。 
         * 第三个参数是方法描述符，在这里要生成的构造方法无参数，无返回值，所以方法描述符为()V。这个参数对应method_info中的descriptor_index。 
         * 第四个参数是和泛型相关的，这里传入null表示该方法不是泛型方法。这个参数对应method_info中的Signature属性。
         * 第五个参数指定方法声明可能抛出的异常。这里无异常声明抛出，传入null。这个参数对应method_info中的Exceptions属性。
         * 接下来调用MethodVisitor中的多个方法，生成当前构造方法的字节码。对应method_info中的Code属性。
         * 1 调用visitVarInsn方法，生成aload指令，将第0个本地变量（也就是this）压入操作数栈。
         * 2 调用visitMethodInsn方法，生成invokespecial指令，调用父类（也就是Object）的构造方法。
         * 3 调用visitInsn方法，生成return指令，方法返回。 
         * 4 调用visitMaxs方法，指定当前要生成的方法的最大局部变量和最大操作数栈。 对应Code属性中的max_stack和max_locals。 
         * 5 最后调用visitEnd方法，表示当前要生成的构造方法已经创建完成。 
         */
        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
//        mv.visitCode(); // 如果要生成方法的代码，需要先以visitCode开头，访问结束需要调用visitEnd方法
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // 添加add方法
        mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "add", "(II)I", null, null);
//        mv.visitCode();
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitInsn(Opcodes.IADD);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();

        visitor.visitEnd();

        FileOutputStream fos = new FileOutputStream(new File(path + "AddOperImpl.class"))
        fos.write(cw.toByteArray());
        fos.close();
    }

}
