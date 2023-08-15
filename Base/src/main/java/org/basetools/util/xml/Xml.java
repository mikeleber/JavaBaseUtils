package org.basetools.util.xml;

import org.basetools.util.StringUtils;
import org.basetools.util.sort.FastQSort;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//https://argonrain.wordpress.com/2009/10/27/000/
public class Xml {

    private final String name;
    private final Map<String, String> nameAttributes = new LinkedHashMap<>();
    private final Map<String, ArrayList<Xml>> nameChildren = new LinkedHashMap<>();
    private String content;

    public Xml(InputStream inputStream, String rootName) {
        this(rootElement(new InputSource(inputStream), rootName));
    }

    private Xml(Element element) {
        name = element.getNodeName();
        content = getTextContentOnly(element);
        NamedNodeMap namedNodeMap = element.getAttributes();
        int n = namedNodeMap.getLength();
        for (int i = 0; i < n; i++) {
            Node node = namedNodeMap.item(i);
            String name = node.getNodeName();
            addAttribute(name, node.getNodeValue());
        }
        NodeList nodes = element.getChildNodes();
        n = nodes.getLength();
        for (int i = 0; i < n; i++) {
            Node node = nodes.item(i);
            int type = node.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                Xml child = new Xml((Element) node);
                addChild(node.getNodeName(), child);
            }
        }
    }

    private static Element rootElement(InputSource inputStream, String rootName) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element rootElement = document.getDocumentElement();
            if (!rootElement.getNodeName().equals(rootName)) {
                throw new RuntimeException("Could not find root node: " + rootName);
            }
            return rootElement;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } catch (ParserConfigurationException exception) {
            throw new RuntimeException(exception);
        } catch (SAXException exception) {
            throw new RuntimeException(exception);
        }
    }

    public byte getValue(String propName, byte defValue, boolean prefDef) {
        if (prefDef) return childBooleanToByte(propName, defValue);
        else
            return defValue == -1 ? childBooleanToByte(propName, defValue) : defValue;
    }

    public byte getAttrValue(String propName, byte defValue, boolean prefDef) {
        if (prefDef) return attrBooleanToByte(propName, defValue);
        else
            return defValue == -1 ? attrBooleanToByte(propName, defValue) : defValue;
    }

    public short getAttrValue(String propName, short defValue, boolean prefDef) {
        if (prefDef) return optAttrShort(propName, defValue);
        else
            return defValue == -1 ? optAttrShort(propName, defValue) : defValue;
    }

    public int getAttrValue(String propName, int defValue, boolean prefDef) {
        if (prefDef) return optAttrInteger(propName, defValue);
        else
            return defValue == -1 ? optAttrInteger(propName, defValue) : defValue;
    }

    public String getAttrValue(String propName, String defValue, boolean prefDef) {
        if (prefDef) return optAttrString(propName, defValue);
        else
            return defValue == null ? optAttrString(propName, defValue) : defValue;
    }

    public double getValue(String propName, double defValue, boolean prefDef) {
        if (prefDef) return childToDouble(propName, defValue);
        else
            return defValue == -1 ? childToDouble(propName, defValue) : defValue;
    }
    public boolean getValue(String propName, Boolean defValue, boolean prefDef) {
        if (prefDef) return childToBoolean(propName, defValue);
        else
            return defValue == null ? childToBoolean(propName, defValue) : defValue;
    }
    public int getValue(String propName, int defValue, boolean prefDef) {
        if (prefDef) return childToInt(propName, defValue);
        else
            return defValue == -1 ? childToInt(propName, defValue) : defValue;
    }
    public short getValue(String propName, short defValue, boolean prefDef) {
        if (prefDef) return (short)childToInt(propName, defValue);
        else
            return defValue == -1 ? (short) childToInt(propName, defValue) : defValue;
    }

    public String getValue(String propName, String defValue, boolean prefDef) {
        if (prefDef) return childToString(propName, defValue);
        else
            return defValue == null ? childToString(propName, defValue) : defValue;
    }

    public String getTextContentOnly(Element element) {
        StringBuilder textContent = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node n = childNodes.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                if (textContent == null) {
                    textContent = new StringBuilder();
                }
                String value = n.getNodeValue();
                textContent.append(value.trim());
            }
        }
        return textContent != null ? textContent.toString() : null;
    }

    public void addAttribute(String name, String value) {
        nameAttributes.put(name, value);
    }

    private void addChild(String name, Xml child) {
        ArrayList<Xml> children = nameChildren.get(name);
        if (children == null) {
            children = new ArrayList<>();
            nameChildren.put(name, children);
        }
        children.add(child);
    }

    public Xml(InputSource input, String rootName) {
        this(rootElement(input, rootName));
    }

    public Xml(String nodeName, String textContent) {
        name = nodeName;
        content = textContent;
    }

    public Xml(String rootName) {
        name = rootName;
    }

    public static Xml from(String xmlContent, String rootName) {
        return new Xml(new ByteArrayInputStream(xmlContent.getBytes()), rootName);
    }

    public static Xml from(Reader input, String rootName) {
        return new Xml(new InputSource(input), rootName);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addChild(Xml xml) {
        addChild(xml.name(), xml);
    }

    public String name() {
        return name;
    }

    public Xml getChild(String... pathNames) {
        Xml current=this;
        for (String name : pathNames) {
            current= current.optChild(name);
            if (current==null){
                return null;
            }
        }
        return current;
    }


    public Xml child(String name) {
        Xml child = optChild(name);
        if (child == null) {
            throw new RuntimeException("Could not find child node: " + name);
        }
        return child;
    }

    public Xml optChild(String name) {
        ArrayList<Xml> children = children(name);
        int n = children.size();
        if (n > 1) {
            throw new RuntimeException("Could not find individual child node: " + name);
        }
        return n == 0 ? null : children.get(0);
    }

    public ArrayList<Xml> children(String name) {
        ArrayList<Xml> children = nameChildren.get(name);
        return children == null ? new ArrayList<>() : children;
    }

    public String[] childContent(String name) {
        return children(name).stream().map((xmlev) -> xmlev.content()).toArray(String[]::new);
    }

    public String content() {
        return content;
    }

    public Stream<Xml> childStream(String name) {
        ArrayList<Xml> children = children(name);
        int n = children.size();
        if (n > 1) {
            throw new RuntimeException("Could not find individual child node: " + name);
        }
        return children.stream();
    }

    public void sortChild(String name, Comparator comparator) {
        ArrayList<Xml> nameChilds = nameChildren.get(name);
        FastQSort.sortList(nameChilds, comparator);
    }

    public boolean option(String name) {
        return optChild(name) != null;
    }

    public Integer optAttrInteger(String name) {
        String string = optAttrString(name);
        return string == null ? null : integerAttr(name);
    }

    public String optAttrString(String name) {
        return nameAttributes.get(name);
    }

    public short optAttrShort(String name, short defaultValue) {
        String string = optAttrString(name);
        return string == null ? defaultValue : shortAttr(name);
    }

    public short shortAttr(String name) {
        return Short.parseShort(stringAttr(name));
    }

    public boolean booleanAttr(String name) {
        String attrVal = optAttrString(name);
        return StringUtils.isTrue(attrVal);
    }

    public String stringAttr(String name) {
        String value = optAttrString(name);
        if (value == null) {
            throw new RuntimeException(
                    "Could not find attribute: " + name + ", in node: " + this.name);
        }
        return value;
    }

    public String optAttrString(String name, String defaultValue) {
        String value = nameAttributes.get(name);
        return value == null ? defaultValue : value;
    }

    public Integer optAttrInteger(String name, int defaultValue) {
        String string = optAttrString(name);
        return string == null ? defaultValue : integerAttr(name);
    }

    public int integerAttr(String name) {
        return Integer.parseInt(stringAttr(name));
    }

    public Double optAttrDouble(String name) {
        String string = optAttrString(name);
        return string == null ? null : doubleAttrValue(name);
    }

    public double doubleAttrValue(String name) {
        return Double.parseDouble(optAttrString(name));
    }

    public Double doubleAttrValue(String name, double defaultValue) {
        String val = optAttrString(name);
        if (val == null || val.length() == 0) {
            return defaultValue;
        }
        return Double.parseDouble(val);
    }

    public double intAttrValue(String name) {
        return Integer.parseInt(optAttrString(name));
    }

    public int intAttrValue(String name, int defaultValue) {
        String val = optAttrString(name);
        if (val == null || val.length() == 0) {
            return defaultValue;
        }
        return Integer.parseInt(val);
    }

    public boolean booleanAttrValue(String name) {
        return Boolean.parseBoolean(optAttrString(name));
    }

    public boolean booleanAttrValue(String name, boolean defaultValue) {
        return isTrue(optAttrString(name), defaultValue);
    }

    private static boolean isTrue(String val, boolean defaultValue) {
        if (val == null || val.length() == 0) {
            return defaultValue;
        }
        return (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("y") || val.equalsIgnoreCase("yes") || val.equalsIgnoreCase("1") || val.equalsIgnoreCase("1.0"));
    }

    public Optional<String> getFirstChildName() {
        return nameChildren.keySet().stream().findFirst();
    }

    public List<String> getChildNames() {
        return nameChildren.keySet().stream().collect(Collectors.toList());
    }

    public String childToString(String name) {
        return childToString(name, null);
    }

    public String childToString(String name, String defaultValue) {
        Xml child = optChild(name);
        if (child == null) {
            return defaultValue;
        } else {
            return child.content(defaultValue);
        }
    }

    public String content(String defaultValue) {
        return content == null ? defaultValue : content;
    }

    public boolean childToBoolean(String name, boolean defaultValue) {
        Xml child = optChild(name);
        if (child == null) {
            return defaultValue;
        } else {
            return child.contentToBoolean(defaultValue);
        }
    }

    public boolean contentToBoolean(boolean defaultValue) {
        return isTrue(content, defaultValue);
    }

    public int childToInt(String name, int defaultValue) {
        Xml child = optChild(name);
        if (child == null) {
            return defaultValue;
        } else {
            return child.contentToInt(defaultValue);
        }
    }

    public int contentToInt(int defaultValue) {
        if (content == null) {
            return defaultValue;
        }
        return Integer.parseInt(content);
    }
    public short contentToShort(short defaultValue) {
        if (content == null) {
            return defaultValue;
        }
        return Short.parseShort(content);
    }
    public byte childBooleanToByte(String name, byte defaultValue) {
        Xml child = optChild(name);
        if (child == null || child.content == null || child.content.length() == 0) {
            return defaultValue;
        } else {
            return (byte) (child.contentToBoolean(false) ? 1 : 0);
        }
    }

    public byte attrBooleanToByte(String name, byte defaultValue) {
        String val = optAttrString(name);
        if (val == null || val.length() == 0) {
            return defaultValue;
        } else {
            return (byte) (isTrue(val, false) ? 1 : 0);
        }
    }

    public double childToDouble(String name, double defaultValue) {
        Xml child = optChild(name);
        if (child == null) {
            return defaultValue;
        } else {
            return child.contentToDouble(defaultValue);
        }
    }

    public double contentToDouble(double defaultValue) {
        if (content == null) {
            return defaultValue;
        }
        return Double.parseDouble(content);
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        toXML(sb);
        return sb.toString();
    }

    public void toXML(StringBuilder xml) {
        xml.append("<");
        xml.append(name);

        for (Map.Entry attrib : nameAttributes.entrySet()) {
            xml.append(" ");
            xml.append(attrib.getKey());
            xml.append("=");
            xml.append("\"");
            xml.append(attrib.getValue());
            xml.append("\"");
        }
        xml.append(">");
        for (Map.Entry<String, ArrayList<Xml>> child : nameChildren.entrySet()) {
            child.getValue().forEach(xmlEntry -> xmlEntry.toXML(xml));
        }
        if (content != null && content.length() > 0 && content.trim().length() > 0) {
            xml.append(content);
        }
        xml.append("</");
        xml.append(name);
        xml.append(">");
    }
}
