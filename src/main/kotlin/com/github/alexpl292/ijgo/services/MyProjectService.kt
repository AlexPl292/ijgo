package com.github.alexpl292.ijgo.services

import com.intellij.openapi.project.Project
import com.github.alexpl292.ijgo.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
