package net.ralphpina.intellij.openapilinter

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

private const val PROP_RULESET_KEY = "net.ralphpina.intellij.openapilinter.ruleSetPath"

internal fun fetchRuleSetPath(project: Project) =
        PropertiesComponent.getInstance(project).getValue(PROP_RULESET_KEY)

internal fun updateRuleSetPath(project: Project, path: String) =
        PropertiesComponent.getInstance(project).setValue(PROP_RULESET_KEY, path)

private const val PROP_EXECUTABLE_KEY = "net.ralphpina.intellij.openapilinter.executablePath"

internal fun fetchExecutablePath(project: Project) =
        PropertiesComponent.getInstance(project).getValue(PROP_EXECUTABLE_KEY)

internal fun updateExecutablePath(project: Project, path: String) =
        PropertiesComponent.getInstance(project).setValue(PROP_EXECUTABLE_KEY, path)