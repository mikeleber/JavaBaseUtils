package org.basetools.util.tree;

import org.junit.jupiter.api.Test;

public class TreeNodeTest {

    @Test
    void addNode() {
        TreeNode root = new TreeNode();
        String[] path = new String[]{"a", "b", "c"};
        TreeNode last = root.addNodeUsingCreator(path, (node, isLast) -> {
            return new TreeNode(node, null);
        });
        assertEquals("c", last.getID());
    }

    @Test
    void whenCallAddPath_withGivenXPaths_ThenCreateTree() {
        TreeNode root = new TreeNode();
        root.addNode("/mein/name/ist/hase".split("/"), null);
        root.addNode("/mein/name/war/hase".split("/"), null);
        root.addNode("/mein/name/war/oder".split("/"), null);
        assertEquals("mein", root.getFirstChild().getID());
    }
}
