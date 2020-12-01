package net.ralphpina.intellij.openapilinter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

private val logger = Logger.getInstance("SpectralAnnotator")

/**
 * Implementation influenced by:
 * -> https://github.com/yoheimuta/intellij-protolint
 * -- See https://github.com/yoheimuta/intellij-protolint/blob/master/src/main/java/com/yoheimuta/intellij/plugin/protolint/ProtolintAnnotator.java
 * -> https://github.com/sonar-intellij-plugin/sonar-intellij-plugin
 * -- See https://github.com/sonar-intellij-plugin/sonar-intellij-plugin/blob/master/src/main/java/org/intellij/sonar/analysis/SonarExternalAnnotator.java
 * -> https://github.com/KronicDeth/intellij-elixir
 * -- See https://github.com/KronicDeth/intellij-elixir/blob/master/src/org/elixir_lang/credo/Annotator.java
 * -> https://github.com/antlr/jetbrains-plugin-sample
 * -- https://github.com/antlr/jetbrains-plugin-sample/blob/master/src/main/java/org/antlr/jetbrains/sample/SampleExternalAnnotator.java
 *
 */
class SpectralAnnotator : ExternalAnnotator<PsiFile, List<SpectralLintIssue>>() {

    override fun collectInformation(file: PsiFile) = file

    override fun doAnnotate(psiFile: PsiFile) = lint(psiFile)

    override fun apply(psiFile: PsiFile, results: List<SpectralLintIssue>, holder: AnnotationHolder) {
        logger.log("Applying ${results.size} annotations")
        if (results.isEmpty()) return
        results.forEach { annotateIssueInFile(holder, psiFile, it) }
    }
}

private fun annotateIssueInFile(holder: AnnotationHolder, psiFile: PsiFile, issue: SpectralLintIssue) {
    logger.log("Annotating $issue")
    val startElement = findFirstElementAtLine(psiFile, issue.line)
    if (startElement == null) {
        // There is no AST element on this line. Maybe a tabulation issue on a blank line?
        holder.createAnnotation(
            getLineRange(psiFile, issue.line),
            issue
        )
    } else if (startElement.isValid) {
        holder.createAnnotation(
            getLineRange(startElement.orFirstChild()),
            issue
        )
    } else {
        logger.log("No annotating added for $issue")
    }
}

private fun AnnotationHolder.createAnnotation(
    textRange: TextRange,
    issue: SpectralLintIssue
) = newAnnotation(issue.toHighlightSeverity(), issue.message)
    .range(textRange)
    .tooltip(issue.message.toHtml())
    .create()

private fun SpectralLintIssue.toHighlightSeverity() =
    when (severity) {
        Severity.WARNING -> HighlightSeverity.WARNING
        Severity.ERROR -> HighlightSeverity.ERROR
    }

/**
 * TODO: document why this is needed
 */
private fun PsiElement.orFirstChild() =
    if (text.trim().isEmpty()) firstChild ?: this else this
