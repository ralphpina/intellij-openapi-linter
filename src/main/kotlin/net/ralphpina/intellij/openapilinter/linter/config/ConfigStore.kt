package net.ralphpina.intellij.openapilinter.linter.config

import com.intellij.ide.util.PropertiesComponent

private const val PROP_RULESET_KEY = "net.ralphpina.intellij.openapilinter.ruleSetPath"

fun fetchRuleSetPath() =
    PropertiesComponent.getInstance().getValue(PROP_RULESET_KEY).orEmpty()

fun updateRuleSetPath(path: String) =
    PropertiesComponent.getInstance().setValue(PROP_RULESET_KEY, path)

const val PROP_EXECUTABLE_KEY = "net.ralphpina.intellij.openapilinter.executablePath"

fun fetchExecutablePath() =
    PropertiesComponent.getInstance().getValue(PROP_EXECUTABLE_KEY).orEmpty()

fun updateExecutablePath(path: String) =
    PropertiesComponent.getInstance().setValue(PROP_EXECUTABLE_KEY, path)
