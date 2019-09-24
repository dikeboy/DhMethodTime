package com.lin.dhjar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project;
import com.example.dhjarfix.LJarUtils
/**
 * Created by zhangyipeng on 2018/1/24.
 */

public class MainPlugin implements Plugin<Project>{
    void apply(Project project) {
        project.extensions.create("dhMCConfig", LJarConfig)
        if(LJarUtils.getCurrentFlavor(project).contains("release"))
            return
        project.android.registerTransform(new JavassistTransform(project))
    }

}
