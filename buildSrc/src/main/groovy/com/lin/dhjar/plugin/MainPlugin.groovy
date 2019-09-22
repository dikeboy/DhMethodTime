package com.lin.dhjar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project;

/**
 * Created by zhangyipeng on 2018/1/24.
 */

public class MainPlugin implements Plugin<Project>{
    void apply(Project project) {
        project.logger.error("Dhjar start=========================")
        project.extensions.create("dhMCConfig", LJarConfig)
        project.android.registerTransform(new JavassistTransform(project))
    }
}
