package net.ralphpina.intellij.openapilinter.linter.config

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton
import net.ralphpina.intellij.openapilinter.log
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JPanel

class LinterConfigs(private val project: Project) : SearchableConfigurable {
    private val logger = Logger.getInstance("LinterConfigs")

    private lateinit var executablePathTextField: TextFieldWithHistoryWithBrowseButton
    private lateinit var spectralConfigTextField: TextFieldWithHistoryWithBrowseButton
    private lateinit var rootPanel: JPanel

    override fun createComponent(): JComponent {
        loadSettings()
        addListeners()
        return rootPanel
    }

    override fun isModified() =
        executablePathTextField.text != fetchExecutablePath() || spectralConfigTextField.text != fetchRuleSetPath()

    override fun apply() {
        updateExecutablePath(executablePathTextField.text)
        updateRuleSetPath(spectralConfigTextField.text)
        logger.log("Applying configuration.")
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName() = "OpenAPI Linter"

    override fun getId() = "net.ralphpina.intellij.openapilinter.linter.config.LinterConfigs"

    private fun loadSettings() {
        executablePathTextField.text = fetchExecutablePath()
        spectralConfigTextField.text = fetchRuleSetPath()
    }

    private fun addListeners() {
        executablePathTextField.addActionListener {
            val file = FileChooser.chooseFile(
                FileChooserDescriptorFactory.createSingleFileDescriptor(),
                project,
                null
            ) ?: return@addActionListener
            logger.log("Selected executable: ${file.path}")
            executablePathTextField.text = file.path
        }
        spectralConfigTextField.addActionListener {
            val file = FileChooser.chooseFile(
                FileChooserDescriptorFactory.createSingleFileDescriptor().withShowHiddenFiles(true),
                project,
                null
            ) ?: return@addActionListener
            logger.log("Selected ruleset: ${file.path}")
            spectralConfigTextField.text = file.path
        }
    }
}
