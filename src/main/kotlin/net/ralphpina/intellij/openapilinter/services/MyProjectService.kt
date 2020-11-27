package net.ralphpina.intellij.openapilinter.services

import com.intellij.openapi.project.Project
import net.ralphpina.intellij.openapilinter.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
