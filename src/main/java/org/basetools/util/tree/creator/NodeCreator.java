package org.basetools.util.tree.creator;

import org.basetools.util.tree.TreeNode;

public interface NodeCreator<N extends TreeNode> {
    N createNode(String id, N nodeIDPath);
}
