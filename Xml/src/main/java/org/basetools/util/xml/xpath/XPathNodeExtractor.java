package org.basetools.util.xml.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class XPathNodeExtractor {

    public static <T> List<XPathNode<T>> tokenize(String xpath, boolean check) throws XPathError {
        ArrayList<XPathNode<T>> nodes = new ArrayList<>();
        XPathNode<T> node = new XPathNode<>("root", 0, check);
        while ((node = extractNode(node._endPos, xpath, check)) != null) {
            nodes.add(node);
            if (node.getNodeExpression() != null) {
                XPathNode<T> expNode = new XPathNode<>();
                expNode.setNodeName(node.getNodeExpression());
                expNode.setNodeType(XPathNode.IDX_NODE);
                nodes.add(expNode);
            }
        }
        return nodes;
    }

    public static <T> XPathNode<T> extractNode(int start, String xpath, boolean check) throws XPathError {
        return extractNode(start, xpath, check, null);
    }

    public static <T> XPathNode<T> extractNode(int start, String xpath, boolean check, Map<String, String> nsMapping) throws XPathError {
        XPathNode<T> node = null;
        String nodeName = null;
        int lastPos = -1;
        int calculatedNodeEnd = getNodeEndPos(xpath, start);
        if (calculatedNodeEnd >= 0 && start < xpath.length()) {
            nodeName = xpath.substring(start, calculatedNodeEnd);
            lastPos = calculatedNodeEnd + 1;
            if (nodeName == null) {
                node = new XPathNode<>(xpath.substring(start), xpath.length(), check, nsMapping);
            } else {
                node = new XPathNode<>(nodeName, lastPos, check, nsMapping);
            }
            if (node.getEndPos() >= xpath.length()) {
                node.setLast(true);
            }
        }
        return node;
    }

    public static int getNodeEndPos(String xpath, int start) throws XPathError {
        int endPos = -1;
        int oBCount = 0;
        int cBCount = 0;
        for (int i = start; i < xpath.length(); i++) {
            char posChar = xpath.charAt(i);
            switch (posChar) {
                case '[':
                    oBCount++;
                    endPos = i + 1;
                    break;
                case ']':
                    cBCount++;
                    endPos = i + 1;
                    break;
                case '/':
                    if (oBCount == cBCount) {
                        if (i == 0 && start == 0) {
                            // don't return go ahead
                        } else {
                            endPos = i;
                            return endPos;
                        }
                    } else {
                        // must be a expression do ahead
                    }
                    break;
                case '|':
                    if (oBCount == cBCount) {
                        throw XPathNode.ERROR_TO_COMPLEX;
                    }
                default:
                    endPos = i + 1;
                    break;
            }
        }
        return endPos;
    }

    public static <T> XPathNode<T> tokenizeNext(String xpath, XPathNode<T> oldNode, boolean check, boolean reuse) throws XPathError {
        return tokenizeNext(xpath, oldNode, check, null, reuse);
    }

    public static <T> XPathNode<T> tokenizeNext(String xpath, XPathNode<T> oldNode, boolean check, Map<String, String> nsMapping, boolean reuse) throws XPathError {
        return tokenizeNext(xpath, oldNode, check, nsMapping, null, reuse);
    }

    public static <T> XPathNode<T> tokenizeNext(String xpath, XPathNode<T> oldNode, boolean check, Map<String, String> nsMapping, Map<?, ?> varMapping, boolean reuse) throws XPathError {
        XPathNode<T> node = null;
        boolean evalVar = false;
        if (oldNode == null) {
            int start = 0;
            if (xpath.startsWith("/")) {
                start = 1;
            } else if (xpath.startsWith(".")) {
            } else {
                evalVar = true;
            }
            node = new XPathNode<>("root", start, check, true);
        } else {
            node = oldNode;
        }
        node = extractNode(node, xpath, check, reuse);
        if (evalVar && varMapping != null && varMapping.containsKey(node.getNodeName())) {
            node.setNodeType(XPathNode.VAR_REF_NODE);
        }
        return node;
    }

    public static <T> XPathNode<T> extractNode(XPathNode<T> node, String xpath, boolean check, boolean reuse) throws XPathError {
        XPathNode<T> rNode = node;
        if (!reuse) {
            rNode = new XPathNode<>();
        }
        int start = node._endPos;
        String nodeName = null;
        int lastPos = -1;
        int calculatedNodeEnd = getNodeEndPos(xpath, start);
        if (calculatedNodeEnd >= 0 && start < xpath.length()) {
            nodeName = xpath.substring(start, calculatedNodeEnd);
            lastPos = calculatedNodeEnd + 1;
            if (nodeName == null) {
                // node= new XPNode(xpath.substring(start), xpath.length());
                rNode.parse(xpath.substring(start), check);
                rNode.setEndPos(xpath.length());
            } else {
                // node = new XPNode(nodeName, lastPos);
                rNode.parse(nodeName, check);
                rNode.setEndPos(lastPos);
            }
            if (rNode.getEndPos() >= xpath.length()) {
                rNode.setLast(true);
            }
            return rNode;
        }
        return null;
    }
}
