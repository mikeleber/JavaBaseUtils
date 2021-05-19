package org.basetools.util.util.sort;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.objects.XObject;
import org.basetools.util.util.dom.W3CDomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DomSorter {
    public static final int INT_EQUAL = 0;
    public static final int INT_LESS_THEN = -1;
    public static final int INT_GREATER_THEN = 1;
    private static final CachedXPathAPI xPathAPI = new CachedXPathAPI();

    public static void sortChildNodes(Node node, Pair<String, Comparator> preSelector,
                                      List<Triple<String, Boolean, Comparator>> stringComparatorPair) throws TransformerException {
        XObject result = xPathAPI.eval(node, preSelector.getKey());
        List<Node> nodes = new ArrayList();
        NodeList childNodeList = result.nodelist();

        for (int i = INT_EQUAL; i < childNodeList.getLength(); i++) {
            nodes.add(childNodeList.item(i));
        }
        Collections.reverse(nodes);
        for (int i = 0; i < nodes.size(); i++) {
            sortChildNodes(nodes.get(i), stringComparatorPair);
        }
    }

    /**
     * Sorts the children of the given node upto the specified depth if
     * available
     *
     * @param node                 -
     *                             node whose children will be sorted
     * @param stringComparatorPair -
     *                             list of pairs of xpaths and comparators used to sort, if null a default NodeName
     */
    public static void sortChildNodes(Node node,
                                      List<Triple<String, Boolean, Comparator>> stringComparatorPair) throws TransformerException {

        for (Triple<String, Boolean, Comparator> aPair : stringComparatorPair) {
            boolean descending = aPair.getMiddle() == null || aPair.getMiddle().booleanValue();
            XObject result = xPathAPI.eval(node, aPair.getLeft());

            List nodes = new ArrayList();
            NodeList childNodeList = result.nodelist();

            for (int i = INT_EQUAL; i < childNodeList.getLength(); i++) {
                nodes.add(childNodeList.item(i));
            }

            if (childNodeList.getLength() > INT_EQUAL) {
                Comparator comp = (aPair.getRight() != null) ? aPair.getRight() : new DomSorter().new DefaultNodeNameComparator();
                if (descending) {
                    //if descending is true, get the reverse ordered comparator
                    Collections.sort(nodes, Collections.reverseOrder(comp));
                } else {
                    Collections.sort(nodes, comp);
                }

                for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                    Node element = (Node) iter.next();
                    Node parentNode = element.getParentNode();
                    parentNode.removeChild(element);
                    parentNode.appendChild(element);
                }
            }
        }
    }

    /**
     * Sorts the children of the given node upto the specified depth if
     * available
     *
     * @param node       -
     *                   node whose children will be sorted
     * @param descending -
     *                   true for sorting in descending order
     * @param depth      -
     *                   depth upto which to sort in DOM
     * @param comparator -
     *                   comparator used to sort, if null a default NodeName
     *                   comparator is used.
     */
    public static void sortChildNodes(Node node, boolean descending,
                                      int depth, Comparator comparator) {

        List nodes = new ArrayList();
        NodeList childNodeList = node.getChildNodes();
        if (depth > INT_EQUAL && childNodeList.getLength() > INT_EQUAL) {
            for (int i = INT_EQUAL; i < childNodeList.getLength(); i++) {
                Node tNode = childNodeList.item(i);
                sortChildNodes(tNode, descending, depth - 1, comparator);
                // Remove empty text nodes
                if ((!(tNode instanceof Text)) || (tNode instanceof Text && tNode.getTextContent().trim().length() > 1)) {
                    nodes.add(tNode);
                }
            }
            Comparator comp = (comparator != null) ? comparator : new DomSorter().new DefaultNodeNameComparator();
            if (descending) {
                //if descending is true, get the reverse ordered comparator
                Collections.sort(nodes, Collections.reverseOrder(comp));
            } else {
                Collections.sort(nodes, comp);
            }

            for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                Node element = (Node) iter.next();
                node.removeChild(element);
                node.appendChild(element);
            }
        }
    }

    public static int naturalCompare(String a, String b, boolean ignoreCase) {
        if (ignoreCase) {
            a = a.toLowerCase();
            b = b.toLowerCase();
        }
        int aLength = a.length();
        int bLength = b.length();
        int minSize = Math.min(aLength, bLength);
        char aChar, bChar;
        boolean aNumber, bNumber;
        boolean asNumeric = false;
        int lastNumericCompare = 0;
        for (int i = 0; i < minSize; i++) {
            aChar = a.charAt(i);
            bChar = b.charAt(i);
            aNumber = aChar >= '0' && aChar <= '9';
            bNumber = bChar >= '0' && bChar <= '9';
            if (asNumeric) {
                if (aNumber && bNumber) {
                    if (lastNumericCompare == 0) {
                        lastNumericCompare = aChar - bChar;
                    }
                } else if (aNumber) {
                    return 1;
                } else if (bNumber) {
                    return -1;
                } else if (lastNumericCompare == 0) {
                    if (aChar != bChar) {
                        return aChar - bChar;
                    }
                    asNumeric = false;
                } else {
                    return lastNumericCompare;
                }
            } else if (aNumber && bNumber) {
                asNumeric = true;
                if (lastNumericCompare == 0) {
                    lastNumericCompare = aChar - bChar;
                }
            } else if (aChar != bChar) {
                return aChar - bChar;
            }
        }
        if (asNumeric) {
            if (aLength > bLength && a.charAt(bLength) >= '0' && a.charAt(bLength) <= '9') // as number
            {
                return 1;  // a has bigger size, thus b is smaller
            } else if (bLength > aLength && b.charAt(aLength) >= '0' && b.charAt(aLength) <= '9') // as number
            {
                return -1;  // b has bigger size, thus a is smaller
            } else if (lastNumericCompare == 0) {
                return aLength - bLength;
            } else {
                return lastNumericCompare;
            }
        } else {
            return aLength - bLength;
        }
    }

    public static void applySort(Node startNode) throws TransformerException {
        Pair<String, Comparator> preSelector = new ImmutablePair<>("//*[count(child::*) > 0]", new DomSorter().new DefaultNodeNameComparator());
        List<Triple<String, Boolean, Comparator>> comps = new ArrayList<>();
        comps.add(new ImmutableTriple<>("./*", Boolean.FALSE, new DomSorter().new DefaultNodeNameComparator()));
        DomSorter.sortChildNodes(startNode, preSelector, comps);
        startNode.normalize();
    }

    public static void applyLeafElementSort(Node startNode, String leafNameElement) throws TransformerException {
        Pair<String, Comparator> preSelector = new ImmutablePair<>("//*[count(child::*) > 0]", new DomSorter().new DefaultNodeNameComparator());
        List<Triple<String, Boolean, Comparator>> comps = new ArrayList<>();
        comps.add(new ImmutableTriple<>("//*", Boolean.FALSE, new DomSorter().new XPathComparator("./" + leafNameElement, true)));

        DomSorter.sortChildNodes(startNode, preSelector, comps);
        startNode.normalize();
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        for (String arg : args) {
            Document dom = W3CDomUtil.createDocument(new File(arg));
            applySort(dom.getDocumentElement());
            System.out.println("\n********* " + arg + " ********");
            W3CDomUtil.cleanEmptyText(dom.getDocumentElement());
            System.out.println(W3CDomUtil.serialize(dom));
            System.out.println("\n");
        }
    }

    private Integer compareSimple(Object node, Object node1) {
        Integer compareResult = null;
        if (Objects.equals(node, node1)) {
            compareResult = INT_EQUAL;
        } else if (node == null) {
            compareResult = INT_LESS_THEN;
        } else if (node1 == null) {
            compareResult = INT_GREATER_THEN;
        }
        return compareResult;
    }

    public class DefaultNodeNameComparator implements Comparator {
        @Override
        public int compare(Object arg0, Object arg1) {
            return ((Node) arg0).getNodeName().compareTo(
                    ((Node) arg1).getNodeName());
        }
    }

    public class XPathComparator implements Comparator {

        private final String xpath;
        private final boolean ignoreCase;

        public XPathComparator(String xpath, boolean ignoreCase) {
            this.xpath = xpath;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public int compare(Object arg0, Object arg1) {
            Integer compareResult = INT_EQUAL;
            try {
                XObject result = xPathAPI.eval((Node) arg0, xpath);
                XObject result1 = xPathAPI.eval((Node) arg1, xpath);
                compareResult = compareSimple(result, result1);
                if (compareResult == null) {
                    if (result.getType() == XObject.CLASS_NODESET && result1.getType() == XObject.CLASS_NODESET) {
                        compareResult = compareNodeSet(result, result1);
                    } else {
                        compareResult = compareXNode(result, result1);
                    }
                }
            } catch (TransformerException te) {
                throw new RuntimeException(te);
            }
            return compareResult;
        }

        private Integer compareXNode(XObject result, XObject result1) throws TransformerException {
            Integer compareResult = INT_EQUAL;
            if (result.getType() == XObject.CLASS_STRING && result1.getType() == XObject.CLASS_STRING) {
                compareResult = naturalCompare(result.castToType(XObject.CLASS_STRING, null).toString(), result1.castToType(XObject.CLASS_STRING, null).toString(), ignoreCase);
            } else if (result.getType() == XObject.CLASS_NUMBER && result1.getType() == XObject.CLASS_NUMBER) {
                compareResult = result.lessThan(result1) ? INT_LESS_THEN : INT_GREATER_THEN;
            }
            return compareResult;
        }

        private Integer compareNodeSet(XObject result, XObject result1) throws TransformerException {
            Node node = result.nodelist().item(0);
            Node node1 = result1.nodelist().item(0);
            Integer compareResult = compareSimple(node, node1);
            if (compareResult == null) {
                if (node.getNodeType() == Node.TEXT_NODE && node1.getNodeType() == Node.TEXT_NODE) {
                    compareResult = naturalCompare(node.getTextContent(), node1.getTextContent(), ignoreCase);
                } else if (node.getNodeType() == Node.ATTRIBUTE_NODE && node1.getNodeType() == Node.ATTRIBUTE_NODE) {
                    compareResult = naturalCompare(node.getTextContent(), node1.getTextContent(), ignoreCase);
                } else if (node.getNodeType() == Node.ELEMENT_NODE && node1.getNodeType() == Node.ELEMENT_NODE) {
                    compareResult = naturalCompare(node.getTextContent(), node1.getTextContent(), ignoreCase);
                } else {
                    compareResult = INT_EQUAL;
                }
            }
            return compareResult;
        }
    }
}
