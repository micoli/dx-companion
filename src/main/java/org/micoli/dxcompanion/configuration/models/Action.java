package org.micoli.dxcompanion.configuration.models;

public class Action extends AbstractNode {
    String command;
    String cwd = null;
    String shortcut = null;
    String icon = "debugger/threadRunning.svg";

    public String getCommand() {
        return command;
    }

    public String getCwd() {
        return cwd;
    }

    public String getShortcut() {
        return shortcut;
    }

    public String getIcon() {
        return icon;
    }
}