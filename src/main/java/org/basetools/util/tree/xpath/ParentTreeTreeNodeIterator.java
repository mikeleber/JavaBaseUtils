package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ParentTreeTreeNodeIterator extends HandledTreeNodeIterator implements ResetIterator<TreeNode> {
    boolean read = false;
    private TreeNode node;

    public ParentTreeTreeNodeIterator(TreeNode contextType, XPathTreeNodeHandler handler) {
        super(handler);
        // _onlySerializable = choosenNodeOnly;
        initParents(contextType);
        read = (node == null);
    }

    /**
     * @see Iterator#hasNext
     */
    @Override
    public boolean hasNext() {
        return (!read);
    }

    /**
     * @see Iterator#next
     */
    @Override
    public TreeNode next() {
        if (read) {
            throw new NoSuchElementException();
        }
        read = true;
        return node;
    }

    private void initParents(TreeNode node) {
        TreeNode aParent = node;
        while ((aParent = aParent.getParent()) != null && aParent.isList()) {
        }
        if (aParent != null) {
            node = aParent;
        }
    }

    /**
     * @see Iterator#remove
     */
    @Override
    public void remove() {
        read = false;
    }

    @Override
    public void reset() {
        read = false;
    }
}
