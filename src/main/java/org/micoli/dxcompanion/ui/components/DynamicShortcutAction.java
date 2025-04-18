package org.micoli.dxcompanion.ui.components;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DynamicShortcutAction extends AnAction {
    private final Runnable actionCallback;

    public DynamicShortcutAction(String text, String description, Icon icon, Runnable callback) {
        super(text, description, icon);
        this.actionCallback = callback;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (actionCallback != null) {
            actionCallback.run();
        }
    }
}