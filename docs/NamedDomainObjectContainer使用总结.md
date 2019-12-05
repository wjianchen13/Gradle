# Extension使用总结

https://www.jianshu.com/p/167cd4b82653

## NamedDomainObjectContainer介绍
NamedDomainObjectContainer顾名思义就是命名领域对象容器，它的主要功能有：  
1. 它能够通过DSL(在Gradle脚本中)创建指定 type 的对象实例；  
2. 指定的 type 必须有一个 public 构造函数，且必须带有一个 String name 的参数，type 类型的领域对象必须有名为“name”的属性；  
3. 它是一个实现了 SortedSet 接口的容器，所以所有领域对象的 name 属性值都必须是唯一的，在容器内部会用 name 属性来排序；  

## NamedDomainObjectContainer使用
NamedDomainObjectContainer 需要通过 Project.container(...) API 来创建，其定义为：
```Groovy
<T> NamedDomainObjectContainer<T> container​(Class<T> type)
<T> NamedDomainObjectContainer<T> container​(Class<T> type, NamedDomainObjectFactory<T> factory)
<T> NamedDomainObjectContainer<T> container​(java.lang.Class<T> type, Closure factoryClosure
```
例子:
```Groovy
class TestDomainObj {

    //必须定义一个 name 属性，并且这个属性值初始化以后不要修改
    String name

    String msg

    //构造函数必须有一个 name 参数
    public TestDomainObj(String name) {
        this.name = name
    }

    void msg(String msg) {
        this.msg = msg
    }

    String toString() {
        return "name = ${name}, msg = ${msg}"
    }
}

//创建一个扩展
class TestExtension {

    //定义一个 NamedDomainObjectContainer 属性
    NamedDomainObjectContainer<TestDomainObj> testDomains

    public TestExtension(Project project) {
        //通过 project.container(...) 方法创建 NamedDomainObjectContainer 
        NamedDomainObjectContainer<TestDomainObj> domainObjs = project.container(TestDomainObj)
        testDomains = domainObjs
    }

    //让其支持 Gradle DSL 语法
    void testDomain(Action<NamedDomainObjectContainer<TestDomainObj>> action) {
        action.execute(testDomains)
    }

    void test() {
        //遍历命名领域对象容器，打印出所有的领域对象值
        testDomains.all { data ->
            println data
        }
    }
}

//创建一个名为 test 的 Extension
def testExt = getExtensions().create("test", TestExtension, project)

test {
    testDomain {
        domain2 {
            msg "This is domain2"
        }
        domain1 {
            msg "This is domain1"
        }
        domain3 {
            msg "This is domain3"
        }
    }
}

task myTask {
    doLast {
        testExt.test()
    }
}
```
运行myTask，输出结果：
```Groovy
> Task :myTask
name = domain1, msg = This is domain1
name = domain2, msg = This is domain2
name = domain3, msg = This is domain3
```

## NamedDomainObjectContainer遍历和查找
NamedDomainObjectContainer 既然是一个容器类，与之相应的必然会有查找容器里的元素和遍历容器的方法：
//遍历
void all(Closure action)
//查找
<T> T getByName(String name)
//查找
<T> T findByName(String name)
遍历和查找的例子：
```Groovy
void test() {
    //遍历命名领域对象容器，打印出所有的领域对象值
    testDomains.all { data ->
        println data
    }
}

void find() {
    //通过名字查找
    TestDomainObj testData = testDomains.getByName("domain2")
    println "getByName: ${testData}"
}
```
运行myTask，输出结果：
```Groovy
> Task :myTask
name = domain1, msg = This is domain1
name = domain2, msg = This is domain2
name = domain3, msg = This is domain3
getByName: name = domain2, msg = This is domain2
```
Gradle 中有很多容器类的迭代遍历方法有 each(Closure action)、all(Closure action)，但是一般我们都会用 all(...)   
来进行容器的迭代。all(...) 迭代方法的特别之处是，不管是容器内已存在的元素，还是后续任何时刻加进去的元素，都会进行遍历。  












































