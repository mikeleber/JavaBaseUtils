package org.basetools.util.mesh;

import org.basetools.util.tree.TreeNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelationalTreeNodeTest {

    @Test
    void addNode() {
        RelationalTreeNode root = new RelationalTreeNode();
        String[] path = new String[]{"a", "b", "c"};
        TreeNode last = root.addNodeUsingCreator(path, (node, templateNode) -> {
            return new RelationalTreeNode(node, null);
        });
        assertEquals("c", last.getID());
    }

    @Test
    void whenCallAddPath_withGivenXPaths_ThenCreateTree() {
        RelationalTreeNode root = new RelationalTreeNode();
        root.addNodeUsingCreator("/mein/name/ist/hase".split("/"), (node, templateNode) -> {
            return new RelationalTreeNode(node, null);
        });
        root.addNodeUsingCreator("/mein/name/war/hase".split("/"), (node, templateNode) -> {
            return new RelationalTreeNode(node, null);
        });
        root.addNodeUsingCreator("/mein/name/war/oder".split("/"), (node, templateNode) -> {
            return new RelationalTreeNode(node, null);
        });
        assertEquals("mein", root.getFirstChild().getID());
    }
}
