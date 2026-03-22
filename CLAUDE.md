# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IntelliJ Platform Plugin project using Kotlin and Jetpack Compose for UI. The plugin uses Jewel (JetBrains Compose UI library) for building tool window interfaces.

## Build Commands

```bash
# Build the plugin
./gradlew buildPlugin

# Run IDE with plugin (for development/testing)
./gradlew runIde

# Run tests
./gradlew test

# Verify plugin compatibility
./gradlew verifyPlugin

# Publish plugin to JetBrains Marketplace
./gradlew publishPlugin
```

## Architecture

- **Package**: `cw.chaos.cw2_frp`
- **Target IDE**: IntelliJ IDEA 2025.2.4 (since-build: 252.25557)
- **JVM**: Java 21
- **Kotlin**: 2.1.20 with Compose compiler plugin

### Key Files

- `src/main/resources/META-INF/plugin.xml` - Plugin manifest defining extensions, dependencies, and tool windows
- `src/main/kotlin/cw/chaos/cw2_frp/MyToolWindow.kt` - Tool window implementation using Jewel Compose
- `build.gradle.kts` - Build configuration with IntelliJ Platform Gradle Plugin 2.10.2

### Dependencies

- `com.intellij.modules.compose` - Compose UI support
- `org.jetbrains.kotlin` - Kotlin plugin dependency
- Jewel UI components (bundled with IntelliJ Platform)

## UI Development

The plugin uses Jewel (`org.jetbrains.jewel`) for Compose-based UI components within IntelliJ:

- Use `toolWindow.addComposeTab()` to add Compose content to tool windows
- Available Jewel components: `Text`, `OutlinedButton`, `TextField`, etc.
- Layout uses standard Compose modifiers and arrangements

## Plugin Extension Points

Tool windows are registered in `plugin.xml`:
```xml
<extensions defaultExtensionNs="com.intellij">
    <toolWindow id="MyToolWindow" factoryClass="cw.chaos.cw2_frp.MyToolWindowFactory"
                icon="AllIcons.Toolwindows.ToolWindowPalette"/>
</extensions>
```

## Logs

When running via `runIde`, logs are available at:
`build/idea-sandbox/system/log/idea.log`
