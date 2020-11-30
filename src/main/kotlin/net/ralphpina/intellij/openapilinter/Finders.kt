package net.ralphpina.intellij.openapilinter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Copy/pasted from https://github.com/sonar-intellij-plugin/sonar-intellij-plugin/blob/master/src/main/java/org/intellij/sonar/util/Finders.java
 * I converted it to Kotlin using Intellij.
 * Then I manually fixed things like the use of Optional<>.
 */
fun findFirstElementAtLine(file: PsiFile, ijLine: Int): PsiElement? {
    val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
    var element = getFirstSiblingFrom(file, ijLine, document)
    while (element != null && element.textLength == 0) {
        element = element.nextSibling
    }
    if (document != null && element != null
            && document.getLineNumber(element.textOffset) != ijLine) {
        element = null
    }
    return element
}

private fun getFirstSiblingFrom(file: PsiFile, ijLine: Int, document: Document?): PsiElement? {
    if (document == null) return null
    var element: PsiElement? = null
    try {
        val offset = document.getLineStartOffset(ijLine)
        element = file.viewProvider.findElementAt(offset)
        if (element != null && document.getLineNumber(element.textOffset) != ijLine) {
            element = element.nextSibling
        }
    } catch (ignore: IndexOutOfBoundsException) { //NOSONAR
        // element keeps to be absent
    }
    return element
}

fun getLineRange(psiFile: PsiFile, line: Int?): TextRange {
    if (line == null) return TextRange.EMPTY_RANGE
    val project = psiFile.project
    val documentManager = PsiDocumentManager.getInstance(project)
    val document = documentManager.getDocument(psiFile.containingFile) ?: return TextRange.EMPTY_RANGE
    val ijLine = if (line > 0) line - 1 else 0
    return getTextRangeForLine(document, ijLine)
}

private fun getTextRangeForLine(document: Document, line: Int): TextRange {
    return try {
        val lineStartOffset = document.getLineStartOffset(line)
        val lineEndOffset = document.getLineEndOffset(line)
        TextRange(lineStartOffset, lineEndOffset)
    } catch (ignore: IndexOutOfBoundsException) { //NOSONAR
        // Local file should be different than remote
        TextRange.EMPTY_RANGE
    }
}

fun getLineRange(psiElement: PsiElement): TextRange {
    val project = psiElement.project
    val documentManager = PsiDocumentManager.getInstance(project)
    val document = documentManager.getDocument(psiElement.containingFile.containingFile)
            ?: return TextRange.EMPTY_RANGE
    val line = document.getLineNumber(psiElement.textOffset)
    val lineEndOffset = document.getLineEndOffset(line)
    return TextRange(psiElement.textOffset, lineEndOffset)
}
