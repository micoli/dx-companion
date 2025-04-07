<!-- Plugin description -->
# DxCompanion

## Overview

The plugin creates a tool window in the IDE sidebar that offers two primary functionalities:
1. **Configuration File Toggle** - Visual controls to enable/disable specific variables in configuration files
2. **Shell Command Execution** - Custom action buttons that run defined shell commands in the terminal

## Features

### File Observation System
- Monitors specific configuration files defined in `.dx-companion.json`
- Visually indicates whether specific variables are active or commented out
- Allows one-click toggling between active and inactive states
- Handles different comment prefixes for various file types

### Action System
- Creates custom buttons with configurable labels and icons
- Executes defined shell commands in the terminal or executes registered action
- Supports specifying working directories for command execution

### Configuration
The plugin uses a JSON configuration file (`.dx-companion.json`) in the project root directory, which defines:
- Files to observe with their paths and variable names
- Custom shell commands with labels and icons

### Auto-Refresh
- Automatically checks for configuration changes every 2 seconds
- Provides a manual refresh button for immediate updates

## Technical Details

- Built as an IntelliJ plugin using JetBrains SDK
- Uses Jackson for JSON parsing
- Integrates with IntelliJ Terminal API for command execution
- Utilizes IntelliJ icon system for visual feedback

## Use Cases

The plugin is particularly useful for:
1. Managing feature flags in configuration files (enabling/disabling features)
2. Running common development tasks with a single click (build, deploy, test)
3. Switching between development environments (toggling environment variables)
4. Executing project-specific commands without memorizing syntax

## Integration

The plugin integrates with:
- IntelliJ Platform APIs
- Terminal plugin for command execution
- Project file system for configuration management

This tool serves as a companion for developers to streamline common tasks and reduce cognitive load when working with complex project configurations.

# DxCompanion Configuration File Documentation

## Overview

The `.dx-companion.json` file is the core configuration file for the DxCompanion IntelliJ plugin. It defines the files to observe for toggling and the shell commands that can be executed from the plugin's tool window.

## File Location

The configuration file must be placed in the root directory of your project and named exactly `.dx-companion.json`.

## Schema

The configuration file follows this JSON structure:

```json
{
  "observedFiles": [
    {
      "label": "Display name",
      "filePath": "path/to/file.env",
      "variableName": "VARIABLE_NAME",
      "commentPrefix": "#",
      "shortcut": "ctrl alt l",
      "activeIcon": "actions/inlayRenameInComments.svg",
      "inactiveIcon": "actions/inlayRenameInCommentsActive.svg",
      "unknownIcon": "expui/fileTypes/unknown.svg"
    }
  ],
  "actions": [
    {
      "label": "Action Name",
      "command": "echo 'Hello World'",
      "cwd": "./optional/path",
      "shortcut": "ctrl alt H",
      "icon": "debugger/threadRunning.svg"
    }
  ]
}
```

## Configuration Properties

### Root Properties

| Property | Type | Description |
|----------|------|-------------|
| `observedFiles` | Array | List of files to observe for toggling variables |
| `actions` | Array | List of shell commands to execute |

### ObservedFile Properties

| Property | Type | Required | Default | Description |
|----------|------|----------|---------|-------------|
| `label` | String | Yes | - | The display name for the toggle button |
| `filePath` | String | Yes | - | Path to the file, relative to project root |
| `variableName` | String | Yes | - | Name of the variable to toggle |
| `commentPrefix` | String | No | `#` | Character(s) used to comment out variables |
| `shortcut` | String | No | - | Keyboard shortcut (currently not implemented) |
| `activeIcon` | String | No | `actions/inlayRenameInComments.svg` | Icon path when variable is active |
| `inactiveIcon` | String | No | `actions/inlayRenameInCommentsActive.svg` | Icon path when variable is commented out |
| `unknownIcon` | String | No | `expui/fileTypes/unknown.svg` | Icon path when file is not found |

### Action Properties

| Property | Type | Required | Default | Description                                                             |
|----------|------|----------|---------|-------------------------------------------------------------------------|
| `label` | String | No | `Action` | The display name for the action button                                  |
| `command` | String | Yes | - | Shell command to execute or registered action if started with `action:` |
| `cwd` | String | No | Project root | Working directory for the command                                       |
| `shortcut` | String | No | - | Keyboard shortcut (currently not implemented)                           |
| `icon` | String | No | `debugger/threadRunning.svg` | Icon path for the action button                                         |

## Icon System

DxCompanion uses IntelliJ's built-in icon system. You can specify icons from the IntelliJ icon library by using their path. Some common icon paths include:

- `actions/inlayRenameInComments.svg`
- `actions/inlayRenameInCommentsActive.svg`
- `expui/fileTypes/unknown.svg`
- `debugger/threadRunning.svg`
- `expui/actions/buildAutoReloadChanges.svg`

## Examples

### Basic Example

```json
{
  "observedFiles": [
    {
      "label": "Debug Mode",
      "filePath": ".env",
      "variableName": "DEBUG_MODE"
    }
  ],
  "actions": [
    {
      "label": "Build Project",
      "command": "mvn clean install"
    }
  ]
}
```

### Comprehensive Example

```json
{
  "observedFiles": [
    {
      "label": "Debug Mode",
      "filePath": ".env",
      "variableName": "DEBUG_MODE",
      "commentPrefix": "#"
    },
    {
      "label": "Feature Flag",
      "filePath": "config/settings.properties",
      "variableName": "FEATURE_ENABLED",
      "commentPrefix": "#",
      "activeIcon": "actions/execute.svg",
      "inactiveIcon": "actions/suspend.svg"
    },
    {
      "label": "Docker Compose",
      "filePath": "docker-compose.yml",
      "variableName": "LOCAL_DATABASE",
      "commentPrefix": "#"
    }
  ],
  "actions": [
    {
      "label": "Build",
      "command": "mvn clean install",
      "icon": "actions/compile.svg"
    },
    {
      "label": "Open IdeScriptingConsole",
      "command": "action:IdeScriptingConsole",
      "icon": "actions/compile.svg"
    },
    {
      "label": "Run Tests",
      "command": "mvn test",
      "cwd": "./tests",
      "icon": "runConfigurations/testState.svg"
    },
    {
      "label": "Deploy",
      "command": "./scripts/deploy.sh",
      "icon": "actions/upload.svg"
    }
  ]
}
```

## How It Works

### File Observation

The plugin monitors the specified files for variables that are either active or commented out. For example, in an `.env` file:

```
# This is a commented out variable
# DEBUG_MODE=true  
```

Clicking the toggle button would change it to:

```
# This is a commented out variable
DEBUG_MODE=true
```

The plugin detects the difference between active variables and commented ones using the `commentPrefix` and `variableName` configuration.

### Actions

When you click an action button, the plugin:

1. Opens a terminal session in the IntelliJ Terminal tool window
2. Changes to the specified working directory (if provided)
3. Executes the specified command
   nb: if command starts with `action:`, as in `action:IdeScriptingConsole`, then the registered action will be executed (list of actions https://centic9.github.io/IntelliJ-Action-IDs/)

This allows for quick execution of common development tasks directly from the IDE.

## Troubleshooting

If the plugin encounters issues with your configuration file, it will display an error message in the tool window. Common issues include:

- Missing `.dx-companion.json` file in project root
- Invalid JSON syntax
- Missing required properties

The plugin automatically reloads the configuration every 2 seconds, so changes to the configuration file will be reflected without restarting the IDE.

<!-- Plugin description end -->

## Architecture

The project follows a clean architecture with clear separation between models, UI components, and core functionality:

### Core Components

- **Models**: Contains simple data classes representing configuration elements
  - `Configuration`: Root configuration container with arrays of observed files and actions
  - `ObservedFile`: Defines configuration files to observe with toggle capabilities
  - `Action`: Defines shell commands that can be executed

- **UI Components**: Handles display and interaction
  - `ToolWindowFactory`: Creates the tool window in the IDE
  - `ToolWindowContent`: Manages the visual content and updates
  - `FileObserverToggle`: UI control for toggling configuration variables
  - `ActionButton`: UI control for executing shell commands or registered actions

- **Configuration Management**:
  - `ConfigurationFactory`: Reads and parses the `.dx-companion.json` configuration file
  - `ConfigurationException`: Custom exception for configuration errors
