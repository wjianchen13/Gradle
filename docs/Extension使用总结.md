# Extension使用总结

https://www.jianshu.com/p/58d86b4c0ee5

## 使用扩展参数（Extension）
1.先定义一个Persion类
```Groovy
package com.cold.plugin

class Person {
    
    String name = null
    String num = null
    String sex = null

}
```
2.插件内部动态创建Extension
```Groovy
project.extensions.create('person', Person)
```
这样，在build.gradle可以进行使用
```Groovy
person {
    name "gg"
    num "12345"
    sex "man"
}
```
person {}这部分代码必须使用在插件引入之后，否则会报错
```Groovy
apply plugin: 'com.cold.plugin'
```
在上面这部分代码之后
project.extensions相当于project.getExtensions()，即返回的是ExtensionContainer对象，而ExtensionContainer
对象的create方法就是把person{}与Person关联起来,其他通过project.的方式也是同样的道理,例如上面的代码可以改为：
```Groovy
project.getExtensions().create('person', Person)
```

3.创建一个task读取扩展属性的值
```Groovy
project.task('getExtension') {
    doLast {
        def person = project['person']
        System.out.println("========================> person name: " + person.name + "  num: " + person.num + "  sex: " + person.sex)

    }
}
```
gradle 5.0中 << 已经过时了，，是推荐使用doLast 来替代的,原来的写法：
```Groovy
project.task('getExtension') << {
    ...
}
```
应该替换成
```Groovy
project.task('getExtension')  {
    doLast {
        ...
    }
}
```
4.通过task获取动态属性的值
控制台输入：
```Groovy
gradlew getExtension
```
输出：
```Groovy
> Task :app:getExtension
========================> person name: gg  num: 12345  sex: man
```

## ExtensionContainer主要API功能及用法
1.创建Extension
```Groovy
<T> T create​(String name, Class<T> type, Object... constructionArguments)
<T> T create​(Class<T> publicType, String name, Class<? extends T> instanceType, Object... constructionArguments)
```
对应参数含义如下：
publicType：创建的 Extension 实例暴露出来的类类型；
name：要创建的Extension的名字，可以是任意符合命名规则的字符串，不能与已有的重复，否则会抛异常；
instanceType：该Extension的类类型；
constructionArguments：类的构造函数参数值

例子：
```Groovy
class Animal {

    String username
    int legs

    Animal(String name) {
        username = name
    }

    void setLegs(int c) {
        legs = c
    }

    String toString() {
        return "This animal is $username, it has ${legs} legs."
    }
}

//子类
class Pig extends Animal {

    int age
    String owner

    Pig(int age, String owner) {
        super("Pig")
        this.age = age
        this.owner = owner
    }

    String toString() {
        return super.toString() + " Its age is $age, its owner is $owner."
    }

}

//创建的Extension是 Animal 类型
Animal aAnimal = getExtensions().create(Animal, "animal", Pig, 3, "hjy")
//创建的Extension是 Pig 类型
Pig aPig = getExtensions().create("pig", Pig, 5, "kobe")

animal {
    legs = 4    //配置属性
}

pig {
    setLegs 2   //这个是方法调用，也就是 setLegs(2)
}

task testExt {
    doLast {
        println aAnimal
        println aPig
        //验证 aPig 对象是 ExtensionAware 类型的
        println "aPig is a instance of ExtensionAware : ${aPig instanceof ExtensionAware}"
    }
}
```
运行testExt
gradlew testExt
输出结果：
```Groovy
> Task :testExt
This animal is Pig, it has 4 legs. Its age is 3, its owner is hjy.
This animal is Pig, it has 2 legs. Its age is 5, its owner is kobe.
aPig is a instance of ExtensionAware : true
```

2.增加Extension
前面的 create() 方法会创建并返回一个 Extension 对象，与之相似的还有一个 add() 方法，唯一的差别是它并不会返回一个 Extension 对象。
```Groovy
void add​(Class<T> publicType, String name, T extension)
void add​(String name, T extension)
```
增加Extension例子
```Groovy
getExtensions().add(Pig, "mypig", new Pig(5, "kobe"))
mypig {
    username = "MyPig"
    legs = 4
    age = 1
}
task testExt {
    doLast {
        def aPig = project.getExtensions().getByName("mypig")
        println aPig
    }
}
```

3.查找Extension
```Groovy
Object findByName(String name)
<T> T findByType(Class<T> type)
Object getByName(String name)       //找不到会抛异常
<T> T getByType(Class<T> type)  //找不到会抛异常
```
这些API一个是通过名字去查找，一个是通过类类型去查找

4.嵌套Extension
嵌套Extension就是外部Extension定义另外一个Extentsion
例子：
```Groovy
class OuterExt {

    String outerName
    String msg
    InnerExt innerExt = new InnerExt()

    void outerName(String name) {
        outerName = name
    }

    void msg(String msg) {
        this.msg = msg
    }

    //创建内部Extension，名称为方法名 inner
    void inner(Action<InnerExt> action) {
        action.execute(inner)
    }

    //创建内部Extension，名称为方法名 inner
    void inner(Closure c) {
        org.gradle.util.ConfigureUtil.configure(c, innerExt)
    }

    String toString() {
        return "OuterExt[ name = ${outerName}, msg = ${msg}] " + innerExt
    }
}

class InnerExt {

    String innerName
    String msg

    void innerName(String name) {
        innerName = name
    }

    void msg(String msg) {
        this.msg = msg
    }

    String toString() {
        return "InnerExt[ name = ${innerName}, msg = ${msg}]"
    }
}

def outExt = getExtensions().create("outer", OuterExt)

outer {

    outerName "outer"
    msg "this is a outer message."

    inner {
        innerName "inner"
        msg "This is a inner message."
    }

}

task testExt {
    doLast {
        println outExt
    }
}
```
运行testExt，输出结果：
```Groovy
> Task :testExt
OuterExt[ name = outer, msg = this is a outer message.] InnerExt[ name = inner, msg = This is a inner message.]
```
定义嵌套Extension使用了下面的方法：
```Groovy
void inner(Action<InnerExt> action)
void inner(Closure c)
```
定义在 outer 内部的 inner ，Gradle 解析时实质上会进行方法调用，也就是会执行 outer.inner(...) 方法，而该方法的参数是  
一个闭包（俗称 Script Block），所以在类 OuterExt 中必须定义 inner(...) 方法。 
尝试把void inner(Action<InnerExt> action)注释掉，功能是正常的，但是把另外一个注释掉却会报错。按照原文的意思  
应该是使用任意一个方法都可以的。











































