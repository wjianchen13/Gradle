package com.cold.plugin

import com.android.build.gradle.AppExtension
import com.cold.asm.learn.LearnTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        System.out.println("========================")
        System.out.println("这是个插件!")
        System.out.println("========================")

//        project.extensions.create('person', Person)
        project.getExtensions().create('person', Person)
        
        project.task('getExtension') {
            doLast {
                def person = project['person']
                System.out.println("========================> person name: " + person.name + "  num: " + person.num + "  sex: " + person.sex)

            }
        }

        // 注册transform
        project.extensions.findByType(AppExtension.class)
                .registerTransform(new LearnTransform(project))
//        project.android.registerTransform(new LearnTransform(mProject))
    }

    
    
}

