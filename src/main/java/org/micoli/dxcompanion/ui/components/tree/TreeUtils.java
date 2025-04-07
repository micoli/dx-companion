package org.micoli.dxcompanion.ui.components.tree;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeUtils {
    @FunctionalInterface
    public interface LeafProcessor {
        void process(DefaultMutableTreeNode node, TreePath path);
    }

    public static void forEachLeaf(Tree tree, LeafProcessor leafProcessor) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        forEachLeafRecursive(root, leafProcessor);
    }

    private static void forEachLeafRecursive(DefaultMutableTreeNode node, LeafProcessor leafProcessor) {
        if (node.isLeaf()) {
            TreePath path = new TreePath(node.getPath());
            leafProcessor.process(node, path);
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            forEachLeafRecursive(child, leafProcessor);
        }
    }
}