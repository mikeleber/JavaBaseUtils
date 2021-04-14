package org.basetools.util.tree.xpath;

public abstract class HandledTreeNodeIterator {
    protected XPathTreeNodeHandler _handler;

    public HandledTreeNodeIterator(XPathTreeNodeHandler handler) {
        _handler = handler;
    }

    public XPathTreeNodeHandler getHandler() {
        return _handler;
    }
}