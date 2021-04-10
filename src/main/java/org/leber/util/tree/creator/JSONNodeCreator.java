package org.leber.util.tree.creator;

import org.leber.util.tree.TreeNode;

import javax.json.JsonObject;
import javax.json.JsonValue;

public interface JSONNodeCreator<N extends TreeNode> {
    N createNode(N current, JsonObject aDef);

    boolean createNode(String key, N current, JsonValue value);
}
