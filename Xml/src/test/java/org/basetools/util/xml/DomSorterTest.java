package org.basetools.util.xml;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.basetools.util.dom.W3CDomUtil;
import org.basetools.util.sort.DomSorter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DomSorterTest {

    public static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
        System.out.println(actualXML);
    }

    public static String readAsStringFromClassloader(Class clazz, String filename, String encoding) throws IOException {
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                return null;
            }
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, encoding);
            return writer.toString();
        }
    }

    @Test
    @Disabled
    public void sortChildNodes() throws Exception {
        File xmlFile = new File(Thread.currentThread().getContextClassLoader().getResource("legodo.xml").getFile());

        Document doc = createDocument(xmlFile);
        //sort all with more 1 or more childs means no leafs!
        Pair<String, Comparator> preSelector = new ImmutablePair<>("//*[count(child::*) > 0]", new DomSorter().new DefaultNodeNameComparator());
        List<Triple<String, Boolean, Comparator>> comps = new ArrayList<>();
        comps.add(new ImmutableTriple<>("./*", Boolean.FALSE, new DomSorter().new DefaultNodeNameComparator()));
        //  comps.add(new ImmutablePair<>("//*", new DomSorter().new XPathComparator("./@name", true)));
        // comps.add(new ImmutablePair<>("/*/complexType//element", new DomSorter().new XPathComparator("./@name", true)));
        DomSorter.sortChildNodes(doc.getFirstChild(), preSelector, comps);
        doc.getDocumentElement().normalize();
        String stringOut = W3CDomUtil.toXML(doc);

        // Display the XML
        assertXMLEquals(readAsStringFromClassloader(getClass(), "legodo.xml", "utf-8"), stringOut.toString());
        System.out.println(stringOut.toString());
    }

    @Test
    @Disabled
    public void sortAFD_VAD00X() throws Exception {
        File xmlFile = new File(Thread.currentThread().getContextClassLoader().getResource("AFD_VAD003.xml").getFile());

        Document doc = createDocument(xmlFile);
        List<Triple<String, Boolean, Comparator>> comps = new ArrayList<>();
        //comps.add(new ImmutablePair<>("./*", new DomSorter().new DefaultNodeNameComparator()));
        comps.add(new ImmutableTriple<>("//*", Boolean.FALSE, new DomSorter().new XPathComparator("./Name", true)));
        // comps.add(new ImmutablePair<>("/*/complexType//element", new DomSorter().new XPathComparator("./@name", true)));
        DomSorter.sortChildNodes(doc.getFirstChild(), comps);
        doc.getDocumentElement().normalize();
        String stringOut = W3CDomUtil.toXML(doc);

        // Display the XML
        assertXMLEquals(readAsStringFromClassloader(getClass(), "AFD_VAD003.xml", "utf-8"), stringOut.toString());
        System.out.println(stringOut.toString());
    }

    @Test
    @Disabled
    public void sortOnlyParentNodes() throws Exception {
        File xmlFile = new File(Thread.currentThread().getContextClassLoader().getResource("AFD_VAD003.xml").getFile());

        Document doc = createDocument(xmlFile);
        //sort all with more 1 or more childs means no leafs!
        Pair<String, Comparator> preSelector = new ImmutablePair<>("//*[count(child::*) > 0]", new DomSorter().new DefaultNodeNameComparator());
        List<Triple<String, Boolean, Comparator>> comps = new ArrayList<>();
        comps.add(new ImmutableTriple<>("./*", Boolean.FALSE, new DomSorter().new DefaultNodeNameComparator()));
        //  comps.add(new ImmutablePair<>("//*", new DomSorter().new XPathComparator("./@name", true)));
        // comps.add(new ImmutablePair<>("/*/complexType//element", new DomSorter().new XPathComparator("./@name", true)));
        DomSorter.sortChildNodes(doc.getFirstChild(), preSelector, comps);
        doc.getDocumentElement().normalize();
        String stringOut = W3CDomUtil.serialize(doc);

        // Display the XML
        assertXMLEquals(readAsStringFromClassloader(getClass(), "AFD_VAD003.xml", "utf-8"), stringOut.toString());
        System.out.println(stringOut.toString());
    }

    public Document createDocument(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        return dBuilder.parse(xmlFile);
    }
}
