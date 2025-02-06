package org.basetools.util.xml.xpath.w3c;

import org.basetools.util.dom.W3CDomUtil;
import org.jaxen.JaxenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public final class W3CXPathExecuterImpl {
    private static W3CXPathExecuterImpl _singleton = null;
    private static Logger LOGGER = LoggerFactory.getLogger(W3CDomUtil.class.getName());

    private W3CXPathExecuterImpl() {
        super();
    }

    public static W3CXPathExecuterImpl getInstance() {
        if (_singleton == null) {
            synchronized (W3CXPathExecuterImpl.class) {
                if (_singleton == null) {
                    _singleton = new W3CXPathExecuterImpl();
                }
            }
            return _singleton;
        } else {
            return _singleton;
        }
    }

    public List<Node> processXPath(Node rootElement, String xpath) throws TransformerException {
        return processXPath(rootElement, xpath, false);
    }

    public List<Node> processXPath(Node rootElement, String xpath, boolean ignoreNS) throws TransformerException {
        List<Node> results = null;
        try {
            W3CXPath typeXpath = new W3CXPath(xpath, ignoreNS);
            Hashtable namespaces = W3CDomUtil.getNameSpaces(rootElement.getOwnerDocument());
            Enumeration namespacesEnum = namespaces.keys();
            String nsKey = null;
            String nsURI = null;
            if (ignoreNS) {

                typeXpath.setNamespaceContext(null);
            } else {

                while (namespacesEnum.hasMoreElements()) {
                    nsKey = (String) namespacesEnum.nextElement();
                    nsURI = (String) namespaces.get(nsKey);
                    typeXpath.addNamespace(nsKey, nsURI);
                }
            }
            results = typeXpath.selectNodes(rootElement);
        } catch (org.jaxen.XPathSyntaxException e) {
            LOGGER.error("XPath-Syntax Error for ({}) Reason:{}", xpath, e.getMultilineMessage());
        } catch (JaxenException e) {
            LOGGER.error("Problem with xpaht:", e);
        }
        return results;
    }

    public String getElementValue(Element element, String xpath) {
        return getElementValue(element, xpath, false);
    }

    public String getElementValue(Element element, String xpath, boolean ignoreNS) {
        if (xpath != null && xpath.length() > 0) {
            try {
                List<?> childs = W3CXPathExecuterImpl.getInstance().processXPath(element, xpath, false);
                if (childs.size() > 0) {
                    Object aChild = childs.get(0);
                    if (aChild instanceof Node) {
                        return W3CDomUtil.getNodeText((Node) aChild);
                    } else {
                        return aChild != null ? aChild.toString() : null;
                    }
                } else {

                    LOGGER.warn("can't find value for:{}", xpath);
                }
            } catch (Exception e) {
                LOGGER.debug(e.getMessage());
            }
        }
        return null;
    }

    public void setElementValue(Element element, String xpath, String value) {
        try {
            List childs = W3CXPathExecuterImpl.getInstance().processXPath(element, xpath, false);
            if (childs.size() > 0) {
                Node node = (Node) childs.get(0);
                if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                    node.setNodeValue(value);
                } else {
                    W3CDomUtil.setText(node, value);
                }
            }
        } catch (Exception e) {
        }
    }
}