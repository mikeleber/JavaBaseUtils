package org.basetools.util.dom;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xs.XSAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class W3CDomUtil {
    public static final String XMLNS = "xmlns".intern();
    public final static int NODE_TYPE = 1;
    public final static int NODE_ELEMENT = 3;
    public final static String ANNOTATION_READONLY = "readOnly";
    public final static String APPINFO = "appinfo";
    public final static String APPINFO_DOCUMENTATION = "documentation";
    public final static String ANNOTATION_INHERIT = "inherit";
    public final static String ANNOTATION_NLS = "NLS_";
    public final static String ANNOTATION_NLS_DEFAULT = ANNOTATION_NLS + "DEFAULT";
    private static final String EQUALS = "=";
    private static final String XSD = "xsd".intern();
    private static final String HTTP_WWW_W3_ORG_2001_XMLSCHEMA = "http://www.w3.org/2001/XMLSchema".intern();
    private static final String SCHEMA_LOCATION = "schemaLocation".intern();
    private static final DOMParser parser = new DOMParser();
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    private static final EntityResolver NULL_RESOLVER = new EntityResolver() {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException {
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }
    };
    private static final String TARGET_PI = "modelui";
    private static final String FORMAT_PRETTY_PRINT = "format-pretty-print";
    private static Logger LOGGER = LoggerFactory.getLogger(W3CDomUtil.class.getName());

    private W3CDomUtil() {
    }

    /**
     * Creates a W3C DOM out of an URL.
     *
     * @param url to create W3C DOM from
     */
    public static Document createDocument(URL url) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        InputStream is = resolveURL(url);
        dom = createDocument(is, getFileEncoding());
        if (dom != null) {
            dom.appendChild(dom.createProcessingInstruction(TARGET_PI, url.getPath()));
        }
        return dom;
    }

    private static String getFileEncoding() {
        return "UTF-8";
    }

    private static InputStream resolveURL(URL url) throws IOException {
        return url.openStream();
    }

    public static Document createDocument(File file) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        InputStream is = resolveURL(file.toURL());
        return createDocument(is);
    }

    /**
     * Creates a W3C DOM out of an InputStream.
     *
     * @param is InputStream to create W3C DOM from
     */
    public static Document createDocument(InputStream is) throws XMLParseException, SAXException, IOException {
        return createDocument(readAsString(is));
    }

    public static String readAsString(InputStream in) throws IOException {
        return readAsString(in, null);
    }

    public static String readAsString(InputStream in, String encoding) throws IOException {
        if (encoding != null) {
            return IOUtils.toString(in, Charset.forName(encoding));
        }
        return IOUtils.toString(in, Charset.defaultCharset());
    }

    /**
     * Creates a W3C DOM out of an InputStream.
     *
     * @param is InputStream to create W3C DOM from
     */
    public static Document createDocument(InputStream is, String enc) throws XMLParseException, SAXException, IOException {
        return createDocument(readAsString(is, enc));
    }

    public static Document createDocument(URL url, String enc) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        InputStream is = resolveURL(url);
        if (enc != null) {
            dom = createDocument(is, enc);
        } else {
            dom = createDocument(is);
        }
        if (dom != null) {
            dom.appendChild(dom.createProcessingInstruction("modelui", url.getPath()));
        }
        return dom;
    }

    public static Document createDocument(LSInput xml) throws XMLParseException, SAXException, IOException {

        Reader aReader = xml.getCharacterStream();
        if (xml.getStringData() != null) {
            aReader = new StringReader(xml.getStringData());
        }
        // }
        if (aReader == null) {
            throw new IOException(xml.getSystemId() + " not found.");
        }
        return createDocument(new InputSource(aReader));
    }

    public static Document createDocument(InputSource is) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        // Get an instance of the parser
        DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/validation", false);
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        parser.setEntityResolver(NULL_RESOLVER);
        // warnings shown, error stream set to stderr.
        // Parse the document.
        parser.parse(is);
        // Obtain the document.
        dom = parser.getDocument();
        return dom;
    }

    private static String makeRelative(String loadedFrom) {
        String schemaName = loadedFrom;
        if (isUrl(loadedFrom)) {
            try {
                schemaName = extractFileName(new URL(loadedFrom).getFile());
            } catch (Exception e) {
            }
        } else {
            schemaName = extractFileName(loadedFrom);
        }
        return schemaName;
    }

    private static String extractFileName(String file) {
        return FilenameUtils.getName(file);
    }

    private static boolean isUrl(String loadedFrom) {
        try {
            new URL(loadedFrom);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Document createEmptyDocument() throws XMLParseException, SAXException, IOException {
        Document doc = createEmptyDocument("dummy");
        return doc;
    }

    public static Document createEmptyDocument(String rootTag) throws XMLParseException, SAXException, IOException {
        Document doc = createDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + rootTag + "/>");
        return doc;
    }

    public static Document createDocument(String content) throws XMLParseException, SAXException, IOException {
        return createDocument(content, false);
    }

    public static Document createDocument(String content, boolean ignorePrefix) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        // Get an instance of the parser
        DOMParser parser = new DOMParser();
        parser.setErrorHandler(null);
        parser.setEntityResolver(NULL_RESOLVER);
        if (ignorePrefix) {
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
        }
        // warnings shown, error stream set to stderr.
        // Parse the document.
        InputSource is = new InputSource(new StringReader(content));
        parser.parse(is);
        // Obtain the document.
        dom = parser.getDocument();
        return dom;
    }

    public static Document createDocument(InputStream input, boolean ignorePrefix, String encoding) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        // Get an instance of the parser
        DOMParser parser = new DOMParser();
        parser.setErrorHandler(null);
        parser.setEntityResolver(NULL_RESOLVER);
        if (ignorePrefix) {
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
        }
        // warnings shown, error stream set to stderr.
        // Parse the document.
        InputSource is = new InputSource(input);
        if (encoding != null) {
            is.setEncoding(encoding);
        }
        parser.parse(is);
        // Obtain the document.
        dom = parser.getDocument();
        return dom;
    }

    /**
     * Creates a W3C DOM out of a simple String.
     *
     * @param content String content
     */
    public synchronized static Document createDocument(String content, ErrorHandler handler) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        parser.setErrorHandler(handler);
        // warnings shown, error stream set to stderr.
        // Parse the document.
        InputSource is = new InputSource(new StringReader(content));
        parser.parse(is);
        parser.setEntityResolver(NULL_RESOLVER);
        // Obtain the document.
        dom = parser.getDocument();
        return dom;
    }

    public synchronized static Document createDocument(String content, ErrorHandler handler, EntityResolver resolver) throws XMLParseException, SAXException, IOException {
        Document dom = null;
        parser.setErrorHandler(handler);
        // warnings shown, error stream set to stderr.
        // Parse the document.
        InputSource is = new InputSource(new StringReader(content));
        parser.parse(is);
        parser.setEntityResolver(resolver);
        // Obtain the document.
        dom = parser.getDocument();
        return dom;
    }

    /**
     * Creates a W3C DOM out of a simple String.
     *
     * @param content String content
     */
    public static Document createDocument(StringBuffer content) throws XMLParseException, SAXException, IOException {
        return createDocument(content.toString());
    }

    public static Element getChild(Node aNode, String... name) {
        return getChild(aNode, name, -1);
    }

    public static Element getChild(Node aNode, String namespace, String defaultNS, String name, int index, boolean ignoreNS) {
        if (index != -1) {
            int locIndex = 0;
            NodeList nl = aNode.getChildNodes();
            int nlSize = nl.getLength();
            for (int ne = 0; ne < nlSize; ne++) {
                Node nextElement = nl.item(ne);
                if (nextElement.getNodeType() == Node.ELEMENT_NODE) {
                    if (locIndex == index) {
                        return (Element) nextElement;
                    } else {
                        locIndex++;
                    }
                }
            }
            return null;
        }
        if (namespace == null) {
            namespace = defaultNS;
        }
        NodeList nl = aNode.getChildNodes();
        int nlSize = nl.getLength();
        Node found = null;
        for (int ne = 0; found == null && ne < nlSize; ne++) {
            Node nextElement = nl.item(ne);
            if (equalsTypeElement(defaultNS, nextElement, name, namespace, ignoreNS)) {
                found = nextElement;
                break;
            }
        }
        return (Element) found;
    }

    public static boolean equalsTypeElement(String defaultNS, Node instanceElement, String typeName, String typeNameSpace, boolean ignoreNS) {
        boolean isEqual = false;
        if (instanceElement.getNodeType() == Node.ELEMENT_NODE) {
            String elementName = instanceElement.getLocalName();
            if (elementName == null) {
                elementName = instanceElement.getNodeName();
            }
            if (elementName != null) {
                if (ignoreNS) {
                    if (elementName.equals(typeName)) {
                        isEqual = true;
                    }
                } else if (elementName.equals(typeName)) {
                    // elementNS is null means no targetNS was defined for this
                    // element! maybe we have to
                    // compare against defaultNS
                    String elementNS = evalNamespaceURI(instanceElement);
                    if (elementNS == null) {
                        elementNS = defaultNS;
                    }
                    if (elementNS == typeNameSpace || StringUtils.equals(elementNS, typeNameSpace)) {
                        isEqual = true;
                    } else {

                        if (elementNS == typeNameSpace || StringUtils.equals(elementNS, typeNameSpace)) {
                            isEqual = true;
                        } else {

                            LOGGER.warn("ns doesnt match:XML NS" + elementNS + " typeNS:" + typeNameSpace);
                        }
                    }
                }
            }
        }
        return isEqual;
    }

    public static Element getChild(Node aNode, String[] name, int index) {
        return getChild(aNode, name, index, ignoreNamespace());
    }

    public static Element getChild(Node aNode, String[] name, int index, boolean ignoreNS) {
        if (index != -1) {
            int locIndex = 0;
            NodeList nl = aNode.getChildNodes();
            int nlSize = nl.getLength();
            for (int ne = 0; ne < nlSize; ne++) {
                Node nextElement = nl.item(ne);
                if (nextElement.getNodeType() == Node.ELEMENT_NODE) {
                    if (locIndex == index) {
                        return (Element) nextElement;
                    } else {
                        locIndex++;
                    }
                }
            }
            return null;
        }
        NodeList nl = aNode.getChildNodes();
        int nlSize = nl.getLength();
        Node found = null;
        for (int ne = 0; found == null && ne < nlSize; ne++) {
            Node nextElement = nl.item(ne);
            if (nextElement != null && nextElement.getNodeType() == Node.ELEMENT_NODE) {
                String elementName;
                // elementName = nextElement.getNodeName();
                elementName = nextElement.getLocalName();
                if (elementName == null) {
                    elementName = nextElement.getNodeName();
                }
                // int elIndexOfDP= elementName.indexOf(":");
                int nls = name.length;
                for (int i = 0; i < nls; i++) {
                    if (ignoreNS) {
                        if (elementName.equals(name[i])) {
                            found = nextElement;
                        }
                    } else if (elementName.equals(name[i])) {
                        found = nextElement;
                    }
                }
            }
        }
        return (Element) found;
    }

    public static Element getChild(Node aNode, String name, int index, boolean ignoreNS) {
        if (aNode == null) {
            return null;
        }
        int indexOfDP = name.indexOf(":");
        if (index != -1) {
            int locIndex = 0;
            NodeList nl = aNode.getChildNodes();
            int nlSize = nl.getLength();
            for (int ne = 0; ne < nlSize; ne++) {
                Node nextElement = nl.item(ne);
                if (nextElement.getNodeType() == Node.ELEMENT_NODE) {
                    if (locIndex == index) {
                        return (Element) nextElement;
                    } else {
                        locIndex++;
                    }
                }
            }
            return null;
        }
        NodeList nl = aNode.getChildNodes();
        int nlSize = nl.getLength();
        Node found = null;
        for (int ne = 0; found == null && ne < nlSize; ne++) {
            Node nextElement = nl.item(ne);
            if (nextElement != null && nextElement.getNodeType() == Node.ELEMENT_NODE) {
                String elementName;
                elementName = nextElement.getNodeName();

                if (ignoreNS) {
                    if (nextElement.getLocalName().equals(name)) {
                        found = nextElement;
                    }
                } else if (elementName.equals(name)) {
                    found = nextElement;
                }
            }
        }
        return (Element) found;
    }

    public static Element getLastChild(Node aNode, String[] name, int index) {
        if (index != -1) {
            int locIndex = 0;
            NodeList nl = aNode.getChildNodes();
            int nlSize = nl.getLength();
            for (int ne = nlSize - 1; ne >= 0; ne--) {
                Node nextElement = nl.item(ne);
                if (nextElement.getNodeType() == Node.ELEMENT_NODE) {
                    if (locIndex == index) {
                        return (Element) nextElement;
                    } else {
                        locIndex++;
                    }
                }
            }
            return null;
        }
        NodeList nl = aNode.getChildNodes();
        int nlSize = nl.getLength();
        Node found = null;
        for (int ne = nlSize - 1; found == null && ne >= 0; ne--) {
            Node nextElement = nl.item(ne);
            if (nextElement != null && nextElement.getNodeType() == Node.ELEMENT_NODE) {
                String elementName;
                elementName = nextElement.getLocalName();
                if (elementName == null) {
                    elementName = nextElement.getNodeName();
                }
                for (int i = 0; i < name.length; i++) {
                    if (ignoreNamespace()) {
                        if (elementName.equals(name[i])) {
                            found = nextElement;
                        }
                    } else if (elementName.equals(name[i])) {
                        found = nextElement;
                    }
                }
            }
        }
        return (Element) found;
    }
//
//    public static String getQualifiedElementName(Node aNode) {
//        String elementName = aNode.getNodeName();
//        String ns = aNode.getNamespaceURI();
//        if (ns != null && ns.length() > 0) {
//            String prefix = schema.getNamespacePrefix(ns);
//            if (prefix != null && prefix.length() > 0) {
//                elementName = prefix + ":" + aNode.getNodeName();
//            }
//        }
//        return elementName;
//    }

    private static boolean ignoreNamespace() {
        return true;
    }

    public static List<Element> getChildren(Node aNode, String namespace, String defaultNS, String name) {
        return getChildren(aNode, namespace, defaultNS, name, ignoreNamespace());
    }

    public static List<Element> getChildren(Node aNode, String namespace, String defaultNS, String name, boolean ignoreNS) {
        NodeList nl = aNode.getChildNodes();
        if (namespace == null) {
            namespace = defaultNS;
        }
        int nlSize = nl.getLength();
        List<Element> list = new ArrayList<>(nlSize);
        String elementName = null;
        for (int i = 0; i < nlSize; i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elementName = node.getLocalName();
                if (elementName == null) {
                    elementName = node.getNodeName();
                }
                if (ignoreNS) {
                    if (node.getLocalName().equals(name)) {
                        list.add((Element) node);
                    }
                } else if (elementName.equals(name)) {
                    String nxtNS = evalNamespaceURI(aNode);
                    if (StringUtils.equals(nxtNS, namespace)) {
                        list.add((Element) node);
                    } else {

                        LOGGER.error("ns doesnt match:XML NS" + node.getNamespaceURI() + " typeNS:" + namespace);
                    }
                }
            }
        }
        return list;
    }

    private static String evalNamespaceURI(Node aNode) {
        String ns = aNode.getNamespaceURI();
        if (ns != null) {
            return ns;
        } else if (aNode.getParentNode() != null) {
            return evalNamespaceURI(aNode.getParentNode());
        } else {
            return null;
        }
    }

    public static List<Element> getChildren(Node aNode, String name) {
        boolean ignoreNS = ignoreNamespace();
        return getChildren(aNode, name, ignoreNS);
    }

    public static List<Element> getChildren(Node aNode, String name, boolean ignoreNS) {
        NodeList nl = aNode.getChildNodes();
        int indexOfDP = name.indexOf(":");
        int nlSize = nl.getLength();
        List list = new ArrayList(nlSize);
        String elementName = null;
        for (int i = 0; i < nlSize; i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elementName = node.getNodeName();
                if (ignoreNS) {
                    if (node.getLocalName().equals(name)) {
                        list.add(node);
                    }
                } else if (elementName.equals(name)) {
                    list.add(node);
                }
            }
        }
        return list;
    }

    public static List<Node> getAllChildren(Element element, String name) {
        NodeList nl = element.getElementsByTagName(name);
        int nls = nl.getLength();
        List<Node> found = new ArrayList<>(nls);
        for (int i = 0; i < nls; i++) {
            found.add(nl.item(i));
        }
        return found;
    }

    public static String getChildText(Node aNode, String name) {
        return getChildText(aNode, name, false);
    }

    public static String getChildText(Node aNode, String name, boolean ignoreNS) {
        Node child = getChild(aNode, name, -1, ignoreNS);
        if (child != null) {
            return getText(child);
        }
        return null;
    }

    public static Element getFirstChildElement(Node aNode) {
        NodeList list = aNode.getChildNodes();
        int length = list.getLength();
        for (int i = 0; i < length; i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        return null;
    }

    public static String getText(Node aNode) {
        String text = null;
        Node textNode = getTextNode(aNode, false);
        if (textNode != null) {
            text = textNode.getNodeValue();
            if (text != null) {
                if (text.length() == 0) {
                    return null;
                }
                text = StringUtils.stripToEmpty(text);
            }
        }
        return text;
    }

    public static String getNodeText(Node aNode) {
        switch (aNode.getNodeType()) {
            case Node.CDATA_SECTION_NODE:
                CDATASection cds = (CDATASection) aNode;
                return cds.getNodeValue();
            case Node.ATTRIBUTE_NODE:
                return ((Attr) aNode).getValue();
            case Node.ELEMENT_NODE:
                return getText(aNode);
        }
        return null;
    }

    public static final boolean isAttributeNode(Node aNode) {
        return aNode.getNodeType() == Node.ATTRIBUTE_NODE;
    }

    public static final boolean isElementNode(Node aNode) {
        return aNode.getNodeType() == Node.ELEMENT_NODE;
    }

    public static final boolean isCDataNode(Node aNode) {
        return aNode.getNodeType() == Node.CDATA_SECTION_NODE;
    }

    public static final boolean isTextNode(Node aNode) {
        return aNode.getNodeType() == Node.TEXT_NODE;
    }

    public static String getMixedContentText(Node aNode) {
        String text = getMixedText(aNode);
        if (text != null && text.length() == 0) {
            return null;
        }
        text = text.trim();
        return text;
    }

    public static Document loadXsdDocument(String inputName) {
        String filename = inputName;

        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        Document doc = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File inputFile = new File(filename);
            doc = builder.parse(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
            // throw new ContentLoadException(msg);
        }

        return doc;
    }

    /**
     * Transforms given XML String using the provided XSLT file (from classpath).
     *
     * @param xsltClasspathFile The XSLT classpath file
     * @param xml               The XML String
     * @return The transformed XML String
     * @throws TransformerException When an error occurs during transformation
     */
    public static String transformWithXslt(String xsltClasspathFile, String xml) throws TransformerException {
        Source xsltSource = new StreamSource(W3CDomUtil.class.getClassLoader().getResourceAsStream(xsltClasspathFile));
        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer(xsltSource);
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    public static Node getCDataNode(Node aNode) {
        NodeList childs = aNode.getChildNodes();
        for (int c = 0; c < childs.getLength(); c++) {
            Node aChild = childs.item(c);
            if (aChild.getNodeType() == Node.CDATA_SECTION_NODE) {
                // check cData
                return aChild;
            }
        }
        return null;
    }

    public static Node getTextNode(Node aNode, boolean create) {
        Node textNode = null;
        NodeList childs = aNode.getChildNodes();
        loop:
        for (int c = 0; c < childs.getLength(); c++) {
            Node aChild = childs.item(c);
            int nodeType = aChild.getNodeType();
            switch (nodeType) {
                case Node.CDATA_SECTION_NODE:
                    textNode = aChild;
                    break loop;
                case Node.TEXT_NODE:
                    textNode = aChild;
                    Node posCDataaChild = getCDataNode(aNode);
                    if (posCDataaChild != null) {
                        textNode = posCDataaChild;
                    }
                    break loop;
            }
        }
        if (create && textNode == null) {
            textNode = aNode.getOwnerDocument().createTextNode("");
            if (textNode == null) {
                aNode.appendChild(textNode);
            } else {
                aNode.insertBefore(textNode, aNode.getFirstChild());
            }
        }
        return textNode;
    }

    public static String getMixedText(Node aNode) {
        StringBuilder sb = new StringBuilder();
        // org.w3c.dom.Node textNode = null;
        NodeList childs = aNode.getChildNodes();
        for (int c = 0; c < childs.getLength(); c++) {
            Node aChild = childs.item(c);
            // check if content is null
            String nodeValue = aChild.getNodeValue();
            if (nodeValue != null) {
                nodeValue = nodeValue.trim();
                if (nodeValue.length() > 0) {
                    sb.append(nodeValue);
                }
            }
        }
        return sb.toString();
    }

    public static void setText(Node aNode, String text) {
        getTextNode(aNode, true).setNodeValue(text);
    }

    public static String getEncoding(Document dom) {
        if (dom instanceof DeferredDocumentImpl) {
            try {
                return ((DeferredDocumentImpl) dom).getXmlEncoding();
            } catch (Throwable t) {
                try {
                    return ((DeferredDocumentImpl) dom).getEncoding();
                } catch (Throwable t1) {
                    return getFileEncoding();
                }
            }
        }
        return null;
    }

    /**
     * Gets the value of a given processing instruction.
     *
     * @param startNode the Document to be searched
     * @param target    the name of the processing instruction
     * @return the value of the processing instruction.
     */
    public static Node getProcessingInstructionTarget(Node startNode, String target) {
        NodeList children = startNode.getChildNodes();
        int childCount = children.getLength();
        for (int i = 0; i < childCount; i++) {
            Node node = children.item(i);
            if (node instanceof ProcessingInstruction && node.getNodeName().equals(target)) {
                return node;
            }
        }
        return null;
    }

    public static String getNoNameSpaceSchemaLocation(Document dom) {
        return getDocElementValue(dom, "xsi:noNamespaceSchemaLocation");
    }

    public static String getNameSpaceSchemaLocation(Document dom) {
        return getDocElementValue(dom, "xsi:schemaLocation");
    }

    public static String getDocElementValue(Document dom, String key) {
        NamedNodeMap attrs = dom.getDocumentElement().getAttributes();
        Node aNode = attrs.getNamedItem(key);
        if (aNode != null) {
            return aNode.getNodeValue();
        } else {
            return null;
        }
    }

    /**
     * Gets the value of a given processing instruction.
     *
     * @param startNode the Document to be searched
     * @param target    the name of the processing instruction
     * @return the value of the processing instruction.
     */
    public static String getProcessingInstructionValue(Node startNode, String target) {
        Node targetNode = getProcessingInstructionTarget(startNode, target);
        if (targetNode != null) {
            return targetNode.getNodeValue();
        }
        return null;
    }

    public static List<String> getProcessingInstructionStrings(Node startNode) {
        NodeList children = startNode.getChildNodes();
        int childCount = children.getLength();
        List pi = new ArrayList(childCount);
        for (int i = 0; i < childCount; i++) {
            Node node = children.item(i);
            if (node instanceof ProcessingInstruction) {
                pi.add(formatPI(node));
            }
        }
        return pi;
    }

    public static List<ProcessingInstruction> getProcessingInstructions(Node startNode) {
        NodeList children = startNode.getChildNodes();
        int childCount = children.getLength();
        List<ProcessingInstruction> pis = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; i++) {
            Node node = children.item(i);
            if (node instanceof ProcessingInstruction) {
                pis.add((ProcessingInstruction) node);
            }
        }
        return pis;
    }

    public static ProcessingInstruction getProcessingInstruction(Node node) {
        NodeList children = node.getChildNodes();
        int childCount = children.getLength();
        Node subNode = null;
        for (int i = 0; i < childCount; i++) {
            subNode = children.item(i);
            if (subNode instanceof ProcessingInstruction) {
                return (ProcessingInstruction) subNode;
            }
        }
        return null;
    }

    public static String formatPI(Node node) {
        String pi = "<?" + node.getNodeName() + " " + node.getNodeValue() + "?>";
        return pi;
    }

    public static String getProcessingInstructionValue(Node startNode, String target, String attribute) {
        String value = getProcessingInstructionValue(startNode, target);
        if (value != null) {
            String searchString = attribute + EQUALS;
            int index = value.indexOf(searchString);
            if (index != -1) {
                value = value.substring(index + searchString.length());
                index = value.indexOf(";");
                if (index != -1) {
                    value = value.substring(0, index);
                }
            } else {
                value = null;
            }
        }
        return value;
    }

    public static String getXMLProcessingInstructionValue(Document document, String target, String attribute) {
        if (document != null) {
            String pInstructionValue = getProcessingInstructionValue(document, target);
            if (pInstructionValue != null && pInstructionValue.indexOf(attribute) != -1) {
                pInstructionValue = pInstructionValue.substring(pInstructionValue.indexOf(attribute) + attribute.length());
                pInstructionValue = pInstructionValue.replace('\"', '\'');
                // first search equals symbol
                for (int i = 0; i < pInstructionValue.length(); i++) {
                    if (pInstructionValue.charAt(i) == '\'') {
                        int indexOfSingleQuote = pInstructionValue.indexOf("\'", i + 1);
                        return pInstructionValue.substring(i + 1, indexOfSingleQuote);
                    }
                }
                return null;
            }
        }
        return null;
    }

    /**
     * Returns just the name of a global addressed XUI component. e.q. global:USAddress/name will return just the name USAddress
     *
     * @param xpath potential global xpath used to address global XUI definitions
     */
    public static String getGlobalNameFromXPath(String xpath) {
        int startIndex = xpath.indexOf(":") + 1;
        int endIndex = xpath.length();
        String globalXPath = null;
        int slIndex = xpath.indexOf("/");
        if (slIndex != -1) {
            endIndex = slIndex;
        }
        if (xpath.length() > endIndex) {
            globalXPath = xpath.substring(endIndex, xpath.length());
        }
        if (globalXPath != null) {
            globalXPath = "." + globalXPath;
        } else {
            globalXPath = ".";
        }
        String globalName = xpath.substring(startIndex, endIndex);
        return globalName;
    }

    /**
     * Returns just the relative address of a global addressed XUI component. e.q. global:USAddress/name will return just './name'
     *
     * @param xpath potential global xpath used to address global XUI definitions
     */
    public static String getGlobalAddressFromXPath(String xpath) {
        int endIndex = xpath.length();
        String globalXPath = null;
        int slIndex = xpath.indexOf("/");
        if (slIndex != -1) {
            endIndex = slIndex;
        }
        if (xpath.length() > endIndex) {
            globalXPath = xpath.substring(endIndex, xpath.length());
        }
        if (globalXPath != null) {
            globalXPath = "." + globalXPath;
        } else {
            globalXPath = ".";
        }
        return globalXPath;
    }

    public static Map<String, String> extractAnnotation(XSAnnotation annotationObject) {
        Map<String, String> annotations = null;
        String annotationString = annotationObject.getAnnotationString();
        if (annotationString != null && annotationString.length() > 0) {
            try {
                Document dom = createDocument(annotationString, null);
                List<Element> childNodes = getChildren(dom.getDocumentElement());
                if (childNodes.size() > 0) {
                    annotations = new HashMap<>();
                }
                Element docNode = null;
                int cs = childNodes.size();
                for (int i = 0; i < cs; i++) {
                    docNode = childNodes.get(i);
                    if (docNode.getNodeName().endsWith(APPINFO_DOCUMENTATION)) {
                        String text = getText(docNode);
                        String content = null;
                        if (text != null) {
                            NamedNodeMap attrs = docNode.getAttributes();
                            if (attrs.getLength() > 0) {
                                Node aNode = attrs.item(0);
                                if (aNode != null) {
                                    content = aNode.getNodeValue();
                                }
                            }
                            if (content == null) {
                                content = ANNOTATION_NLS_DEFAULT;
                            } else {
                                content = ANNOTATION_NLS + content;
                            }
                            if (text != null && content != null) {
                                annotations.put(content, text);
                            }
                        }
                    } else if (docNode.getNodeName().endsWith(APPINFO)) {
                        List<Element> annos = getChildren(docNode);
                        for (int a = 0; a < annos.size(); a++) {
                            Element anno = annos.get(a);
                            String annoVal = getText(anno);
                            if (annoVal != null) {
                                if (anno.getLocalName().equals(ANNOTATION_INHERIT)) {
                                    annotations.put(ANNOTATION_INHERIT, annoVal);
                                } else if (anno.getLocalName().equals(ANNOTATION_READONLY)) {
                                    annotations.put(ANNOTATION_READONLY, annoVal);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return annotations;
    }

    public static String toXML(Document doc) {
        return serialize(doc);
    }

    public static void merge(Node originalParentNode, Node nodeToMerge) {
        Document targetDoc = originalParentNode.getOwnerDocument();
        if (nodeToMerge.getNodeType() == Node.ELEMENT_NODE) {
            Node origNode = getChild(originalParentNode, nodeToMerge.getNodeName());
            if (origNode == null) {
                Node nodeToImport = targetDoc.importNode(nodeToMerge, true);
                Node importedNode = originalParentNode.appendChild(nodeToImport);
                if (nodeToImport.getChildNodes().getLength() == 1) {
                    ((Element) importedNode).setAttributeNS(TARGET_PI, "IS_INCLUDED", "true");
                } else {
                    markAsIncluded(importedNode);
                }
            } else {
                // check attributes first
                if (nodeToMerge.hasAttributes()) {
                    String attributeName = null;
                    Node attribute = null;
                    NamedNodeMap nnm = nodeToMerge.getAttributes();
                    int nnms = nnm.getLength();
                    for (int i = 0; i < nnms; i++) {
                        attribute = nnm.item(i);
                        attributeName = attribute.getNodeName();
                        boolean found = false;
                        if (origNode.getAttributes() != null) {
                            Node foundAttribute = origNode.getAttributes().getNamedItem(attributeName);
                            if (foundAttribute != null) {
                                found = true;
                            }
                        }
                        if (!found) {
                            // Node impAttr=targetDoc.creaimportNode(attribute, false);
                            if (origNode instanceof Element) {
                                ((Element) origNode).setAttributeNS(attribute.getNamespaceURI(), attributeName, attribute.getNodeValue());
                            } else {
                                targetDoc.importNode(attribute, false);
                                origNode.appendChild(attribute);
                            }
                        }
                    }
                }
                NodeList nlist = nodeToMerge.getChildNodes();
                int nlength = nlist.getLength();
                for (int i = 0; i < nlength; i++) {
                    Node child = nlist.item(i);
                    merge(origNode, child);
                }
            }
        } else if (nodeToMerge.getNodeType() == Node.TEXT_NODE) {
            String data = nodeToMerge.getNodeValue().trim();
            if (data.length() > 0) {
                Text textNode = targetDoc.createTextNode(data);
                originalParentNode.appendChild(textNode);
            }
        }
    }

    private static void markAsIncluded(Node node) {
        if (node.getChildNodes().getLength() == 1) {
            ((Element) node).setAttributeNS(TARGET_PI, "IS_INCLUDED", "true");
        }
        NodeList nlist = node.getChildNodes();
        int nlength = nlist.getLength();
        for (int i = 0; i < nlength; i++) {
            Node child = nlist.item(i);
            markAsIncluded(child);
        }
    }

    public static Document createDocument(Reader reader) throws XMLParseException, SAXException, IOException {
        return createDocument(new InputSource(reader));
    }

    public static Hashtable getNameSpaces(Document dom) {
        Hashtable namespaces = new Hashtable();
        if (dom != null) {
            NamedNodeMap nodeMap = dom.getDocumentElement().getAttributes();
            Node attributeNode = null;
            String prefix = null;
            String uri = null;
            int nMs = nodeMap.getLength();
            for (int i = 0; i < nMs; i++) {
                attributeNode = nodeMap.item(i);
                if (attributeNode.getNodeName().startsWith("xmlns:")) {
                    Attr attr = (Attr) attributeNode;
                    prefix = attr.getLocalName();
                    uri = attr.getNodeValue();
                    namespaces.put(prefix, uri);
                }
            }
        }
        return namespaces;
    }

    public static Hashtable getNameSpaceToPrefixMapping(Document dom) {
        Hashtable namespaces = null;
        NamedNodeMap nodeMap = dom.getDocumentElement().getAttributes();
        Node attributeNode = null;
        String prefix = null;
        String uri = null;
        // we need null because of identifing namespace profider!
        if (nodeMap.getLength() > 0) {
            namespaces = new Hashtable();
        }
        int nMs = nodeMap.getLength();
        for (int i = 0; i < nMs; i++) {
            attributeNode = nodeMap.item(i);
            if (attributeNode.getNodeName().startsWith("xmlns:")) {
                Attr attr = (Attr) attributeNode;
                prefix = attr.getLocalName();
                uri = attr.getNodeValue();
                if (prefix != null && !prefix.equals("")) {
                    namespaces.put(uri, prefix);
                } else {

                    LOGGER.warn("don't add nsuri " + uri + " with empty prefix:" + prefix);
                }
            }
        }
        return namespaces;
    }

    public static Hashtable getXMLHeaderAttributes(Document dom) {
        Hashtable attributes = null;
        NamedNodeMap nodeMap = dom.getDocumentElement().getAttributes();
        Node attributeNode = null;
        String name = null;
        String value = null;
        // we need null because of identifing namespace profider!
        if (nodeMap.getLength() > 0) {
            attributes = new Hashtable();
        }
        for (int i = 0; i < nodeMap.getLength(); i++) {
            attributeNode = nodeMap.item(i);
            Attr attr = (Attr) attributeNode;
            name = attr.getName();
            value = attr.getNodeValue();
            attributes.put(name, value);
        }
        return attributes;
    }

    public static String getDefaultNameSpace(Document dom) {
        String defNS = null;
        NamedNodeMap nodeMap = dom.getDocumentElement().getAttributes();
        Node attributeNode = null;
        String uri = null;
        for (int i = 0; i < nodeMap.getLength(); i++) {
            attributeNode = nodeMap.item(i);
            if (attributeNode.getNodeName().equals(XMLNS)) {
                Attr attr = (Attr) attributeNode;
                uri = attr.getNodeValue();
                defNS = uri;
                break;
            }
        }
        return defNS;
    }

    public static String getDefaultNameSpace(Node instanceElement) {
        String defNS = null;
        NamedNodeMap nodeMap = instanceElement.getAttributes();
        Node attributeNode = null;
        String uri = null;
        for (int i = 0; i < nodeMap.getLength(); i++) {
            attributeNode = nodeMap.item(i);
            if (attributeNode.getNodeName().equals(XMLNS)) {
                Attr attr = (Attr) attributeNode;
                uri = attr.getNodeValue();
                defNS = uri;
                break;
            }
        }
        return defNS;
    }

    public static void getAllIncludedURLs(URL xsdURL, List includeList, boolean onlyValue, boolean rootIncluded) {
        if (xsdURL != null) {
            try {
                Document dom = createDocument(xsdURL);
                if (rootIncluded) {
                    includeList.add(serialize(dom));
                }
                NodeList includes = dom.getElementsByTagName("xs:include");
                for (int i = 0; i < includes.getLength(); i++) {
                    Element includeElement = (Element) includes.item(i);
                    String includeUrl = includeElement.getAttribute(SCHEMA_LOCATION);
                    if (includeUrl != null && includeUrl.length() > 0) {
                        getAllIncludedURLs(getUserURL(xsdURL, includeUrl), includeList, onlyValue, true);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private static void addAttribute(Hashtable result, Element element, String name) {
        String attrValue = element.getAttribute(name);
        if (attrValue != null && attrValue.length() > 0) {
            result.put(name, attrValue);
        }
    }

    public static Hashtable getAllSchemaBaseAttributes(Document dom) {
        Hashtable result = new Hashtable();
        if (dom != null) {
            try {
                Element schemaElement = dom.getDocumentElement();
                addAttribute(result, schemaElement, SchemaSymbols.ATT_TARGETNAMESPACE);
                addAttribute(result, schemaElement, SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT);
                addAttribute(result, schemaElement, SchemaSymbols.ATT_ELEMENTFORMDEFAULT);
                addAttribute(result, schemaElement, SchemaSymbols.ATT_DEFAULT);
                addAttribute(result, schemaElement, SchemaSymbols.ATT_SCHEMALOCATION);
                addAttribute(result, schemaElement, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
                addAttribute(result, schemaElement, XMLNS);
                addAttribute(result, schemaElement, org.apache.xerces.impl.xs.SchemaSymbols.ATT_VERSION);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return result;
    }

    public static Hashtable getAllSchemaBaseAttributes(String xsdString) {
        try {
            return getAllSchemaBaseAttributes(createDocument(xsdString));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static Hashtable getAllSchemaBaseAttributes(URL xsdURL) {
        try {
            return getAllSchemaBaseAttributes(createDocument(xsdURL));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static String getPrefixForNamespace(Document doc, String nameSpace) {
        Hashtable docNameSpaces = getNameSpaces(doc);
        Iterator entries = docNameSpaces.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry aEntry = (Map.Entry) entries.next();
            if (aEntry.getValue().toString().indexOf(nameSpace) >= 0) {
                return aEntry.getKey().toString();
            }
        }
        return null;
    }

    public static void mergeSchemaWithIncludedSchemas(URL xsdURL, Document baseDoc, boolean isRoot, String startXSDPrefix) {
        if (xsdURL != null) {
            try {
                Document dom = createDocument(xsdURL);
                String nameSpacePrefix = getPrefixForNamespace(dom, "XMLSchema");
                if (nameSpacePrefix == null) {
                    nameSpacePrefix = "xsi:";
                }
                if (startXSDPrefix == null) {
                    startXSDPrefix = nameSpacePrefix;
                }
                if (!isRoot) {
                    NodeImporter.doImport(dom.getDocumentElement().getChildNodes(), baseDoc.getDocumentElement(), NodeImporter.ENUMERATION_MAPPING_STRATEGY_ADDCHILD, startXSDPrefix);
                }
                NodeList includes = dom.getElementsByTagName(nameSpacePrefix + ":include");
                for (int i = 0; i < includes.getLength(); i++) {
                    Element includeElement = (Element) includes.item(i);
                    String includeUrl = includeElement.getAttribute(SCHEMA_LOCATION);
                    if (includeUrl != null && includeUrl.length() > 0) {
                        mergeSchemaWithIncludedSchemas(getUserURL(xsdURL, includeUrl), baseDoc, false, startXSDPrefix);
                    }
                }
                // do cleanup!
                // remove all includes!
                NodeList includeList = baseDoc.getDocumentElement().getElementsByTagName(nameSpacePrefix + ":include");
                for (int i = includeList.getLength() - 1; i >= 0; i--) {
                    Node includeToRemove = includeList.item(i);
                    Node includeParent = includeToRemove.getParentNode();
                    if (includeParent != null) {
                        includeParent.removeChild(includeList.item(i));
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private static URL getUserURL(URL xsdURL, String includeUrl) {
        return xsdURL;
    }

    public static Hashtable extractData(Document dom, String tagName) throws TransformerException {
        Hashtable metaData = new Hashtable();
        NodeList nodeList = dom.getDocumentElement().getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            NodeList xmlElementMetaData = nodeList.item(0).getChildNodes();
            for (int i = 0; i < xmlElementMetaData.getLength(); i++) {
                Node aMetaNode = xmlElementMetaData.item(i);
                String name = aMetaNode.getNodeName();
                if (aMetaNode.getNodeType() == Node.ELEMENT_NODE) {
                    String value = getText(aMetaNode);
                    if (name != null && value != null) {
                        metaData.put(name, value);
                    }
                }
            }
        }
        return metaData;
    }

    public static final List<String> getNamespaceDeclarations(Map nameSpaces, boolean isXuiDom, boolean forceSerialization, String defaultNameSpace) {
        List<String> xmlNS = null;
        if (nameSpaces != null) {
            Iterator<String> nsKeys = nameSpaces.keySet().iterator();
            xmlNS = new ArrayList<>();
            while (nsKeys.hasNext()) {
                String uri = nsKeys.next();
                String xmlNSText = null;
                if (uri != null && uri.length() > 0 && !uri.equals(HTTP_WWW_W3_ORG_2001_XMLSCHEMA)) {
                    String prefix = (String) nameSpaces.get(uri);
                    if (prefix != null && prefix.length() > 0) {
                        if (!prefix.startsWith(XSD)) {
                            xmlNSText = "xmlns:" + prefix + EQUALS + "\"" + uri + "\"";
                            xmlNS.add(xmlNSText);
                        }
                    } else {
                        if (forceSerialization && isXuiDom) {
                            xmlNSText = "xmlns:" + prefix + EQUALS + "\"" + uri + "\"";
                        } else {
                            xmlNSText = "xmlns=" + "\"" + uri + "\"";
                        }
                        xmlNS.add(xmlNSText);
                    }
                }
            }
            // check if default ns exists
            if (defaultNameSpace != null) {
                boolean defNsExist = false;
                for (int xns = 0; xns < xmlNS.size(); xns++) {
                    String nsDef = xmlNS.get(xns);
                    if (nsDef.startsWith("xmlns=")) {
                        defNsExist = true;
                        break;
                    }
                }
                if (!defNsExist) {
                    xmlNS.add("xmlns=" + "\"" + defaultNameSpace + "\"");
                }
            }
        }
        return xmlNS;
    }

    public static List<Element> getChildren(Node aNode) {
        NodeList nl = aNode.getChildNodes();
        int length = nl.getLength();
        List list = new ArrayList(length);
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                Node node = nl.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    list.add(node);
                }
            }
        }
        return list;
    }

    public static final Node getParent(Node aNode, String regex) {
        Pattern p = Pattern.compile(regex);
        return getParent(aNode, p);
    }

    public static final Node getParent(Node aNode, Pattern regex) {
        Node parent = aNode;
        while ((parent = parent.getParentNode()) != null) {
            if (regex == null || regex.matcher(parent.getNodeName()).matches()) {
                return parent;
            }
        }
        return null;
    }

    public static final List<Node> getParents(Node aNode, String regex) {
        Node parent = aNode;
        Pattern p = (regex != null ? Pattern.compile(regex) : null);
        List<Node> result = new ArrayList<>(5);
        while ((parent = parent.getParentNode()) != null) {
            if (p == null || p.matcher(parent.getNodeName()).matches()) {
                result.add(parent);
            }
        }
        return result;
    }

    public static final String getParentPath(List<Node> nodes, boolean asc) {
        StringBuilder result = new StringBuilder();
        if (asc) {
            for (int n = 0; n < nodes.size(); n++) {
                result.append("/");
                result.append(nodes.get(n).getNodeName());
            }
        } else {
            for (int n = nodes.size() - 1; n >= 0; n--) {
                result.append("/");
                result.append(nodes.get(n).getNodeName());
            }
        }
        return result.toString();
    }

    public static Map elementsToMap(List propElements, String keyField, String valueField) {
        HashMap props = new HashMap(propElements.size());
        for (int p = 0; p < propElements.size(); p++) {
            Element pElem = (Element) propElements.get(p);
            props.put(getChildText(pElem, keyField), getChildText(pElem, valueField));
        }
        return props;
    }

    public static List elementsToList(List propElements, String field) {
        if (propElements == null) {
            return new ArrayList();
        }
        List props = new ArrayList(propElements.size());
        for (int p = 0; p < propElements.size(); p++) {
            Element pElem = (Element) propElements.get(p);
            if (field == null) {
                props.add(getText(pElem));
            } else {
                props.add(getChildText(pElem, field));
            }
        }
        return props;
    }

    public static String getProcessingInstructionValue(ProcessingInstruction pi, String name) {
        String value = null;
        if (pi != null) {
            value = pi.getData();
            if (value != null) {
                String searchString = name + EQUALS;
                int index = value.indexOf(searchString);
                if (index != -1) {
                    index += searchString.length() + 1;
                    int endIdx = value.indexOf("\"", index);
                    if (endIdx == -1) {
                        endIdx = value.length();
                    }
                    value = value.substring(index, endIdx);
                } else {
                    value = null;
                }
            }
        }
        return value;
    }

    public static Node getAttribute(Node aType, String name) {
        if (aType != null) {
            NamedNodeMap nnm = aType.getAttributes();
            if (nnm != null) {
                return nnm.getNamedItem(name);
            }
        }
        return null;
    }

    public static String getAttributeValue(Node aType, String name) {
        Node attNode = getAttribute(aType, name);
        if (attNode != null) {
            return attNode.getNodeValue();
        }
        return null;
    }

    public static ProcessingInstruction searchPrevProcessingInstruction(Node startNode, String target) {
        Node prevSib = startNode.getPreviousSibling();
        if (prevSib == null || prevSib.getNodeType() == Node.ELEMENT_NODE) {
            return null;
        } else if (prevSib instanceof ProcessingInstruction) {
            if (target != null && prevSib.getNodeName().equals(target)) {
                return (ProcessingInstruction) prevSib;
            } else {
                return (ProcessingInstruction) prevSib;
            }
        } else {
            return searchPrevProcessingInstruction(prevSib, target);
        }
    }

    public static List<ProcessingInstruction> searchPrevProcessingInstructions(Node startNode, String target) {
        List<ProcessingInstruction> found = new ArrayList<>();
        boolean stop = false;
        while (!stop) {
            if (startNode != null) {
                ProcessingInstruction pi = searchPrevProcessingInstruction(startNode, target);
                if (pi != null) {
                    found.add(pi);
                    startNode = pi;
                } else {
                    stop = true;
                }
            }
        }
        return found;
    }

    public static int getNodePos(NodeList nl, Node startNode) {
        int ls = nl.getLength();
        for (int n = 0; n < ls; n++) {
            Node aN = nl.item(n);
            if (aN == startNode) {
                return n;
            }
        }
        return -1;
    }

    public static Node cleanEmptyText(Node node) {
        NodeList childNodes = node.getChildNodes();

        for (int n = childNodes.getLength() - 1; n >= 0; n--) {
            Node child = childNodes.item(n);
            short nodeType = child.getNodeType();

            if (nodeType == Node.ELEMENT_NODE) {
                cleanEmptyText(child);
            } else if (nodeType == Node.TEXT_NODE) {
                String trimmedNodeVal = child.getNodeValue().trim();
                if (trimmedNodeVal.length() == 0) {
                    node.removeChild(child);
                } else {
                    child.setNodeValue(trimmedNodeVal);
                }
            } else if (nodeType == Node.COMMENT_NODE) {
                node.removeChild(child);
            }
        }
        return node;
    }

    public static List<ProcessingInstruction> getProcessingInstructions(NodeList levelNodes, Node startNode, String target, int pos) {
        if (levelNodes == null) {
            return searchPrevProcessingInstructions(startNode, target);
        }
        if (pos == -1) {
            pos = getNodePos(levelNodes, startNode);
        }
        pos--;
        if (pos < 0) {
            return null;
        }
        List<ProcessingInstruction> found = new ArrayList<>();
        for (; pos >= 0; pos--) {
            Node aN = levelNodes.item(pos);
            if (aN instanceof ProcessingInstruction) {
                if (target != null) {
                    if (aN.getNodeName().equals(target)) {
                        found.add((ProcessingInstruction) aN);
                    }
                } else {
                    found.add((ProcessingInstruction) aN);
                }
            } else if (aN.getNodeType() == Node.ELEMENT_NODE) {
                break;
            }
        }
        return found;
    }

    public static String serializeNode(Node node) throws TransformerException {

        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Pretty-prints a DOM document to XML using DOM Load and Save's LSSerializer.
     * Note that the "format-pretty-print" DOM configuration parameter can only be set in JDK 1.6+.
     * https://docs.oracle.com/javase/7/docs/api/org/w3c/dom/DOMConfiguration.html
     *
     * @param doc
     * @return formatted xml output
     * @see LSSerializer
     * @see LSOutput
     * @see DOMConfiguration
     */
    public static final String serialize(Document doc) {
        return serialize(doc, false);
    }

    public static final String serialize(Document doc, boolean prettyPrint) {
        DOMImplementation domImplementation = doc.getImplementation();
        if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
            DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
            LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
            DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
            if (prettyPrint) {
                if (domConfiguration.canSetParameter(FORMAT_PRETTY_PRINT, Boolean.TRUE)) {
                    lsSerializer.getDomConfig().setParameter(FORMAT_PRETTY_PRINT, Boolean.TRUE);
                }
                LSOutput lsOutput = domImplementationLS.createLSOutput();
                lsOutput.setEncoding(StandardCharsets.UTF_8.name());
                StringWriter stringWriter = new StringWriter();
                lsOutput.setCharacterStream(stringWriter);
                lsSerializer.write(doc, lsOutput);
                return stringWriter.toString();
            } else {
                throw new UnsupportedOperationException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
            }
        } else {
            throw new UnsupportedOperationException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
        }
    }
}


