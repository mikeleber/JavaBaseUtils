package org.leber.util.tree;

import java.util.Map;

public interface TreeRenderer<T, U> {
    public void render(TreeNode<T, U> genericTreeNode, boolean in, Map traverseData);
}
