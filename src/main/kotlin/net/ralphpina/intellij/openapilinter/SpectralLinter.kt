package net.ralphpina.intellij.openapilinter

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.delete
import java.io.*
import java.nio.file.Files
import java.nio.file.Path

private val logger = Logger.getInstance("SpectralLinter")

private const val SPECTRAL_EXEC = "spectral"

enum class Severity {
    WARNING,
    ERROR
}

data class SpectralLintError(
        val line: Int,
        val column: Int,
        val severity: Severity,
        val message: String
)

/**
 * Implementation taken from https://github.com/yoheimuta/intellij-protolint
 */
fun lint(editor: Editor): List<SpectralLintError> {
    logger.debug(">>>> Starting linter >>>>")
    val path = Files.createTempFile(null, ".yaml")
    try {
        val cache = cacheEditor(editor, path)
        val process = createProcess(checkNotNull(editor.project), cache.path)
        return parseOutput(process)
    } finally {
        logger.debug("<<<< Ending linter <<<<")
        path.delete()
    }
}

private fun cacheEditor(editor: Editor, path: Path): VirtualFile {
    FileWriter(path.toString()).use { it.write(editor.document.text) }
    return checkNotNull(LocalFileSystem.getInstance().findFileByPath(path.toString())) { "File not found." }
}

private fun createProcess(project: Project, filePath: String) =
        with(GeneralCommandLine()) {
            exePath = fetchExecutablePath() ?: SPECTRAL_EXEC
            setWorkDirectory(project.basePath)
            withEnvironment(System.getenv())
            addParameter("lint")
            fetchRuleSetPath()?.let { addParameter("--ruleset=$it") }
            addParameter(filePath)
            createProcess()
        }

private fun parseOutput(process: Process): List<SpectralLintError> {
    val warnings = mutableListOf<SpectralLintError>()
    process.inputStream.bufferedReader().lines().forEach {
        parseWarning(it)?.let { warning ->
            warnings.add(warning)
        }
    }
    process.waitFor()
    println("Process exit code: ${process.exitValue()}")
    return warnings
}

private val REGEX = """(?<line>\d+):(?<column>\d+)\s+(?<severity>warning|error)\s+(?<code>[a-zA-Z0-9-]*)\s+(?<message>.+)""".toRegex()

private fun parseWarning(output: String): SpectralLintError? {
    // might be valid e.g. 1:1   warning  openapi-tags           OpenAPI object should have non-empty `tags` array.
    // or junk such as OpenAPI 3.x detected
    val result = REGEX.matchEntire(output.trim()) ?: return null
    val (line, column, severity, _, message) = result.destructured
    val severityType = if (severity == "warning") Severity.WARNING else Severity.ERROR
    return SpectralLintError(
            line = line.toInt(),
            column = column.toInt(),
            severity = severityType,
            message = message
    )
}