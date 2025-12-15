package org.basetools.util.xml.xpath;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class XPathNode<U> {
    public static final String SIMPLE_CONTENT_TEXT = "text()";
    // private static final short EXP_NODE = 1;
    public static final short LIST_NODE = 1;
    public static final short ATTR_NODE = 2;
    public static final short SELF_NODE = 4;
    public static final short TEXT_NODE = 5;
    public static final short PARENT_NODE = 6;
    public static final short IDX_NODE = 7;
    public static final short ALL_NODE = 8;
    public static final short ASTERIX_NODE = 9;
    public static final short ASTERIX_ATTR_NODE = 10;
    public static final short VAR_REF_NODE = 11;
    public static final short DEFAULT_NODE = 0;
    public static final XPathError ERROR_TO_COMPLEX = new XPathError("expression is complex");
    private static final String PARENT = "..";
    // private static final char ASTERIX = '*';
    private static final String ATTR = "@";
    private static final String SELF = ".";
    private static final char NS_SEP = ':';
    private static final char CLOSE_BRACKET = ']';
    private static final char OPEN_BRACKET = '[';
    protected int _endPos = 0;
    private String _xNodeName;
    private String _xNodeNSPfix;
    private String _xNodeNSpace;
    private U _userObject;
    private boolean _isAsterix = false;
    private boolean _isRoot = false;
    private short _nodeType = DEFAULT_NODE;
    private String _xNodeExpression;
    private int _xNodeIDX = -1;
    private List<XPathNode<U>> _childs = null;
    private List<XPathNode<U>> _sameAxis = null;
    private boolean _isLast;

    public XPathNode(String xNodeValue, int endPos, boolean check, Map<String, String> nsMapping) throws XPathError {
        this(xNodeValue, check, nsMapping);
        _endPos = endPos;
    }

    public XPathNode(String xNodeValue, boolean check, Map<String, String> nsMappings) throws XPathError {
        this();
        parse(xNodeValue, check, nsMappings);
    }

    public XPathNode() {
    }

    public XPathNode(String xNodeValue, int endPos, boolean check) throws XPathError {
        this(xNodeValue, check, false);
        _endPos = endPos;
    }

    public XPathNode(String xNodeValue, boolean check, boolean isRoot) throws XPathError {
        this();
        _isRoot = isRoot;
        parse(xNodeValue, check);
    }

    public XPathNode(String xNodeValue, int endPos, boolean check, boolean isRoot) throws XPathError {
        this(xNodeValue, check, isRoot);
        _endPos = endPos;
    }

    public XPathNode(String xNodeValue, boolean check) throws XPathError {
        this(xNodeValue, check, false);
    }

    public static String extractNodeNS(String value) {
        int nsIndex = value.lastIndexOf(NS_SEP);
        if (nsIndex >= 0) {
            return value.substring(0, nsIndex);
        } else {
            return null;
        }
    }

    public static String extractNodeName(int start, String value) {
        int bIndex = value.indexOf(OPEN_BRACKET, start);
        if (bIndex >= 0) {
            value = value.substring(start, bIndex);
        }
        return value;
    }

    public static String extractNodeExpression(int start, String value) {
        int lastBIndex = value.lastIndexOf(CLOSE_BRACKET);
        if (lastBIndex >= 0) {
            int firstBIndex = value.indexOf(OPEN_BRACKET, start);
            return value.substring(firstBIndex + 1, lastBIndex);
        }
        return null;
    }

    public  void parse(String xNodeValue, boolean check, Map<String, String> nsMappings) throws XPathError {
        _isAsterix = false;
        _nodeType = DEFAULT_NODE;
        _xNodeNSPfix = extractNodeNS(xNodeValue);
        _xNodeNSpace = translateNSPrefix(nsMappings);
        if (_xNodeNSPfix != null) {
            String nn = xNodeValue.substring(_xNodeNSPfix.length() + 1);
            _xNodeName = extractNodeName(0, nn);
        } else {
            _xNodeName = extractNodeName(0, xNodeValue);
        }
        if (_xNodeName != null) {
            if (_xNodeName.equals(SELF)) {
                _nodeType = SELF_NODE;
            } else if (PARENT.equals(_xNodeName)) {
                _nodeType = PARENT_NODE;
            } else if (SIMPLE_CONTENT_TEXT.equalsIgnoreCase(_xNodeName)) {
                _nodeType = TEXT_NODE;
            } else if (_xNodeName.length() == 0) {
                _nodeType = ALL_NODE;
            } else if (_xNodeName.startsWith(ATTR)) {
                if ("@*".equals(_xNodeName)) {
                    _isAsterix = true;
                    _nodeType = ASTERIX_ATTR_NODE;
                } else {
                    _nodeType = ATTR_NODE;
                    _xNodeName = _xNodeName.substring(1);
                }
            } else {
                checkNodeName(_xNodeName);
            }

            if ("*".equals(_xNodeName)) {
                _isAsterix = true;
                _nodeType = ASTERIX_NODE;
            }
        }
        setNodeExpression(extractNodeExpression(_xNodeName.length(), xNodeValue), check);
    }

    public String translateNSPrefix(Map<String, String> nsMappings) {
        String ns = null;
        if (nsMappings != null && _xNodeNSPfix != null) {
            ns = nsMappings.get(_xNodeNSPfix);
        }
        return ns;
    }

    /**
     * Check the given name for not supported characters like "(".
     *
     * @param name
     * @throws XPathError
     */
    private void checkNodeName(String name) throws XPathError {
        if (name.indexOf("(") >= 0) {
            throw ERROR_TO_COMPLEX;
        }
    }

    private void setNodeExpression(String exp, boolean check) throws XPathError {
        _xNodeExpression = exp;
        if (exp != null) {
            if ("list".equals(exp)) {
                _nodeType = LIST_NODE;
            } else if (NumberUtils.isCreatable(exp)) {
                _nodeType = IDX_NODE;
                _xNodeIDX = Integer.parseInt(exp);
            } else if (check) {
                throw ERROR_TO_COMPLEX;
            }
        }
    }

    public  void parse(String xNodeValue, boolean check) throws XPathError {
        parse(xNodeValue, check, null);
    }

    public boolean isList() {
        return _nodeType == LIST_NODE;
    }

    public boolean isAttr() {
        return _nodeType == ATTR_NODE;
    }

    public int getNodeIDX() {
        return _xNodeIDX;
    }

    public String getNodeNSpace() {
        return _xNodeNSpace;
    }

    public int getEndPos() {
        return _endPos;
    }

     void setEndPos(int pos) {
        _endPos = pos;
    }

    public XPathNode<U> getChild(String name) {
        if (_childs != null) {
            for (int i = 0; i < _childs.size(); i++) {
                XPathNode<U> node = _childs.get(i);
                if (node.getNodeName().equals(name)) {
                    return node;
                }
            }
        }
        return null;
    }

    public String getNodeName() {
        return _xNodeName;
    }

    public void setNodeName(String name) {
        _xNodeName = name;
    }

     void addChild(XPathNode<U> node) {
        if (_childs == null) {
            _childs = new ArrayList<>(5);
        }
        _childs.add(node);
    }

    public List<XPathNode<U>> getChilds() {
        return _childs;
    }

    public List<XPathNode<U>> getSameAxis() {
        return _sameAxis;
    }

    public void addSameAxis(XPathNode<U> node) {
        if (_sameAxis == null) {
            _sameAxis = new ArrayList<>(5);
        }
        _sameAxis.add(node);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(_xNodeName).append(_xNodeExpression).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        XPathNode<U> eObj = (XPathNode<U>) obj;
        return new EqualsBuilder().append(_xNodeName, eObj._xNodeName).append(_xNodeExpression, eObj._xNodeExpression).isEquals();
    }

    @Override
    public String toString() {
        return getNodeName() + " exp:[" + getNodeExpression() + "] ns:" + getNodeNSPrefix();
    }

    public String getNodeExpression() {
        return _xNodeExpression;
    }

    public String getNodeNSPrefix() {
        return _xNodeNSPfix;
    }

    public boolean isRoot() {
        return _isRoot;
    }

    public void setIsRoot(boolean isRoot) {
        _isRoot = isRoot;
    }

    public void print(StringBuffer output) {
        output.append(getNodeName());
        output.append("->");
        if (_childs != null) {
            for (int i = 0; i < _childs.size(); i++) {
                _childs.get(i).print(output);
            }
        }
    }

    public List<U> extractUserNodesAsSequence(List<U> sequence) {
        if (_childs != null) {
            // first extract indexed nodes
            for (int i = 0; i < _childs.size(); i++) {
                XPathNode<U> XPathNode = _childs.get(i);
                if (XPathNode.isIndexdNode()) {
                    XPathNode.extractUserNodesAsSequence(sequence);
                }
            }
        }
        if (getUserObject() != null) {
            sequence.add(getUserObject());
        }
        for (int i = 0; _sameAxis != null && i < _sameAxis.size(); i++) {
            XPathNode<U> sa = _sameAxis.get(i);
            if (sa.getUserObject() != null) {
                sequence.add(sa.getUserObject());
            }
        }
        if (_childs != null) {
            for (int i = 0; i < _childs.size(); i++) {
                XPathNode<U> xpNode = _childs.get(i);
                if (!xpNode.isIndexdNode()) {
                    xpNode.extractUserNodesAsSequence(sequence);
                }
            }
        }
        return sequence;
    }

    public void walk(XPathTreeWalker walker) {
        walker.walk(this);
        for (int i = 0; _sameAxis != null && i < _sameAxis.size(); i++) {
            XPathNode<U> sa = _sameAxis.get(i);
            sa.walk(walker);
        }
        if (_childs != null) {
            for (int i = 0; i < _childs.size(); i++) {
                _childs.get(i).walk(walker);
            }
        }
    }

    public U getUserObject() {
        return _userObject;
    }

    public void setUserObject(U object) {
        _userObject = object;
    }

    // public boolean isExpressionNode() {
    // return _nodeType == EXP_NODE;
    // }
    public boolean isIndexdNode() {
        return _nodeType == IDX_NODE;
    }

    public boolean isAsterix() {
        return _isAsterix;
    }

    public boolean isAll() {
        return _nodeType == ALL_NODE;
    }

    public boolean isSelf() {
        return _nodeType == SELF_NODE;
    }

    public boolean isParent() {
        return _nodeType == PARENT_NODE;
    }

    public boolean isText() {
        return _nodeType == TEXT_NODE;
    }

    public short getNodeType() {
        return _nodeType;
    }

    public void setNodeType(short nodeType) {
        _nodeType = nodeType;
    }

    public boolean isLast() {
        return _isLast;
    }

    public void setLast(boolean isLast) {
        _isLast = isLast;
    }
}
