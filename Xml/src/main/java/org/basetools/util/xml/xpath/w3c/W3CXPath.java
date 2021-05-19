package org.basetools.util.xml.xpath.w3c;

import org.jaxen.BaseXPath;
import org.jaxen.Context;
import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenException;
import org.jaxen.util.SingletonList;

import java.util.List;

public final class W3CXPath extends BaseXPath {

    public W3CXPath(String xpathExpr) throws JaxenException {
        this(xpathExpr, false);
    }

    public W3CXPath(String xpathExpr, boolean ignoreNS) throws JaxenException {
        super(xpathExpr, ignoreNS ? W3CDocumentNavigator.getIgnoreNamespaceInstance() : W3CDocumentNavigator.getInstance());
    }

    public W3CXPath(String xpathExpr, DefaultNavigator navigator) throws JaxenException {
        super(xpathExpr, navigator);
    }

    @Override
    protected Context getContext(Object node) {
        if (node instanceof Context) {
            return (Context) node;
        }
        Context fullContext = new Context(getContextSupport());
        List list = new SingletonList(node);
        fullContext.setNodeSet(list);
        return fullContext;
    }
}
