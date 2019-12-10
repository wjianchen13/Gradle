# gradle asm 笔记
这个是gradle asm的相关使用记录
http://www.wangyuwei.me/2017/03/05/ASM%E5%AE%9E%E6%88%98%E7%BB%9F%E8%AE%A1%E6%96%B9%E6%B3%95%E8%80%97%E6%97%B6/

## ASM 简介
ASM 是一个 Java 字节码操控框架。它能被用来动态生成类或者增强既有类的功能。ASM 可以直接产生二进制 class 文件，也可以  
在类被加载入 Java 虚拟机之前动态改变类行为。Java class 被存储在严格格式定义的 .class 文件里，这些类文件拥有足够的元  
数据来解析类中的所有元素：类名称、方法、属性以及 Java 字节码（指令）。ASM 从类文件中读入信息后，能够改变类行为，分析  
类信息，甚至能够根据用户要求生成新类。一句话，ASM能够修改.class文件，从而动态生成类或者增强现有类的功能。  
官网：https://asm.ow2.io/

## 简单使用：
1.可以直接使用android.tools.build的asm，添加下面依赖：
```Groovy
dependencies {
    ...
    implementation 'com.android.tools.build:gradle:3.4.2'
}
```
2.自定义个Transform，在插件里面注册：
```Groovy
class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        ...
        // 注册transform
        project.extensions.findByType(AppExtension.class)
                .registerTransform(new MyTransform(project))
    }
}
```
3.在自定义Transform进行具体处理：
```Groovy
@Override
void transform(Context context, Collection<TransformInput> inputs,
               Collection<TransformInput> referencedInputs,
               TransformOutputProvider outputProvider, boolean isIncremental)
        throws IOException, TransformException, InterruptedException {
    println "transform start ..." 
    ...

}
```

## ASM常用的类
ClassReader  
    字节码的读取与分析引擎。它采用类似SAX的事件读取机制，每当有事件发生时，调用注册的ClassVisitor、AnnotationVisitor、FieldVisitor、MethodVisitor做相应的处理。  
ClassVisitor  
    定义在读取Class字节码时会触发的事件，如类头解析完成、注解解析、字段解析、方法解析等  
AnnotationVisitor  
    定义在解析注解时会触发的事件，如解析到一个基本值类型的注解、enum值类型的注解、Array值类型的注解、注解值类型的注解等  
FieldVisitor  
    定义在解析字段时触发的事件，如解析到字段上的注解、解析到字段相关的属性等  
MethodVisitor  
    定义在解析方法时触发的事件，如方法上的注解、属性、代码等。  
ClassWriter  
    它实现了ClassVisitor接口，用于拼接字节码。

























