package net.ralphpina.intellij.openapilinter

import com.intellij.openapi.diagnostic.Logger

private const val FORCE_STD_OUT = false

fun Logger.log(message: String) =
    if (FORCE_STD_OUT) println(message) else this.debug(message)
