package com.github.crosswall.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.github.crosswall.plugin.option.TimeClassOption
import org.gradle.api.Plugin
import org.gradle.api.Project

class TimePlugin : Plugin<Project> {


    override fun apply(project: Project) {
        println("======================================================")
        println("================TimePlugin apply======================")
        println("======================================================")

        val option = project.extensions.create("timeClassOption", TimeClassOption::class.java)

        println("================TimePlugin create======================")


        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isApp) {
            val appExtension = project.extensions.getByType(AppExtension::class.java)
            appExtension.registerTransform(TimeClassTransformer(project, option))
        }

    }
}