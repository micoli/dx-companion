package org.micoli.dxcompanion.configuration.models;

public class ObservedFile extends AbstractNode {
    String commentPrefix = "#";
    String filePath;
    String variableName;
    String shortcut = null;
    String activeIcon = "actions/inlayRenameInComments.svg";
    String inactiveIcon = "actions/inlayRenameInCommentsActive.svg";
    String unknownIcon = "expui/fileTypes/unknown.svg";

    public String getCommentPrefix() {
        return commentPrefix;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getActiveIcon() {
        return activeIcon;
    }

    public String getInactiveIcon() {
        return inactiveIcon;
    }

    public String getUnknownIcon() {
        return unknownIcon;
    }

    public String getShortcut() {
        return shortcut;
    }
}