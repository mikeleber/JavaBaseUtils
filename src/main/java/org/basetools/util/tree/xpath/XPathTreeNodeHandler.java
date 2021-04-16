package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;

public class XPathTreeNodeHandler {

    public boolean accept(TreeNode aNode) {
        return true;
    }

    public boolean acceptAttribute(TreeNode aChildType) {
        return false;
    }

    public boolean hasSelfAxisNext(TreeNode aNode) {
        return true;
    }

    public void reset() {
    }
}
