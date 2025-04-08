package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.Tree;
import org.micoli.dxcompanion.configuration.models.ObservedFile;
import org.micoli.dxcompanion.ui.Notification;

import javax.swing.tree.DefaultTreeModel;
import java.io.*;

public class FileObserverToggle extends DynamicTreeNode {
    private static final Logger LOGGER = Logger.getInstance(FileObserverToggle.class);
    private final String root;
    ObservedFile observedFile;
    String activeRegularExpression;
    String disabledRegularExpression;

    private enum Status {
        Active, Inactive, Unknown
    }
    Status status;

    public FileObserverToggle(Tree tree, ObservedFile observedFile) {
        super(tree, observedFile, IconLoader.getIcon(observedFile.unknownIcon, DxIcon.class));
        this.root = ProjectManager.getInstance().getOpenProjects()[0].getBasePath();
        this.observedFile = observedFile;
        activeRegularExpression = "^" + observedFile.variableName + "=";
        disabledRegularExpression = "^" + observedFile.commentPrefix + "\\s*" + observedFile.variableName + "=";

        setAction(this::toggle);
        registerShortcut(observedFile.label, observedFile.shortcut, this::toggle);
        status = getStatus();
    }

    public void check() {
        Status oldStatus = status;
        status = getStatus();
        switch (status) {
            case Active:
                setLabel(observedFile.label);
                setIcon(IconLoader.getIcon(observedFile.activeIcon, DxIcon.class));
                break;
            case Inactive:
                setLabel("# " + observedFile.label);
                setIcon(IconLoader.getIcon(observedFile.inactiveIcon, DxIcon.class));
                break;
            case Unknown:
                setLabel(observedFile.label + " not present");
                setIcon(IconLoader.getIcon(observedFile.unknownIcon, DxIcon.class));
                break;
        }
        if (!status.equals(oldStatus)){
            ((DefaultTreeModel)tree.getModel()).reload(this);
        }
    }

    void toggle() {
        switch (getStatus()) {
            case Active:
                replaceInFile(false);
                break;
            case Inactive:
                replaceInFile(true);
                break;
        }
        check();
    }

    private Status getStatus() {
        File file = new File(root, observedFile.filePath);
        if (!file.exists()) {
            return Status.Unknown;
        }
        LOGGER.info(file.getAbsolutePath());
        Status result = Status.Unknown;
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                if (line.matches(activeRegularExpression + ".*")) {
                    result = Status.Active;
                }
                if (line.matches(disabledRegularExpression + ".*")) {
                    result = Status.Inactive;
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return result;
    }

    private void replaceInFile(boolean toActive) {
        File file = new File(root, observedFile.filePath);
        if (!file.exists()) {
            return;
        }
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                if (toActive) {
                    result.append(line.replaceFirst(disabledRegularExpression, observedFile.variableName + "="));
                } else {
                    result.append(line.replaceFirst(activeRegularExpression, observedFile.commentPrefix + observedFile.variableName + "="));
                }
                result.append(System.lineSeparator());
                line = reader.readLine();
            }

            reader.close();

            FileWriter fstream = new FileWriter(file, false);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(result.toString());
            out.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        Notification.message(observedFile.variableName + " " + (toActive ? "activated" : "deactivated"));
    }
}