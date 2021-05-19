package org.basetools.util.util.xml.diffing;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.util.Objects;

public class XMLNodeDiff {
    private Object currentValue;
    private Object expectedValue;
    private Node actualNode;
    private Node expectNode;
    private DiffType diffType;
    private String xpath;

    private XMLNodeDiff() {

    }

    public static final String cut(String value, int length, String cutTail) {

        if (value == null || value.length() < length) {
            return value;
        } else {
            return value.substring(0, length) + (cutTail != null ? cutTail : "");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    @Override
    public String toString() {
        return "<" + diffType +
                "> " + toDisplayValue(currentValue != null ? currentValue : actualNode) +
                " -> expected=" + toDisplayValue(expectedValue != null ? expectedValue : expectNode) +
                ", " + xpath + "\n";
    }

    public final String toDisplayValue(Object value) {
        if (value instanceof Node) {
            switch (((Node) value).getNodeType()) {
                case Node.TEXT_NODE:
                    value = ((Node) value).getNodeValue();
                    break;
                default:
                    value = ((Node) value).getNodeName();
                    break;
            }
        }
        String stringValue = Objects.toString(value, "");
        return cut(StringUtils.normalizeSpace(stringValue), 30, "...");
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public Node getActualNode() {
        return actualNode;
    }

    public Node getExpectNode() {
        return expectNode;
    }

    public DiffType getDiffType() {
        return diffType;
    }

    public String getXpath() {
        return xpath;
    }

    public enum DiffType {
        VALUE_CHANGED, NODE_ADDED, NODE_TYPE, NODE_REMOVED, CHILD_COUNT, CHILD_REMOVED, CHILD_ADDED
    }

    public static final class Builder {
        private Object actualValue;
        private Object expectedValue;
        private Node actualNode;
        private Node expectedNode;
        private DiffType diffType;
        private String xpath;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withActualValue(Object value) {
            this.actualValue = value;
            return this;
        }

        public Builder withExpectedValue(Object value) {
            this.expectedValue = value;
            return this;
        }

        public Builder withActualNode(Node node) {
            this.actualNode = node;
            return this;
        }

        public Builder withExpectedNode(Node node) {
            this.expectedNode = node;
            return this;
        }

        public Builder withDiffType(DiffType diffType) {
            this.diffType = diffType;
            return this;
        }

        public Builder withXpath(String xpath) {
            this.xpath = xpath;
            return this;
        }

        public XMLNodeDiff build() {
            XMLNodeDiff diff = new XMLNodeDiff();
            diff.expectedValue = this.expectedValue;
            diff.xpath = this.xpath;
            diff.diffType = this.diffType;
            diff.currentValue = this.actualValue;
            diff.expectNode = this.expectedNode;
            diff.actualNode = this.actualNode;
            return diff;
        }
    }
}
