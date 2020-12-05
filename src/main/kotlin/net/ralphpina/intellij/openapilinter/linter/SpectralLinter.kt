package net.ralphpina.intellij.openapilinter.linter

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil.execAndGetOutput
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiFile
import net.ralphpina.intellij.openapilinter.linter.config.fetchExecutablePath
import net.ralphpina.intellij.openapilinter.linter.config.fetchRuleSetPath
import net.ralphpina.intellij.openapilinter.log

private val logger = Logger.getInstance("SpectralLinter")

private const val SPECTRAL_EXEC = "spectral"

enum class Severity {
    WARNING,
    ERROR
}

data class SpectralLintIssue(
    val line: Int,
    val column: Int,
    val severity: Severity,
    val message: String
)

/**
 * Inspired by https://github.com/KronicDeth/intellij-elixir
 */
fun lint(psiFile: PsiFile): List<SpectralLintIssue> {
    logger.log(">>>> Starting linter >>>>")
    try {
        val processOutput = execAndGetOutput(generalCommandLine(psiFile))
        return processOutput.parseSpectralResult()
    } catch (exception: ExecutionException) {
        logger.log("!!!! Error linting: $exception !!!!")
    } finally {
        logger.log("<<<< Ending linter <<<<")
    }
    logger.log("<<<< Ending linter with empty list <<<<")
    return emptyList()
}

private fun generalCommandLine(psiFile: PsiFile): GeneralCommandLine {
    return with(GeneralCommandLine()) {
        exePath = fetchExecutablePath().ifEmpty { SPECTRAL_EXEC }
        setWorkDirectory(psiFile.project.basePath)
        withEnvironment(System.getenv())
        addParameter("lint")
        fetchRuleSetPath().let { if (it.isNotEmpty()) addParameter("--ruleset=$it") }
        addParameter(psiFile.virtualFile.path)
        this
    }
}

private fun ProcessOutput.parseSpectralResult() =
    stdoutLines.mapNotNull { it.toSpectralLintIssue() }

@Suppress("MaxLineLength")
private val REGEX =
    """(?<line>\d+):(?<column>\d+)\s+(?<severity>warning|error)\s+(?<code>[a-zA-Z0-9-]*)\s+(?<message>.+)""".toRegex()

private fun String.toSpectralLintIssue(): SpectralLintIssue? {
    // might be valid e.g. 1:1   warning  openapi-tags           OpenAPI object should have non-empty `tags` array.
    // or junk such as OpenAPI 3.x detected
    val result = REGEX.matchEntire(trim()) ?: return null
    val (line, column, severity, _, message) = result.destructured
    val severityType = if (severity == "warning") Severity.WARNING else Severity.ERROR
    return SpectralLintIssue(
        line = line.toInt() - 1,
        column = column.toInt(),
        severity = severityType,
        message = message
    )
}
