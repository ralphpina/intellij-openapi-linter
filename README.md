# intellij-openapi-linter

![Build](https://github.com/ralphpina/intellij-openapi-linter/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/15469.svg)](https://plugins.jetbrains.com/plugin/15469)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/15469.svg)](https://plugins.jetbrains.com/plugin/15469)

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Planned Improvements
- [ ] Fix editing breaks linter output (seems protolint had the same issue?)
- [ ] Add custom regex to parse issue messages
- [ ] Add tests
- [ ] Can I know when plugin is running in debug?
- [ ] Add `ErrorReportSubmitter` implementation [example](https://github.com/uwolfer/gerrit-intellij-plugin/blob/intellij14/src/main/java/com/urswolfer/intellij/plugin/gerrit/errorreport/PluginErrorReportSubmitter.java)
- [ ] Add apply a fix to issues if possible
- [ ] Add support for [quick fixes](https://jetbrains.org/intellij/sdk/docs/tutorials/code_inspections.html#quick-fix-implementation)
- [ ] Publish blog post 

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "intellij-openapi-linter"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/ralphpina/intellij-openapi-linter/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Configuration
There are two configuration options:

![Image of OpenAPI Linter Preferences](docs/openapi_linter_preferences.png)

**Mac users**: by convention the [Spectral ruleset](https://meta.stoplight.io/docs/spectral/docs/guides/2-cli.md#using-a-ruleset-file) is prefixed with a dot: `.spectral.yaml`. This will hide if from the file picker, however, you can use the [Command+Shift+Period](https://osxdaily.com/2011/03/01/show-hidden-files-in-mac-os-x-dialog-boxes-with-commandshiftperiod/) keyboard shortcut to show them.  

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
