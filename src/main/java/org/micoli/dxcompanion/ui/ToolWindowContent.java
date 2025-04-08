package org.micoli.dxcompanion.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.SingleComponentCenteringLayout;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.ConfigurationException;
import org.micoli.dxcompanion.configuration.ConfigurationFactory;
import org.micoli.dxcompanion.configuration.models.Configuration;
import org.micoli.dxcompanion.ui.components.DxIcon;
import org.micoli.dxcompanion.ui.components.DynamicTreeNode;
import org.micoli.dxcompanion.ui.components.FileObserverToggle;
import org.micoli.dxcompanion.ui.components.tree.ActionTreeFactory;
import org.micoli.dxcompanion.ui.components.tree.TreeUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.TimeUnit;

class ToolWindowContent {
    private static final Logger LOGGER = Logger.getInstance(ToolWindowContent.class);
    private final Project project;
    public final JPanel contentPanel = new JPanel();
    private final JComponent mainPanel = new JPanel();
    private Tree tree;
    private Configuration configuration = new Configuration();
    private final ActionTreeFactory actionTreeFactory = new ActionTreeFactory();

    public ToolWindowContent(Project project) {
        this.contentPanel.setLayout(new BorderLayout(2, 2));
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.contentPanel.add(this.mainPanel, BorderLayout.CENTER);
        this.contentPanel.add(createControlsPanel(), BorderLayout.PAGE_END);
        this.mainPanel.setLayout(new BorderLayout());
        this.project = project;
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() -> {
            updateMainPanel();
            refreshComponents();
            this.mainPanel.revalidate();
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    private void refreshComponents() {
        if(this.tree == null){
            return;
        }
        TreeUtils.forEachLeaf(this.tree, (node, path) -> {
            if (node instanceof FileObserverToggle fileObserverToggle) {
                fileObserverToggle.check();
            }
        });
    }

    private void updateMainPanel() {
        Configuration newConfiguration;
        try {
            newConfiguration = ConfigurationFactory.get(project.getBasePath());
        } catch (ConfigurationException e) {
            removeAllComponents();
            this.tree = null;
            this.mainPanel.add(new TextArea(e.getMessage()));
            this.mainPanel.revalidate();
            configuration.serial = null;
            return;
        }
        if (newConfiguration.serial.equals(configuration.serial)) {
            return;
        }

        removeAllComponents();
        this.mainPanel.revalidate();

        this.tree = actionTreeFactory.treeBuilder(newConfiguration.nodes);
        JBScrollPane comp = new JBScrollPane(this.tree);
        comp.setBorder(JBUI.Borders.empty());
        this.mainPanel.add(comp, BorderLayout.CENTER);

        configuration = newConfiguration;
        LOGGER.debug("MainPanel reloaded");
    }

    private void removeAllComponents() {
        if(this.tree == null){
            return;
        }
        TreeUtils.forEachLeaf(this.tree, (node, path) -> {
            if (node instanceof DynamicTreeNode dynamicTreeNode) {
                dynamicTreeNode.unregisterShortcut();
            }
        });
        this.mainPanel.removeAll();
    }

    @NotNull
    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel();
        JButton refreshDateAndTimeButton = new JButton("Refresh", DxIcon.Refresh);
        controlsPanel.add(refreshDateAndTimeButton);
        refreshDateAndTimeButton.addActionListener(e -> {
            updateMainPanel();
            this.mainPanel.revalidate();
        });
        return controlsPanel;
    }
}
