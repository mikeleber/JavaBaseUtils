package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;

public class SelfAxisIteratorTree extends SingleObjectIteratorTree<TreeNode> {

    public SelfAxisIteratorTree(TreeNode node, XPathTreeNodeHandler handler) {
        super(node, handler);
    }

    @Override
    public boolean hasNext() {

        if (_handler != null) {
            _handler.hasSelfAxisNext(getObject());
        }
        return super.hasNext();
    }
}
