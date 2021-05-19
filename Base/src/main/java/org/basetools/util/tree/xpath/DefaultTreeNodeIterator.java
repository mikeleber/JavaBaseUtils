package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class DefaultTreeNodeIterator extends HandledTreeNodeIterator implements ResetIterator<TreeNode> {
    protected List<TreeNode> nodes;
    protected TreeNode node;
    protected int elementCount = 0;
    protected int currentPos = 0;

    public DefaultTreeNodeIterator(XPathTreeNodeHandler handler) {
        super(handler);
    }

    /**
     * Test whether a DOM node is usable by XPath.
     *
     * @param node The DOM node to test.
     * @return true if the node is usable, false if it should be skipped.
     */
    protected boolean isXPathNode(TreeNode node) {
        return true;
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
        while (!isXPathNode(node)) {
            node = getNextNode(node);
        }
        return ret;
    }

    /**
     * @see Iterator#remove
     */
    @Override
    public void remove() {
        // types.remove(currentPos);
        // if (currentPos < types.size()) {
        // node = (TreeNode) types.get(currentPos);
        // } else {
        node = null;
        // }
        // throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        currentPos = 0;
        node = getFirstNode(null);
        currentPos++;
        while (!isXPathNode(node)) {
            node = getNextNode(node);
        }
    }

    /**
     * Get the first node for iteration.
     * <p>
     * This method must derive an initial node for iteration from a context node.
     * </p>
     *
     * @param contextNode The starting node.
     * @return The first node in the iteration.
     * @see #getNextNode
     */
    protected TreeNode getFirstNode(TreeNode contextNode) {
        if (nodes != null && nodes.size() > 0) {
            return nodes.get(0);
        } else {
            return null;
        }
    }

    /**
     * Get the next node for iteration.
     * <p>
     * This method must locate a following node from the current context node.
     * </p>
     *
     * @param contextNode The current node in the iteration.
     * @return The following node in the iteration, or null if there is none.
     * @see #getFirstNode
     */
    protected TreeNode getNextNode(TreeNode contextNode) {
        if (nodes != null && currentPos < nodes.size()) {
            return nodes.get(currentPos++);
        }
        return null;
    }

    /**
     * @see Iterator#hasNext
     */
    @Override
    public boolean hasNext() {
        return (node != null);
    }
}
