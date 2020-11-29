package net.ralphpina.intellij.openapilinter

import com.google.common.collect.Sets
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Copy/pasted from https://github.com/sonar-intellij-plugin/sonar-intellij-plugin/blob/master/src/main/java/org/intellij/sonar/util/Finders.java
 * I converted it to Kotlin using Intellij.
 * Then I manually fixed things like the use of Optional<>.
 */
fun findFirstElementAtLine(file: PsiFile, line: Int): PsiElement? {
    val ijLine = line - 1
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

fun findDocumentFromPsiFile(psiFile: PsiFile) =
        PsiDocumentManager.getInstance(psiFile.project).getDocument(psiFile)

fun findEditorsFrom(document: Document) =
        EditorFactory.getInstance().getEditors(document).toList()

fun findRangeHighlighterAtLine(editor: Editor, line: Int?): RangeHighlighter? {
    if (line == null) return null
    val markupModel = editor.markupModel
    val highlighters = markupModel.allHighlighters
    for (highlighter in highlighters) {
        val logicalPosition = editor.offsetToLogicalPosition(highlighter.startOffset)
        val lineOfHighlighter = logicalPosition.line
        if (lineOfHighlighter == line - 1) {
            return highlighter
        }
    }
    return null
}

fun findAllRangeHighlightersFrom(document: Document): Set<RangeHighlighter> {
    val highlighters: MutableSet<RangeHighlighter> = Sets.newHashSet()
    for (editor in findEditorsFrom(document)) {
        addHighlightersFromEditor(highlighters, editor)
    }
    return highlighters
}

private fun addHighlightersFromEditor(highlighters: MutableSet<RangeHighlighter>, editor: Editor) {
    ApplicationManager.getApplication().invokeAndWait({
        val highlightersFromCurrentEditor = editor.markupModel.allHighlighters
        highlighters.addAll(Sets.newHashSet(*highlightersFromCurrentEditor))
    }, ModalityState.any())
}

fun findLineOfRangeHighlighter(highlighter: RangeHighlighter, editor: Editor): Int {
    val logicalPosition = editor.offsetToLogicalPosition(highlighter.startOffset)
    return logicalPosition.line
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