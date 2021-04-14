package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;

import java.util.ArrayList;

public final class TreeTreeNodeIterator extends DefaultTreeNodeIterator implements ResetIterator<TreeNode> {

    public TreeTreeNodeIterator(TreeNode contextType, XPathTreeNodeHandler handler) {
        super(handler);
        if (contextType != null) {
            initChilds(contextType);
            node = getFirstNode(contextType);
            currentPos++;
            while (!isXPathNode(node)) {
                node = getNextNode(node);
            }
        }
    }

    private void initChilds(TreeNode aNode) {
        nodes = new ArrayList<>(aNode.getChildren());
    }
}
