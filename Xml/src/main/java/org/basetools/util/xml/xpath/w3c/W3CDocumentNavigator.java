package org.basetools.util.xml.xpath.w3c;

import org.apache.commons.lang3.StringUtils;
import org.jaxen.Navigator;
import org.jaxen.dom.DocumentNavigator;
import org.w3c.dom.Node;

public class W3CDocumentNavigator extends org.jaxen.dom.DocumentNavigator {

    private final static DocumentNavigator SINGLETON = new W3CDocumentNavigator();
    private final static DocumentNavigator IGNORE_NS_SINGLETON = new W3CDocumentNavigator(true);
    private boolean ignoreNS;

    /**
     * Default constructor.
     */
    public W3CDocumentNavigator() {
    }

    public W3CDocumentNavigator(boolean ignoreNS) {
        this.ignoreNS = ignoreNS;
    }

    /**
     * Get a constant DocumentNavigator for efficiency.
     *
     * @return a constant instance of a DocumentNavigator.
     */

    public static Navigator getInstance() {
        return SINGLETON;
    }

    public static Navigator getIgnoreNamespaceInstance() {
        return IGNORE_NS_SINGLETON;
    }

    @Override
    public String getElementNamespaceUri(Object element) {
        try {
            Node node = (Node) element;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return node.getNamespaceURI();
            }
        } catch (ClassCastException ex) {
        }
        return null;
    }

    /**
     * Get the local name of an element.
     *
     * @param element the target node
     * @return a string representing the unqualified local name
     * if the node is an element, or null otherwise
     */
    @Override
    public String getElementName(Object element) {
        if (isElement(element)) {
            String name = ((Node) element).getLocalName();
            if (name == null) {
                name = ((Node) element).getNodeName();
            }
            if (ignoreNS) {
                name = getNoNSName(name);
            }
            return name;
        }
        return null;
    }

    private String getNoNSName(String name) {
        return StringUtils.substringAfterLast(name, ":");
    }

    /**
     * Get the qualified name of an element.
     *
     * @param element the target node
     * @return a string representing the qualified (i.e. possibly
     * prefixed) name if the argument is an element, or null otherwise
     */
    @Override
    public String getElementQName(Object element) {
        try {
            Node node = (Node) element;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return node.getNodeName();
            }
        } catch (ClassCastException ex) {
        }
        return null;
    }

    /**
     * Get the namespace URI of an attribute.
     *
     * @param attribute the target node
     * @return the namespace name of the specified node
     */
    @Override
    public String getAttributeNamespaceUri(Object attribute) {
        try {
            Node node = (Node) attribute;
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                return node.getNamespaceURI();
            }
        } catch (ClassCastException ex) {
        }
        return null;
    }

    /**
     * Get the local name of an attribute.
     *
     * @param attribute the target node
     * @return a string representing the unqualified local name
     * if the node is an attribute, or null otherwise
     */
    @Override
    public String getAttributeName(Object attribute) {
        if (isAttribute(attribute)) {
            String name = ((Node) attribute).getLocalName();
            if (name == null) {
                name = ((Node) attribute).getNodeName();
            }
            if (ignoreNS) {
                name = getNoNSName(name);
            }
            return name;
        }
        return null;
    }

    /**
     * Get the qualified name of an attribute.
     *
     * @param attribute the target node
     * @return a string representing the qualified (i.e. possibly
     * prefixed) name if the argument is an attribute, or null otherwise
     */
    @Override
    public String getAttributeQName(Object attribute) {
        try {
            Node node = (Node) attribute;
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                return node.getNodeName();
            }
        } catch (ClassCastException ex) {
        }
        return null;
    }
}


