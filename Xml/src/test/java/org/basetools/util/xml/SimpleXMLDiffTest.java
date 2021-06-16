package org.basetools.util.xml;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.basetools.util.dom.W3CDomUtil;
import org.basetools.util.sort.DomSorter;
import org.basetools.util.xml.diffing.SimpleXMLDiff;
import org.basetools.util.xml.diffing.XMLDifferences;
import org.basetools.util.xml.diffing.XMLNodeDiff;
import org.basetools.util.xml.diffing.XmlDifferException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleXMLDiffTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleXMLDiffTest.class);
    private static final String PATH = "diffing/";

    private static final String EXPECTED = PATH + "expected.xml";
    private static final String NO_DIFF = PATH + "actual_no_diff.xml";
    private static final String VALUE_DIFF = PATH + "actual_value_diff.xml";
    private static final String MISSING_NODE = PATH + "actual_missing_node.xml";
    private static final String ADDITIONAL_NODE = PATH + "actual_additional_node.xml";
    private static final String CHILD_COUNT_DIFF = PATH + "actual_child_count_diff.xml";
    private static final String CHILD_DIFF = PATH + "actual_child_diff.xml";
    private static final String LIST_DIFF_WITHID_EXPECTED = PATH + "actual_childlistwithid_expected.xml";
    private static final String LIST_DIFF_WITHID = PATH + "actual_childlistwithid_diff.xml";
    private static final String CHILD_ATTRIBUTE_DIFF = PATH + "actual_child_attribute_diff.xml";
    private static final String ATTRIBUTE_DIFF = PATH + "actual_attribute_diff.xml";

    private final SimpleXMLDiff xmlDiffer = SimpleXMLDiff.builder().build();

    @Test
    public void givenActualWithDifferentSequence_thenNoDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {

        XMLDifferences diffs = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(NO_DIFF));
        assertFalse(diffs.isDifferent());
    }

    @Test
    public void givenActualWithDifferentValues_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        XMLDifferences diffingResult = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(VALUE_DIFF));
        assertTrue(diffingResult.isDifferent());
    }

    @Test
    public void givenActualWithMissingNode_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        XMLDifferences diffingResult = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(MISSING_NODE));
        assertTrue(diffingResult.isDifferent());
    }

    @Test
    public void givenActualWithAdditionalNode_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        XMLDifferences diffingResult = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(ADDITIONAL_NODE));
        assertTrue(diffingResult.isDifferent());
    }

    @Test
    public void givenActualWithDifferentAttribute_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        XMLDifferences diffingResult = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(ATTRIBUTE_DIFF));
        assertTrue(diffingResult.isDifferent());
        assertEquals(1, diffTypeCount(diffingResult.getDifferences(), XMLNodeDiff.DiffType.VALUE_CHANGED));
    }

    @Test
    public void givenActualWithAdditionalChildNodes_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        XMLDifferences diffingResult = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(CHILD_COUNT_DIFF));
        assertTrue(diffingResult.isDifferent());
        assertEquals(1, diffTypeCount(diffingResult.getDifferences(), XMLNodeDiff.DiffType.CHILD_COUNT));
    }

    @Test
    public void givenActualWithDifferentChildNodes_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        XMLDifferences diffingResult = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(CHILD_DIFF));
        assertTrue(diffingResult.isDifferent());
        assertEquals(2, diffTypeCount(diffingResult.getDifferences(), XMLNodeDiff.DiffType.VALUE_CHANGED));
    }

    @Test
    public void givenActualWithDifferentList_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        Document expected = (Document) loadDocument(LIST_DIFF_WITHID_EXPECTED);
        Document current = (Document) loadDocument(LIST_DIFF_WITHID);

        Pair<String, Comparator> preSelector = new ImmutablePair<>("//*[count(child::*) > 0]", new DomSorter().new DefaultNodeNameComparator());
        List<Triple<String, Boolean, Comparator>> comps = new ArrayList<>();
        comps.add(new ImmutableTriple("/Partner/ListNode", Boolean.FALSE, new DomSorter().new XPathComparator("./@id", true)));
        comps.add(new ImmutableTriple("/Partner/ListNode2", Boolean.FALSE, new DomSorter().new XPathComparator("./@id", true)));
        comps.add(new ImmutableTriple("/Partner/ListHolder/ListNode2", Boolean.FALSE, new DomSorter().new XPathComparator("./@id", true)));
        DomSorter.sortChildNodes(current, preSelector, comps);
        W3CDomUtil.cleanEmptyText(current);
        System.out.println(W3CDomUtil.serialize(current, true));
        System.out.println(W3CDomUtil.serialize(expected, true));
        current.normalize();

        DomSorter.sortChildNodes(expected, preSelector, comps);
        expected.normalize();

        XMLDifferences diffingResult = xmlDiffer.difference(expected, current);
        assertTrue(diffingResult.isDifferent());
        System.out.println(diffingResult.toString());
        assertEquals(6, diffTypeCount(diffingResult.getDifferences(), XMLNodeDiff.DiffType.VALUE_CHANGED));
    }

    @Test
    public void givenActualWithDifferentChildNodeAttributes_thenDiffIsRecognized() throws IOException, XmlDifferException, TransformerException, SAXException, ParserConfigurationException {
        XMLDifferences diffingResult = xmlDiffer.difference(loadDocument(EXPECTED), loadDocument(CHILD_ATTRIBUTE_DIFF));
        assertTrue(diffingResult.isDifferent());
        assertEquals(1, diffTypeCount(diffingResult.getDifferences(), XMLNodeDiff.DiffType.VALUE_CHANGED));
    }

    private long diffTypeCount(Collection<XMLNodeDiff> differences, XMLNodeDiff.DiffType diffType) {
        return differences.stream().filter(d -> diffType.equals(d.getDiffType())).count();
    }

    protected Node loadDocument(String fileName) throws IOException, SAXException, ParserConfigurationException {
        return W3CDomUtil.createDocument(new File(this.getClass().getClassLoader().getResource(fileName).getFile()));
    }
}