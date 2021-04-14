package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class AttributeTreeNodeIterator extends DefaultTreeNodeIterator implements Iterator<TreeNode> {
    protected int currentPos = 0;
    private TreeNode node;

    public AttributeTreeNodeIterator(TreeNode contextType, XPathTreeNodeHandler handler) {
        super(handler);
        if (contextType != null) {
            initChilds(contextType);
            node = getFirstNode(contextType);
        }
    }

    @Override
    public boolean hasNext() {
        return (node != null);
    }

    /**
     * @see Iterator#next
     */
    @Override
    public TreeNode next() {
        if (node == null) {
            throw new NoSuchElementException();
        }
        TreeNode ret = node;
        node = getNextNode(node);
        return ret;
    }

    private void initChilds(TreeNode aNode) {
        nodes = Collections.EMPTY_LIST;
    }

    @Override
    public void remove() {
        nodes.remove(currentPos);
        if (currentPos < nodes.size()) {
            node = nodes.get(currentPos);
        } else {
            node = null;
        }
    }

    @Override
    protected TreeNode getFirstNode(TreeNode context) {
        if (nodes.size() > 0) {
            return nodes.get(currentPos++);
        } else {
            return null;
        }
    }

    @Override
    protected TreeNode getNextNode(TreeNode context) {
        if (currentPos < nodes.size()) {
            return nodes.get(currentPos++);
        }
        return null;
    }
}
