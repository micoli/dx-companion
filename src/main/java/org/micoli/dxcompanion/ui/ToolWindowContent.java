package org.micoli.dxcompanion.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.concurrency.AppExecutorUtil;
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
import java.awt.*;
import java.util.concurrent.TimeUnit;

class ToolWindowContent {
    private static final Logger LOGGER = Logger.getInstance(ToolWindowContent.class);
    private final Project project;
    private final JPanel contentPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();
    private Tree tree;
    private Configuration configuration = new Configuration();
    private final ActionTreeFactory actionTreeFactory = new ActionTreeFactory();

    public ToolWindowContent(Project project) {
        contentPanel.setLayout(new BorderLayout(2, 2));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(createControlsPanel(), BorderLayout.PAGE_END);
        this.project = project;
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() -> {
            updateMainPanel();
            refreshComponents();
            mainPanel.revalidate();
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    private void refreshComponents() {
        if(tree == null){
            return;
        }
        TreeUtils.forEachLeaf(tree, (node, path) -> {
            if (node instanceof FileObserverToggle fileObserverToggle) {
                fileObserverToggle.check();
            }
        });
    }

    private void updateMainPanel() {
        Configuration newConfiguration;
        try {
            newConfiguration = ConfigurationFactory.get(project.getBaseDir().getCanonicalPath());
        } catch (ConfigurationException e) {
            removeAllComponents();
            tree = null;
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(new TextArea(e.getMessage()));
            mainPanel.revalidate();
            configuration.setSerial(null);
            return;
        }
        assert newConfiguration != null;
        if (newConfiguration.getSerial().equals(configuration.getSerial())) {
            return;
        }

        removeAllComponents();
        mainPanel.revalidate();
        mainPanel.setLayout(new BorderLayout());

        tree = actionTreeFactory.treeBuilder(newConfiguration.getNodes());
        mainPanel.add(new JBScrollPane(tree), BorderLayout.CENTER);

        configuration = newConfiguration;
        LOGGER.debug("MainPanel reloaded");
    }

    private void removeAllComponents() {
        if(tree == null){
            return;
        }
        TreeUtils.forEachLeaf(tree, (node, path) -> {
            if (node instanceof DynamicTreeNode dynamicTreeNode) {
                dynamicTreeNode.unregisterShortcut();
            }
        });
        mainPanel.removeAll();
    }

    @NotNull
    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel();
        JButton refreshDateAndTimeButton = new JButton("Refresh", DxIcon.Refresh);
        controlsPanel.add(refreshDateAndTimeButton);
        refreshDateAndTimeButton.addActionListener(e -> {
            updateMainPanel();
            mainPanel.revalidate();
        });
        return controlsPanel;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
