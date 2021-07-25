package com.github.crosswall.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.github.crosswall.plugin.option.HookerOption
import org.gradle.api.Plugin
import org.gradle.api.Project

class ASMHookerPlugin : Plugin<Project> {


    override fun apply(project: Project) {
        println("======================================================")
        println("===============ASMHookerPlugin apply==================")
        println("======================================================")

        val option = project.extensions.create("hookerOption", HookerOption::class.java)

        println("================ASMHookerPlugin create=================")


        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isApp) {
            val appExtension = project.extensions.getByType(AppExtension::class.java)
            appExtension.registerTransform(ASMTransformer(project, option))
        }

    }
}