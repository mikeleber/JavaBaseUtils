package org.basetools.util.xml.diffing;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.basetools.util.xml.xpath.w3c.W3CXPathExecuterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleXMLDiff extends XmlDiffer {
    private static Logger LOGGER = LoggerFactory.getLogger(SimpleXMLDiff.class.getName());
    private boolean nodeTypeDiff = true;
    private boolean nodeValueDiff = true;
    private boolean normalizeCharSpacing = true;
    private boolean ignoreNS = true;
    private Node actualRootNode, expectedRootNode;
    private Set<Node> blacklist;
    private Set<String> blacklistXPaths;

    public static SimpleXmlDiffBuilder builder() {
        return new SimpleXmlDiffBuilder();
    }

    public void addToBlackList(String xpath) throws TransformerException {
        addToBlackList(actualRootNode, xpath);
        addToBlackList(expectedRootNode, xpath);
    }

    public void addToBlackList(Node baseNode, String xpath) throws TransformerException {
        if (baseNode == null) {
            LOGGER.error("no baseNode defined for xpath{}", xpath);
            return;
        }

        List<Node> childNodeList = W3CXPathExecuterImpl.getInstance().processXPath(baseNode, xpath, ignoreNS);

        for (Node child : childNodeList) {
            blacklist.add(child);
        }
    }

    public XMLDifferences difference(Node actualNode, Node expectedNode) {

        if (actualRootNode == null) {
            actualRootNode = actualNode;
        }
        if (expectedRootNode == null) {
            expectedRootNode = expectedNode;
        }
        XMLDifferences differences = new XMLDifferences(null, null);
        difference(actualNode, expectedNode, differences);
        return differences;
    }

    public Set<Node> getBlacklistedNodes() {
        if (blacklist == null) {
            blacklist = new LinkedHashSet<>();
            initializeBlacklistedNodes();
        }
        return blacklist;
    }

    private void initializeBlacklistedNodes() {
        blacklist.clear();
        if (blacklistXPaths != null) {

            blacklistXPaths.stream().forEach((xpath) -> {
                try {
                    addToBlackList(xpath);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * XMLNodeDiff 2 nodes and put the differences in the list
     */
    public boolean difference(Node actualNode, Node expectedNode, XMLDifferences differences) {
        if (getBlacklistedNodes().contains(actualNode)) {
            return false;
        }
        if (getBlacklistedNodes().contains(expectedNode)) {
            return false;
        }

        if (diffNodeExists(actualNode, expectedNode, differences)) {
            return true;
        }

        if (nodeTypeDiff) {
            diffNodeType(actualNode, expectedNode, differences);
        }

        if (nodeValueDiff) {
            diffNodeValue(actualNode, expectedNode, differences);
        }

        if (actualNode.getNodeType() != Node.ATTRIBUTE_NODE) {
            diffAttributes(actualNode, expectedNode, differences);
            diffNodes(actualNode, expectedNode, differences);
        }
        return differences.countErrors() > 0;
    }

    /**
     * XMLNodeDiff the nodes
     */
    public boolean diffNodes(Node actualNode, Node expectedNode, XMLDifferences differences) {
        //Sort by Name
        Map<String, Pair<Integer, Node>> actualChildren = new LinkedHashMap<>();
        int pos = 0;
        for (Node child1 = actualNode.getFirstChild(); child1 != null; child1 = child1.getNextSibling()) {
            if (!isTextNode(child1)) pos++;
            actualChildren.put(getNodeIdentification(child1), new ImmutablePair<>(Integer.valueOf(pos), child1));
        }

        //Sort by Name
        pos = 0;
        Map<String, Pair<Integer, Node>> expectedChildren = new LinkedHashMap<>();
        for (Node child2 = expectedNode.getFirstChild(); child2 != null; child2 = child2.getNextSibling()) {
            if (!isTextNode(child2)) pos++;
            expectedChildren.put(getNodeIdentification(child2), new ImmutablePair<>(Integer.valueOf(pos), child2));
        }

        Set<Node> mixedInBlackList = new HashSet<>(getMixinBlacklist(actualNode));
        mixedInBlackList.addAll(getMixinBlacklist(expectedNode));
        if (mixedInBlackList.size() > 0) {
            handleMixinListChilds(actualNode, expectedNode, differences);
        }

        //XMLNodeDiff all the children1
        for (Pair<Integer, Node> actualChildPair : actualChildren.values()) {
            if (!mixedInBlackList.contains(actualChildPair)) {
                Pair<Integer, Node> expectedChildPair = expectedChildren.remove(getNodeIdentification(actualChildPair.getValue()));
                Node actualChild = actualChildPair != null ? actualChildPair.getValue() : null;
                Node expectedChild = expectedChildPair != null ? expectedChildPair.getValue() : null;
                difference(actualChild, expectedChild, differences);
                checkPosition(differences, expectedChildPair, actualChildPair);
            }
        }

        //XMLNodeDiff all the children2 left over
        for (Pair<Integer, Node> expectedChildPair : expectedChildren.values()) {
            if (!mixedInBlackList.contains(expectedChildPair)) {
                Pair<Integer, Node> actualChildPair = actualChildren.get(getNodeIdentification(expectedChildPair.getValue()));
                Node actualChild = actualChildPair != null ? actualChildPair.getValue() : null;
                Node expectedChild = expectedChildPair != null ? expectedChildPair.getValue() : null;
                difference(actualChild, expectedChild, differences);
                checkPosition(differences, expectedChildPair, actualChildPair);
            }
        }

        return differences.countErrors() > 0;
    }

    private void checkPosition(XMLDifferences differences, Pair<Integer, Node> expectedChildPair, Pair<Integer, Node> actualChildPair) {
        if (actualChildPair != null && expectedChildPair != null && !isTextNode(actualChildPair.getValue()) && !isTextNode(expectedChildPair.getValue()) && actualChildPair.getKey() != expectedChildPair.getKey()) {
            XMLNodeDiff.Builder diffBuilder = XMLNodeDiff.builder()
                    .withXpath(getPath(actualChildPair.getValue()))
                    .withActualNode(actualChildPair.getValue())
                    .withActualValue(actualChildPair.getKey())
                    .withExpectedNode(expectedChildPair.getValue())
                    .withExpectedValue(expectedChildPair.getKey())
                    .withSeverity(XMLNodeDiff.Servity.WARNING)
                    .withDiffType(XMLNodeDiff.DiffType.POSITION);
            differences.add(diffBuilder.build());
        }
    }

    private Set<Node> getMixinBlacklist(Node node) {
        Set<Node> nodeBlackList = new LinkedHashSet<>();
        Map<String, Long> grouping = getChildGrouping(getElementsFrom(node.getChildNodes()));
        Set<Map.Entry<String, Long>> entries = grouping.entrySet();
        entries.stream().filter(entry -> entry.getValue().longValue() > 1)
                .forEach(entry -> nodeBlackList.addAll(getElementsByName(node.getChildNodes(), entry.getKey())));
        return nodeBlackList;
    }

    private String getNodeIdentification(Node child1) {
        String nodeName = child1.getNodeName();
        if (nodeName.indexOf(":") >= 0) {
            nodeName = StringUtils.substringAfter(child1.getNodeName(), ":");
        }
        if (!ignoreNS) {
            nodeName = child1.getNamespaceURI() + ":" + nodeName;
        }
        return nodeName;
    }

    private Map<String, Long> getChildGrouping(Set<Node> childs) {
        return childs.stream().collect(Collectors.groupingBy(e -> e.getNodeName(), Collectors.counting()));
    }

    private boolean handleMixinListChilds(Node actualNode, Node expectedNode, XMLDifferences differences) {
        Map<String, Long> groupingActualNodes = getChildGrouping(getElementsFrom(actualNode.getChildNodes()));
        Map<String, Long> groupingExpectedNodes = getChildGrouping(getElementsFrom(expectedNode.getChildNodes()));
        Set<Map.Entry<String, Long>> actualNodeEntries = groupingActualNodes.entrySet();

        actualNodeEntries.stream().filter(entry -> entry.getValue().longValue() > 1).forEach((entry) ->
                {
                    if (entry.getValue() != groupingExpectedNodes.get(entry.getKey())) {
                        addToDiff(differences, XMLNodeDiff.builder()
                                .withXpath(getPath(actualNode) + "/" + entry.getKey())
                                .withDiffType(XMLNodeDiff.DiffType.CHILD_COUNT)
                                .withActualNode(actualNode)
                                .withExpectedValue(expectedNode)
                                .withActualValue(groupingActualNodes.get(entry.getKey()))
                                .withExpectedValue(groupingExpectedNodes.get(entry.getKey()))
                                .build());
                    } else {
                        Set<Node> actualNodeListEntries = getElementsByName(actualNode.getChildNodes(), entry.getKey());

                        Node[] testNodeListEntries = getElementsByName(expectedNode.getChildNodes(), entry.getKey()).toArray(new Node[]{});
                        int pos = 0;
                        for (Node actualNodeListEntry : actualNodeListEntries) {
                            Node expectedNodeListEntry = testNodeListEntries[pos++];
                            difference(actualNodeListEntry, expectedNodeListEntry, differences);
                            //process ListDiff for same size!
                        }
                    }
                }
        );
        return false;
    }

    private Set<Node> getElementsFrom(NodeList childs) {
        Set<Node> result = new LinkedHashSet<>(childs.getLength());
        for (int i = 0; i < childs.getLength(); i++) {
            Node child2 = childs.item(i);
            if (child2.getNodeType() == Node.ELEMENT_NODE) {
                result.add(child2);
            }
        }
        return result;
    }

    private Set<Node> getElementsByName(NodeList childs, String nodeName) {
        Set<Node> result = new LinkedHashSet<>(childs.getLength());
        for (int i = 0; i < childs.getLength(); i++) {
            Node child2 = childs.item(i);
            String currentName = getNodeIdentification(child2);
            if (child2.getNodeType() == Node.ELEMENT_NODE) {
                if (StringUtils.equals(nodeName, currentName)) {
                    result.add(child2);
                }
            }
        }
        return result;
    }

    /**
     * XMLNodeDiff the nodes
     */
    public boolean diffAttributes(Node actualNode, Node expectedNode, XMLDifferences diffs) {
        //Sort by Name
        NamedNodeMap currentMap = actualNode.getAttributes();
        Map<String, Node> currentAttributes = new LinkedHashMap<>();
        for (int index = 0; currentMap != null && index < currentMap.getLength(); index++) {
            currentAttributes.put(getNodeIdentification(currentMap.item(index)), currentMap.item(index));
        }

        //Sort by Name
        NamedNodeMap testMap = expectedNode.getAttributes();
        Map<String, Node> testAttributes = new LinkedHashMap<>();
        for (int index = 0; testMap != null && index < testMap.getLength(); index++) {
            testAttributes.put(getNodeIdentification(testMap.item(index)), testMap.item(index));
        }

        for (Node currentAttribute : currentAttributes.values()) {
            Node testAttribute = testAttributes.remove(getNodeIdentification(currentAttribute));
            difference(currentAttribute, testAttribute, diffs);
        }

        for (Node testAttribute : testAttributes.values()) {
            Node currentAttribute = currentAttributes.get(getNodeIdentification(testAttribute));
            difference(currentAttribute, testAttribute, diffs);
        }

        return diffs.countErrors() > 0;
    }

    /**
     * Check that the nodes exist
     */
    public boolean diffNodeExists(Node actualNode, Node expectedNode, XMLDifferences differences) {

        if (handleNodeAsNull(actualNode)) {
            actualNode = null;
        }
        if (handleNodeAsNull(expectedNode)) {
            expectedNode = null;
        }
        if (actualNode == null && expectedNode == null) {
            return true;
        }

        if (actualNode == null && expectedNode != null) {
            addToDiff(differences, XMLNodeDiff.builder()
                    .withXpath(getPath(expectedNode))
                    .withDiffType(XMLNodeDiff.DiffType.NODE_REMOVED)
                    .withActualNode(actualNode)
                    .withExpectedNode(expectedNode)
                    .build());
            return true;
        }

        if (actualNode != null && expectedNode == null) {
            addToDiff(differences, XMLNodeDiff.builder()
                    .withXpath(getPath(actualNode))
                    .withDiffType(XMLNodeDiff.DiffType.NODE_ADDED)
                    .withActualNode(actualNode)
                    .withExpectedNode(expectedNode)
                    .build());
            return true;
        }

        return false;
    }

    private boolean handleNodeAsNull(Node nodeA) {
        return nodeA == null
                || (isTextNode(nodeA)
                && StringUtils.isBlank(nodeA.getTextContent()));
    }

    private boolean isTextNode(Node nodeA) {
        return nodeA.getNodeType() == Node.TEXT_NODE;
    }

    private void addToDiff(XMLDifferences differences, XMLNodeDiff XMLNodeDiff) {
        differences.add(XMLNodeDiff);
    }

    /**
     * XMLNodeDiff the Node Type
     */
    public boolean diffNodeType(Node actualNode, Node expectedNode, XMLDifferences diffs) {
        if (actualNode.getNodeType() != expectedNode.getNodeType()) {
            addToDiff(diffs, XMLNodeDiff.builder()
                    .withXpath(getPath(actualNode))
                    .withActualNode(actualNode)
                    .withExpectedNode(expectedNode)
                    .withDiffType(XMLNodeDiff.DiffType.NODE_TYPE)
                    .withActualValue(actualNode.getNodeType())
                    .withExpectedValue(expectedNode.getNodeType())
                    .build());

            return true;
        }

        return false;
    }

    /**
     * Get the node path
     */
    public String getPath(Node node) {

        List<String> pathStack = new ArrayList<>();
        do {
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                pathStack.add("/@" + getNodeIdentification(node));
                node = ((Attr) node).getOwnerElement();
            } else {
                pathStack.add("/" + getNodeIdentification(node));
            }
        }
        while ((node = node.getParentNode()) != null);

        StringBuilder resultXPath = new StringBuilder();
        for (int s = pathStack.size() - 1; s >= 0; s--) {
            resultXPath.append(pathStack.get(s));
        }
        return resultXPath.toString();
    }

    /**
     * XMLNodeDiff the Node Value
     */
    public boolean diffNodeValue(Node actualNode, Node expectedNode, XMLDifferences diffs) {
        String currentNodeValue = getNodeValue(actualNode);
        String testNodeValue = getNodeValue(expectedNode);

        if (StringUtils.isBlank(currentNodeValue) && StringUtils.isBlank(testNodeValue)) {
            return false;
        }
        XMLNodeDiff.Builder diffBuilder = XMLNodeDiff.builder()
                .withXpath(getPath(actualNode))
                .withActualNode(actualNode)
                .withExpectedNode(expectedNode)
                .withDiffType(XMLNodeDiff.DiffType.VALUE_CHANGED);
        if (currentNodeValue == null && testNodeValue != null) {

            addToDiff(diffs, diffBuilder.withActualValue(actualNode).withExpectedValue(getNodeValue(expectedNode)).build());
            return true;
        }

        if (currentNodeValue != null && testNodeValue == null) {
            addToDiff(diffs, diffBuilder.withActualValue(currentNodeValue).withExpectedValue(expectedNode).build());
            return true;
        }

        if (!currentNodeValue.equals(testNodeValue)) {
            addToDiff(diffs, diffBuilder.withActualValue(currentNodeValue).withExpectedValue(getNodeValue(expectedNode)).build());
            return true;
        }

        return false;
    }

    private String getNodeValue(Node currentNode) {
        if (currentNode == null) {
            return null;
        } else {
            String stringValue = currentNode.getNodeValue();
            if (normalizeCharSpacing) {
                stringValue = StringUtils.normalizeSpace(stringValue);
            }
            return stringValue;
        }
    }

    @Override
    public XMLDifferences diff(String expected, String actual) throws XmlDifferException {
        String preProcessedExpected = preProcess(expected);
        String preProcessedActual = preProcess(actual);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(!ignoreNS);
            dbf.setCoalescing(true);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setIgnoringComments(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document currentDoc = db.parse(new ByteArrayInputStream(preProcessedActual.getBytes()));
            actualRootNode = currentDoc.getDocumentElement();
            Document testDoc = db.parse(new ByteArrayInputStream(preProcessedExpected.getBytes()));
            expectedRootNode = testDoc.getDocumentElement();
            currentDoc.normalizeDocument();
            testDoc.normalizeDocument();

            XMLDifferences diffs = new XMLDifferences(expected, actual);
            difference(currentDoc, testDoc, diffs);
            return diffs;
        } catch (Exception e) {
            throw new XmlDifferException(e);
        }
    }

    public static final class SimpleXmlDiffBuilder {
        private boolean nodeTypeDiff = true;
        private boolean nodeValueDiff = true;
        private boolean normalizeCharSpacing = true;
        private boolean ignoreNS = true;
        private Node actualNode;
        private Node expectedNode;
        private String[] blacklistXpaths;

        private SimpleXmlDiffBuilder() {
        }

        public SimpleXmlDiffBuilder withNodeTypeDiff(boolean nodeTypeDiff) {
            this.nodeTypeDiff = nodeTypeDiff;
            return this;
        }

        public SimpleXmlDiffBuilder withNodeValueDiff(boolean nodeValueDiff) {
            this.nodeValueDiff = nodeValueDiff;
            return this;
        }

        public SimpleXmlDiffBuilder withNormalizeCharSpacing(boolean normalizeCharSpacing) {
            this.normalizeCharSpacing = normalizeCharSpacing;
            return this;
        }

        public SimpleXmlDiffBuilder withBlacklistXPaths(String... xpaths) {
            this.blacklistXpaths = xpaths;
            return this;
        }

        public SimpleXmlDiffBuilder withNodes(Node actualNode, Node expectedNode) {
            Objects.requireNonNull(actualNode);
            Objects.requireNonNull(expectedNode);
            this.actualNode = actualNode;
            this.expectedNode = expectedNode;
            return this;
        }

        public SimpleXMLDiff build() {
            SimpleXMLDiff simpleXmlDiff = new SimpleXMLDiff();
            simpleXmlDiff.nodeValueDiff = this.nodeValueDiff;
            simpleXmlDiff.normalizeCharSpacing = this.normalizeCharSpacing;
            simpleXmlDiff.nodeTypeDiff = this.nodeTypeDiff;
            simpleXmlDiff.ignoreNS = this.ignoreNS;
            if (this.blacklistXpaths != null) {
                simpleXmlDiff.blacklistXPaths = Arrays.stream(this.blacklistXpaths).collect(Collectors.toSet());
            }
            return simpleXmlDiff;
        }
    }
}




