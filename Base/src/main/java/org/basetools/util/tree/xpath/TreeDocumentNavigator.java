package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeDocument;
import org.basetools.util.tree.TreeNode;
import org.jaxen.DefaultNavigator;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.dom.NamespaceNode;
import org.jaxen.saxpath.SAXPathException;

import java.util.Iterator;
import java.util.Objects;

/**
 * Interface for navigating around the W3C DOM Level 2 object model.
 * <p>
 * This class is not intended for direct usage, but is used by the Jaxen engine during evaluation.
 * </p>
 * <p>
 * This class implements the org.jaxen.DefaultNavigator interface for the Jaxen XPath library, version 1.0beta3 (it is not guaranteed to work with subsequent releases). This adapter allows the Jaxen library to be used to execute XPath queries against any object tree that implements the DOM level 2
 * interfaces.
 * </p>
 * <p>
 * Note: DOM level 2 does not include a node representing an XML Namespace declaration. This navigator will return Namespace decls as instantiations of the custom {@link NamespaceNode}class, and users will have to check result sets to locate and isolate these.
 * </p>
 *
 * @author David Megginson
 * @author James Strachan
 * @see XPath
 * @see NamespaceNode
 */
public class TreeDocumentNavigator extends DefaultNavigator {
    private final static TreeDocumentNavigator SINGLETON = new TreeDocumentNavigator();
    private XPathTreeNodeHandler _nodeHandler;
    private boolean _usIdAsName = false;

    public TreeDocumentNavigator() {
        super();
    }

    public TreeDocumentNavigator(XPathTreeNodeHandler handler) {
        super();
        _nodeHandler = handler;
    }

    public static Navigator getInstance() {
        return SINGLETON;
    }

    public void reset() {
        if (getHandler() != null) {
            getHandler().reset();
        }
    }

    public XPathTreeNodeHandler getHandler() {
        return _nodeHandler;
    }

    @Override
    public Iterator getChildAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return new TreeTreeNodeIterator((TreeNode) contextNode, getHandler());
    }

    @Override
    public Iterator getParentAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return new ParentTreeTreeNodeIterator((TreeNode) contextNode, getHandler());
    }

    @Override
    public Iterator getAttributeAxisIterator(Object contextNode) throws UnsupportedAxisException {

        return new AttributeTreeNodeIterator((TreeNode) contextNode, getHandler());
    }

    @Override
    public Iterator getSelfAxisIterator(Object contextNode) throws UnsupportedAxisException {
        return new SelfAxisIteratorTree((TreeNode) contextNode, getHandler());
    }

    @Override
    public Object getDocumentNode(Object contextNode) {
        return (contextNode instanceof TreeNode) ? ((TreeNode) contextNode) : null;
    }

    @Override
    public String getElementNamespaceUri(Object element) {
        return getElementNamespaceUri(element, false);
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getElementNamespaceUri(java.lang.Object)
     */
    //  @Override
    public String getElementNamespaceUri(Object element, boolean defaultStepIsPrefixed) {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getElementName(java.lang.Object)
     */
    @Override
    public String getElementName(Object element) {
        TreeNode treeNode = (TreeNode) element;
        String name = treeNode.getName();
        if (_usIdAsName) {
            name = treeNode.getID();
        }
        return name == null ? "" : name;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getElementQName(java.lang.Object)
     */
    @Override
    public String getElementQName(Object element) {
        return getElementName(element);
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeNamespaceUri(java.lang.Object)
     */
    @Override
    public String getAttributeNamespaceUri(Object attr) {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeName(java.lang.Object)
     */
    @Override
    public String getAttributeName(Object attr) {
        return getElementName(attr);
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeQName(java.lang.Object)
     */
    @Override
    public String getAttributeQName(Object attr) {
        return getElementName(attr);
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#isDocument(java.lang.Object)
     */
    @Override
    public boolean isDocument(Object object) {
        return (object instanceof TreeDocument);
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#isElement(java.lang.Object)
     */
    @Override
    public boolean isElement(Object object) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#isAttribute(java.lang.Object)
     */
    @Override
    public boolean isAttribute(Object object) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#isNamespace(java.lang.Object)
     */
    @Override
    public boolean isNamespace(Object object) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#isComment(java.lang.Object)
     */
    @Override
    public boolean isComment(Object object) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#isText(java.lang.Object)
     */
    @Override
    public boolean isText(Object object) {

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#isProcessingInstruction(java.lang.Object)
     */
    @Override
    public boolean isProcessingInstruction(Object object) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getCommentStringValue(java.lang.Object)
     */
    @Override
    public String getCommentStringValue(Object comment) {

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getElementStringValue(java.lang.Object)
     */
    @Override
    public String getElementStringValue(Object element) {
        TreeNode node = (TreeNode) element;
        return Objects.toString(node, "NULL");
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeStringValue(java.lang.Object)
     */
    @Override
    public String getAttributeStringValue(Object attr) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespaceStringValue(java.lang.Object)
     */
    @Override
    public String getNamespaceStringValue(Object ns) {
        return null;
    }

    // public void setCachedMode(boolean cachedMode) {
    // _cachedMode = cachedMode;
    // }
    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#parseXPath(java.lang.String)
     */

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getTextStringValue(java.lang.Object)
     */
    @Override
    public String getTextStringValue(Object txt) {
        if (txt != null) {
            return txt.toString();
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespacePrefix(java.lang.Object)
     */
    @Override
    public String getNamespacePrefix(Object ns) {
        return "";
    }

    @Override
    public XPath parseXPath(String xpath) throws SAXPathException {
        return new TreeNodeXPath(xpath, this);
    }
}
