package org.leber.util.tree.creator;

import org.leber.util.tree.TreeNode;

public interface NodeCreator<N extends TreeNode> {
    N createNode(String id, N nodeIDPath);
}
