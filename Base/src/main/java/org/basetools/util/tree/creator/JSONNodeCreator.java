package org.basetools.util.tree.creator;

import net.minidev.json.JSONAware;
import org.basetools.util.tree.TreeNode;


public interface JSONNodeCreator<N extends TreeNode> {
    N createNode(N current, JSONAware aDef);

    boolean createNode(String key, N current, JSONAware value);
}
