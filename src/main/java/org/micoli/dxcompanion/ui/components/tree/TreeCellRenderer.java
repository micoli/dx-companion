package org.micoli.dxcompanion.ui.components.tree;

import org.micoli.dxcompanion.ui.components.DynamicTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

class TreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (value instanceof DynamicTreeNode dynamicTreeNode) {
            setText(dynamicTreeNode.getLabel());
            setIcon(dynamicTreeNode.getIcon());
        } else {
            setIcon(null);
        }

        return this;
    }
}
