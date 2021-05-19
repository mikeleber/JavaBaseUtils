package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DummyTreeNodeIterator implements Iterator<TreeNode> {
    private TreeNode node;

    public DummyTreeNodeIterator(TreeNode contextType) {
        node = contextType;
        while (!isXPathNode(node)) {
            node = getNextNode(node);
        }
    }

    /**
     * @see Iterator#hasNext
     */
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
        return node;
    }

    /**
     * @see Iterator#remove
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the first node for iteration.
     *
     * <p>
     * This method must derive an initial node for iteration from a context node.
     * </p>
     *
     * @param context The starting node.
     * @return The first node in the iteration.
     * @see #getNextNode
     */
    protected TreeNode getFirstNode(TreeNode context) {
        return node;
    }

    /**
     * Get the next node for iteration.
     *
     * <p>
     * This method must locate a following node from the current context node.
     * </p>
     *
     * @param context The current node in the iteration.
     * @return The following node in the iteration, or null if there is none.
     * @see #getFirstNode
     */
    protected TreeNode getNextNode(TreeNode context) {
        return null;
    }

    /**
     * Test whether a DOM node is usable by XPath.
     *
     * @param node The DOM node to test.
     * @return true if the node is usable, false if it should be skipped.
     */
    private boolean isXPathNode(TreeNode node) {
        return true;
    }
}
