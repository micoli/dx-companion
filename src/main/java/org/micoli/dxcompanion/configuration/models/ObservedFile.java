package org.micoli.dxcompanion.configuration.models;

public final class ObservedFile extends AbstractNode {
    public String commentPrefix = "#";
    public String filePath;
    public String variableName;
    public String shortcut = null;
    public String activeIcon = "actions/inlayRenameInComments.svg";
    public String inactiveIcon = "actions/inlayRenameInCommentsActive.svg";
    public String unknownIcon = "expui/fileTypes/unknown.svg";
}