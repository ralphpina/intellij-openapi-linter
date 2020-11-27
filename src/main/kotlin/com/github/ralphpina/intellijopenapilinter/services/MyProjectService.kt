package com.github.ralphpina.intellijopenapilinter.services

import com.intellij.openapi.project.Project
import com.github.ralphpina.intellijopenapilinter.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
