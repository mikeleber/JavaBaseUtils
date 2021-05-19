package org.basetools.util.tree.xpath;

import org.jaxen.BaseXPath;
import org.jaxen.Context;
import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenException;
import org.jaxen.util.SingletonList;

import java.util.List;

/**
 * An XPath implementation for the W3C DOM model
 *
 * <p>
 * This is the main entry point for matching an XPath against a DOM tree. You create a compiled XPath object, then match it against one or more context nodes using the {@link #selectNodes} method, as in the following example:
 * </p>
 *
 * <pre>
 * XPath path = new DOMXPath(&quot;a/b/c&quot;);
 * List results = path.selectNodes(domNode);
 * </pre>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 * @see BaseXPath
 */
public final class TreeNodeXPath extends BaseXPath {

    public TreeNodeXPath(String xpathExpr, DefaultNavigator navigator) throws JaxenException {
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
