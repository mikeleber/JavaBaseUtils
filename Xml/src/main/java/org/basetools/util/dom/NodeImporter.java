/*
 * Created on 21.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.basetools.util.dom;

import org.apache.commons.lang3.StringUtils;
import org.basetools.util.xml.xpath.w3c.W3CXPathExecuterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class NodeImporter {
    public static final int ENUMERATION_MAPPING_STRATEGY_REPLACE = 1;
    public static final int ENUMERATION_MAPPING_STRATEGY_APPEND = 2;
    public static final int ENUMERATION_MAPPING_STRATEGY_ADDCHILD = 3;
    public static final int ENUMERATION_MAPPING_STRATEGY_INSERTBEFORE = 4;
    public static final int ENUMERATION_MAPPING_STRATEGY_INSERTAFTER = 5;
    public static final int ENUMERATION_MAPPING_STRATEGY_APPEND_CLONED_ROOT = 6;
    public static final int ENUMERATION_MAPPING_STRATEGY_REPLACE_CLONED_ROOT = 7;
    private static final String ELT_XML_DATA = "xmlData";

    private static Logger LOGGER = LoggerFactory.getLogger(NodeImporter.class.getName());

    public static void doImport(NodeList from, Node to, int importStrategy, String xsdPrefix) {
        for (int i = 0; i < from.getLength(); i++) {
            doImport(from.item(i), to, importStrategy, xsdPrefix);
        }
    }

    public static Node doImport(Node from, Node to, int importStrategy, String xsdPrefix) {
        Node result = null;
        if (from != null && to != null) {
            Node importedNode = importNodeToDom(from, to, xsdPrefix);
            Node toParent = to.getParentNode();
            Node clone = null;
            switch (importStrategy) {
                case ENUMERATION_MAPPING_STRATEGY_REPLACE:
                    result = toParent.replaceChild(importedNode, to);
                    break;
                case ENUMERATION_MAPPING_STRATEGY_APPEND:
                    result = insertAfter(importedNode, to);
                    break;
                case ENUMERATION_MAPPING_STRATEGY_ADDCHILD:
                    result = to.appendChild(importedNode);
                    break;
                case ENUMERATION_MAPPING_STRATEGY_INSERTBEFORE:
                    result = toParent.insertBefore(importedNode, to);
                    break;
                case ENUMERATION_MAPPING_STRATEGY_INSERTAFTER:
                    result = insertAfter(importedNode, to);
                    break;
                case ENUMERATION_MAPPING_STRATEGY_APPEND_CLONED_ROOT:
                    clone = to.cloneNode(false);
                    insertAfter(clone, to);
                    replaceChilds(importedNode, clone);
                    result = clone;
                    break;
                case ENUMERATION_MAPPING_STRATEGY_REPLACE_CLONED_ROOT:
                    clone = to.cloneNode(false);
                    toParent.replaceChild(clone, to);
                    replaceChilds(importedNode, clone);
                    result = clone;
                    break;
            }
        }
        return result;
    }

    public static Node insertAfter(Node newChild, Node refChild) {
        Node toParent = refChild.getParentNode();
        Node insBefore = refChild.getNextSibling();
        if (insBefore != null) {
            toParent.insertBefore(newChild, insBefore);
        } else {
            toParent.appendChild(newChild);
        }
        return newChild;
    }

    public static Node importNodeToDom(Node from, Node to, String xsdPrefix) {
        Node importedNode = importReferencedNode(to, from);
        updatePrefix(importedNode, xsdPrefix);
        return importedNode;
    }

    public static Node importNodeToDom(Document ownerDoc, Node from, String xsdPrefix) {
        Node importedNode = importReferencedNode(ownerDoc, from);
        updatePrefix(importedNode, xsdPrefix);
        return importedNode;
    }

    public static void updatePrefix(Node importNode, String prefix) {
        try {
            if (!StringUtils.equals(importNode.getPrefix(), prefix)) {
                importNode.setPrefix(prefix);
            }
        } catch (Throwable t) {
            // never mind
        }
        if (importNode.getChildNodes().getLength() > 0) {
            for (int i = 0; i < importNode.getChildNodes().getLength(); i++) {
                updatePrefix(importNode.getChildNodes().item(i), prefix);
            }
            NamedNodeMap nodeMap = importNode.getAttributes();
            if (nodeMap.getLength() > 0) {
                for (int i = 0; i < nodeMap.getLength(); i++) {
                    try {
                        (nodeMap.item(i)).setPrefix(prefix);
                    } catch (Throwable t) {
                        // never mind
                    }
                }
            }
        }
    }

    public static Node importReferencedNode(Node toNode, Node fromNode) {
        return toNode.getOwnerDocument().importNode(fromNode, true);
    }

    public static Node importReferencedNode(Document ownerDoc, Node fromNode) {
        return ownerDoc.importNode(fromNode, true);
    }

    public static Document injectXMLData(Document toDocument, Document fromDocument, String toXPath, String fromXPath, int strategy) {

        Node targetNode = null;
        try {
            if (toXPath == null || toXPath.length() == 0) {
                toXPath = ".";
            }
            if (fromXPath == null || fromXPath.length() == 0) {
                fromXPath = ".";
            }
            List targetNodeList = W3CXPathExecuterImpl.getInstance().processXPath(toDocument.getDocumentElement(), toXPath);

            if (targetNodeList == null || targetNodeList.size() == 0) {
            } else {
                targetNode = (Node) targetNodeList.get(0);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return injectXMLData(toDocument, fromDocument, targetNode, fromXPath, strategy);
    }

    public static Node replaceChilds(Node fromNode, Node toNode) {
        //
        removeAllChilds(toNode);
        NodeList srcNodes = fromNode.getChildNodes();
        for (int c = 0; c < srcNodes.getLength(); c++) {
            doImport(srcNodes.item(c), toNode, ENUMERATION_MAPPING_STRATEGY_ADDCHILD, null);
        }
        return toNode;
    }

    public static void removeAllChilds(Node toNode) {
        NodeList childs = toNode.getChildNodes();
        for (int c = 0; c < childs.getLength(); c++) {
            toNode.removeChild(childs.item(c));
        }
    }

    public static Document injectXMLData(Document toDocument, Document fromDocument, Node targetNode, List fromList, int strategy) {
        Document resultDom = null;
        try {
            if (fromList == null) {
                fromList = new ArrayList();
                fromList.add(fromDocument.getDocumentElement());
            }
            resultDom = toDocument;
            if (targetNode == null) {
                // create xml-data node
                Element xmlData = toDocument.createElement(ELT_XML_DATA);
                Node textNode = toDocument.createTextNode("");
                xmlData.appendChild(textNode);
                toDocument.getDocumentElement().appendChild(xmlData);
                NodeImporter.doImport(fromDocument.getDocumentElement(), textNode, strategy, null);
            } else {
                for (int f = 0; f < fromList.size(); f++) {
                    NodeImporter.doImport((Node) fromList.get(f), targetNode, strategy, null);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return resultDom;
    }

    public static Document injectXMLData(Document toDocument, Document fromDocument, Node targetNode, String fromXPath, int strategy) {
        Document resultDom = null;
        try {
            if (fromXPath == null || fromXPath.length() == 0) {
                fromXPath = ".";
            }
            resultDom = toDocument;
            if (targetNode == null) {
                // create xml-data node
                Element xmlData = toDocument.createElement(ELT_XML_DATA);
                Node textNode = toDocument.createTextNode("");
                xmlData.appendChild(textNode);
                toDocument.getDocumentElement().appendChild(xmlData);
                NodeImporter.doImport(fromDocument.getDocumentElement(), textNode, strategy, null);
            } else {
                List fromList = W3CXPathExecuterImpl.getInstance().processXPath(fromDocument.getDocumentElement(), fromXPath);
                for (int f = 0; f < fromList.size(); f++) {
                    NodeImporter.doImport((Node) fromList.get(f), targetNode, strategy, null);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return resultDom;
    }

    public static Document mergeXML(Document toDocument, Document fromDocument, String toXPath, String fromXPath, int strategy, String glue, Node insertTarget) {
        Document resultDom = null;
        List<Node> toAdd, toUpdate, fromToAdd = null;
        try {
            if (toXPath == null || toXPath.length() == 0) {
                toXPath = ".";
            }
            if (fromXPath == null || fromXPath.length() == 0) {
                fromXPath = ".";
            }
            List<Node> toNodeList = W3CXPathExecuterImpl.getInstance().processXPath(toDocument.getDocumentElement(), toXPath);
            List<Node> fromNodeList = W3CXPathExecuterImpl.getInstance().processXPath(fromDocument.getDocumentElement(), fromXPath);
            toAdd = new ArrayList<>();
            toUpdate = new ArrayList<>();
            fromToAdd = new ArrayList<>();
            List<Node> result = new ArrayList<>();
            resultDom = toDocument;
            for (int t = 0; t < toNodeList.size(); t++) {
                Node targetNode = toNodeList.get(t);
                String syncTargetValue = W3CDomUtil.getChildText(targetNode, glue);
                Node fromNode = getNode(fromNodeList, glue, syncTargetValue);
                if (fromNode == null) {
                    toAdd.add(targetNode);
                } else {
                    toUpdate.add(targetNode);
                }
            }
            // die zu aktualisierenden
            for (int f = 0; f < fromNodeList.size(); f++) {
                Node fromNode = fromNodeList.get(f);
                String syncTargetValue = W3CDomUtil.getChildText(fromNode, glue);
                Node toUpdateNode = getNode(toUpdate, glue, syncTargetValue);
                if (toUpdateNode == null) {
                    result.add(fromNode); // importedNode
                } else {
                    if (W3CDomUtil.getChildText(toUpdateNode, "columnName") != null) {
                        result.add(toUpdateNode);
                    }
                }
            }
            // die zu addierenden
            for (int f = 0; f < toAdd.size(); f++) {
                result.add(toAdd.get(f));
            }
            // alle entfernen
            for (int t = 0; t < toNodeList.size(); t++) {
                toNodeList.get(t).getParentNode().removeChild(toNodeList.get(t));
            }
            // add all field nodes to target dom
            for (int r = 0; r < result.size(); r++) {
                Node fNode = result.get(r);
                NodeImporter.doImport(fNode, insertTarget, ENUMERATION_MAPPING_STRATEGY_INSERTAFTER, null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return resultDom;
    }

    public static Node getNode(List<Node> from, String glue, String key) {
        for (int f = 0; f < from.size(); f++) {
            Node fromNode = from.get(f);
            String syncFromValue = W3CDomUtil.getChildText(fromNode, glue);
            if (syncFromValue != null && syncFromValue.equals(key)) {
                return fromNode;
            }
        }
        return null;
    }

    public static Node removeNode(Node toRemove) {
        Node toRemParent = toRemove.getParentNode();
        Node removed = null;
        if (toRemParent != null) {
            removed = toRemParent.removeChild(toRemove);
        }
        return removed;
    }
}
