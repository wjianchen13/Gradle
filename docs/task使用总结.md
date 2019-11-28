# gradle task 使用总结
https://www.jianshu.com/p/c45861426eba

## task的创建
在Gradle创建task的方式有：
```Groovy
task myTask
task myTask { configure closure }
task myTask(type: SomeType)
task myTask(type: SomeType) { configure closure }
```
常见写法：
```Groovy
task myTask1 {
    doLast {
        println "doLast in task1"
    }
}

//采用 Project.task(String name) 方法来创建
project.task("myTask3").doLast {
    println "doLast in task3"
}

//采用 TaskContainer.create(String name) 方法来创建
project.tasks.create("myTask4").doLast {
    println "doLast in task4"
}
```


## Task Actions
task括号内部的代码我们称之为配置代码，在 gradle 脚本的配置阶段都会执行，也就是说不管执行脚本里的哪个任务，
所有 task 里的配置代码都会执行。一个Task是由一序列 Action (动作)组成的，当运行一个 Task 的时候，这个Task 
里的 Action 序列会按顺序依次执行。前面例子括号里的代码只是配置代码，它们并不是 Action ，Task 里的 Action 
只会在该 Task 真正运行时执行，Gralde 里通过 doFirst、doLast 来为 Task 增加 Action 。

doFirst：task执行时最先执行的操作
doLast：task执行时最后执行的操作

## 创建task参数介绍
在 Gradle 中定义 Task 的时候，可以指定更多的参数，如下所示：
参数名	        含义	                        默认值
name	        task的名字	                必须指定，不能为空
type	        task的父类	                默认值为org.gradle.api.DefaultTask
overwrite	    是否替换已经存在的同名task	false
group	        task所属的分组名	            null
description	    task的描述	                null
dependsOn	    task依赖的task集合	        无
constructorArgs	构造函数参数	                无

使用以下代码测试：
```Groovy
task myTask1 {
    doLast {
        println "=============================> doLast in task1"
    }
}

task myTask2 {
    doLast {
        println "=============================> doLast in task2"
    }
}

task myTask3 {
    doLast {
        println "=============================> doLast in task3, this is old task"
    }
}

task myTask3(description: "这是task3的描述", group: "myTaskGroup", dependsOn: [myTask1, myTask2], overwrite: true) {
    doLast {
        println "=============================> doLast in task3, this is new task"
    }
}
```
执行命令
gradlew myTask3
输出结果如下：
```Groovy
> Task :myTask1
=============================> doLast in task1

> Task :myTask2
=============================> doLast in task2

> Task :myTask3
=============================> doLast in task3, this is new task
```
上面例子中创建了2个名为 myTask3 的 task，但是后一个将前一个替换掉了，在分组信息里多了个一个名为 MyTaskGroup 的分组，其他没有命名分组的统一归到 Other 这个分组里去了。

执行命令以下命令查看下 task 信息
gradlew -q tasks --all
```Groovy
MyTaskGroup tasks
-----------------
myTask3 - 这是task3的描述

Other tasks
-----------
myTask1
myTask2

```

## task中的type使用方法
，在 Gradle 中通过 task 关键字创建的 task，默认的父类都是 org.gradle.api.DefaultTask，这里定义了一些 task 的默认行为。
下面是task type使用的一个列子，实际上type可以理解为继承的某一个基类。
```Groovy
// task type使用方法
//自定义Task类，必须继承自DefaultTask
class SayHelloTask extends DefaultTask {

    String msg = "default name"
    int age = 18

    // 构造函数必须用@javax.inject.Inject注解标识
    @javax.inject.Inject
    SayHelloTask(int age) {
        this.age = age
    }

    // 通过@TaskAction注解来标识该Task要执行的动作
    @TaskAction
    void sayHello() {
        println "Hello $msg ! age is ${age}"
    }
    
}

//通过constructorArgs参数来指定构造函数的参数值
task hello1(type: SayHelloTask, constructorArgs: [30])

//通过type参数指定task的父类，可以在配置代码里修改父类的属性
task hello2(type: SayHelloTask, constructorArgs: [18]) {
    //配置代码里修改 SayHelloTask 里的字段 msg 的值
    msg = "hjy"
}
```
分别运行这两个task，可以查看输出:
```Groovy
> Task :hello1
Hello default name ! age is 30

> Task :hello2
Hello hjy ! age is 18

```

## task 接口解析
```Groovy
class SayHelloTask extends DefaultTask {

    String msg = "default name"
    int age = 20

    @TaskAction
    void sayHello() {
        println "=============================>  Hello $msg ! Age is ${age}"
    }

}

task test1 {
    doLast {
        println "=============================> task test1 exec..."
    }
}
task test2 {
    doLast {
        println "=============================> task test2 exec..."
    }
}
task test3 {
    doLast {
        println "=============================> task test3 exec..."
    }
}
task hello(type: SayHelloTask, group: "MyGroup")

//对task进行配置，
hello.configure {
    println "=============================> hello task configure"
    msg = "hjy"
}

//获取task的名称
println "=============================> task name is ${hello.getName()}"
//获取task的组名
println "=============================> task group is ${hello.getGroup()}"

//设置task里的属性值，设置 age = 70
hello.setProperty("age", 70)
//获取task里的某个属性值
println "=============================> task msg is ${hello.property('msg')}"

//设置依赖的task，只有test1 task执行完后才会执行hello task
hello.dependsOn(test1)
//设置终结者任务，执行完hello task之后会执行test2 task，通常可以用该方法做一些清理操作
hello.finalizedBy(test2)

//如果同时执行hello、test3这2个task，会确保test3执行完之后才执行hello这个task，用这个来保证执行顺序
hello.setMustRunAfter([test3])

//设置满足某个条件后才执行该task
hello.setOnlyIf {
    //只有当 age = 70 时，才会执行task，否则不会执行
    return hello.property("age") == 70
}
```
运行下面命令：
gradlew hello test3
输出结果
```Groovy
> Configure project :
=============================> hello task configure
=============================> task name is hello
=============================> task group is MyGroup
=============================> task msg is hjy

> Task :test3
=============================> task test3 exec...

> Task :test1
=============================> task test1 exec...

> Task :hello
=============================>  Hello hjy ! Age is 70

> Task :test2
=============================> task test2 exec...
```

## TaskContainer接口解析
TaskContianer 是用来管理所有的 Task 实例集合的，可以通过 Project.getTasks() 来获取 TaskContainer 实例。
```Groovy
org.gradle.api.tasks.TaskContainer接口：
//查找task
findByPath(path: String): Task
getByPath(path: String): Task
getByName(name: String): Task
withType(type: Class): TaskCollection
matching(condition: Closure): TaskCollection

//创建task
create(name: String): Task
create(name: String, configure: Closure): Task 
create(name: String, type: Class): Task
create(options: Map<String, ?>): Task
create(options: Map<String, ?>, configure: Closure): Task

//当task被加入到TaskContainer时的监听
whenTaskAdded(action: Closure)
```

创建 task 的方法：
```Groovy
//当有task创建时
getTasks().whenTaskAdded { Task task ->
    println "The task ${task.getName()} is added to the TaskContainer"
}

//采用create(name: String)创建
getTasks().create("task1")

//采用create(options: Map<String, ?>)创建
getTasks().create([name: "task2", group: "MyGroup", description: "这是task2描述", dependsOn: ["task1"]])

//采用create(options: Map<String, ?>, configure: Closure)创建
getTasks().create("task3", {
    group "MyGroup"
    setDependsOn(["task1", "task2"])
    setDescription "这是task3描述"
})
```
执行命令gradle -q tasks --all，查看是否创建成功，结果如下：
```Groovy
MyGroup tasks
-------------
task2 - 这是task2描述
task3 - 这是task3描述

Other tasks
-----------
task1
```

我们再来试试查找 task 的方法：

//通过名字查找指定的task
def task3 = getTasks().findByName("task3")
println "findByName() return task is " + task3

def taskList = getTasks().withType(DefaultTask)
def count = 0
//遍历所有的task，打印出其名字
taskList.all { Task t ->
    println "${count++} task name is ${t.name}"
}

执行命令：
gradlew task3
输出结果：
```Groovy
> Configure project :
The task task1 is added to the TaskContainer
The task task2 is added to the TaskContainer
The task task3 is added to the TaskContainer
findByName() return task is task ':task3'
0 task name is buildEnvironment
1 task name is components
2 task name is dependencies
3 task name is dependencyInsight
4 task name is dependentComponents
5 task name is help
6 task name is init
7 task name is model
8 task name is projects
9 task name is properties
10 task name is task1
11 task name is task2
12 task name is task3
13 task name is tasks
14 task name is wrapper
The task clean is added to the TaskContainer
15 task name is clean
```


## Task增量构建
Gradle 支持一种叫做 up-to-date 检查的功能，也就是常说的增量构建。Gradle 的 Task 会把每次运行的结果缓存下来，  
当下次运行时，会检查输出结果有没有变更，如果没有变更则跳过运行，这样可以提高 Gradle 的构建速度。
TaskInputs、TaskOutputs
那么怎么实现一个增量构建呢？一个增量构建必须至少指定一个输入、一个输出，从前面 Task 的类图中可以看到，Task.getInputs()   
对象类型为 TaskInputs，Task.getOutputs() 对象类型为 TaskOuputs，从中也可以看到inputs、outputs都支持哪些数据类型。  
同样以一个简单例子来说明：
```Groovy
task test1 {
    //设置inputs
    inputs.property("name", "hjy")
    inputs.property("age", 20)
    //设置outputs
    outputs.file("$buildDir/test.txt")

    doLast {
        println "exec task task1"
    }
}

task test2 {
    doLast {
        println "exec task task2"
    }
}
```

连续2次运行task，执行命令gradle test1 test2，结果如下：
```Groovy
//第一次的运行结果
> Task :test1
exec task task1

> Task :test2
exec task task2

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

//第二次的运行结果
> Task :test2
exec task task2

BUILD SUCCESSFUL in 0s
2 actionable tasks: 1 executed, 1 up-to-date
```
从结果中可以看到，第2次运行时，test1 task 并没有运行，而是被标记为 up-to-date，而 test2 task 则每次都会运行，这就是典型的增量构建。


## taskInputs、taskOutputs注解
当你自定义 task class 时，可以通过注解来实现增量构建，这是一种更加灵活方便的方式。我们常用的注解包括：

注解名	                属性类型            	                描述
@Input	                任意Serializable类型	                一个简单的输入值
@InputFile	            File	                            一个输入文件，不是目录
@InputDirectory	        File	                            一个输入目录，不是文件
@InputFiles	            Iterable<File>	                    File列表，包含文件和目录
@OutputFile	            File	                            一个输出文件，不是目录
@OutputDirectory	    File	                            一个输出目录，不是文件
@OutputFiles	        Map<String, File>或Iterable<File>	输出文件列表
@OutputDirectories	    Map<String, File>或Iterable<File>	输出目录列表
通过自定义一个 task class 来演示一下用法：
```Groovy
class SayHelloTask extends DefaultTask {
    
    //定义输入
    @Input
    String username;
    @Input
    int age

    //定义输出
    @OutputDirectory
    File destDir;

    @TaskAction
    void sayHello() {
        println "Hello $username ! age is $age"
    }

}

task test(type: SayHelloTask) {
    age = 18
    username = "hjy"
    destDir = file("$buildDir/test")
}
```
执行该 task 之后，会自动生成一个 $buildDir/test 文件目录，当你再次运行时就实施增量构建。但是当你修改   
 age、username 的值，或者删除磁盘上 $buildDir/test 目录，再次运行该 task ，该 task 就会重新运行。


