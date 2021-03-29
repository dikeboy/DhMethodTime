package com.lin.dhjar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project;
import com.example.dhjarfix.LJarUtils

public class MainPlugin implements Plugin<Project>{
    void apply(Project project) {
        System.out.println("==============regist plugin dhmtime==========")
        project.extensions.create("dhMCConfig", LJarConfig)
        if(LJarUtils.getCurrentFlavor(project).contains("release"))
            return
        project.android.registerTransform(new JavassistTransform(project))
    }

}
