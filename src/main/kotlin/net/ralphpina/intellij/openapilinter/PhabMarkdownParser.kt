package net.ralphpina.intellij.openapilinter

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.ui.UIUtil

private val logger = Logger.getInstance("PhabMarkdownParser")

// Phabricator/Differential have their own link markdown.
// https://secure.phabricator.com/book/phabricator/article/remarkup/#linking-uris
// It looks like so: [[http://www.boring-legal-documents.com/ | legal documents]]
// We want to parse these urls and convert them to HTML links for tooltips
private val REGEX = """(?<message>.+)\sSee \[\[ (?<url>.+) \| (?<linkText>.+)\s(?<extra>.+)""".toRegex()

/**
 * TODO: Figure out this abstraction.
 * I want to provide a way for users to configure
 * a regex to parse their custom messages.
 */
fun String.toHtml(): String {
    // Message format: Name must be alpha snake_case. See [[ https://co.com/Parameters | Operation -> Parameters ]]
    val result = REGEX.matchEntire(trim()) ?: return this
    val (message, url, linkText, _) = result.destructured
    return """
        <html>
            $message    
            <a href="$url"${(if (UIUtil.isUnderDarcula()) " color=\"7AB4C9\" " else "")}>$linkText</a>
        </html>
    """.trimIndent()
}
