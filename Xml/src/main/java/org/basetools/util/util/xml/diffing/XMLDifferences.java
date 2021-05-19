package org.basetools.util.util.xml.diffing;

import java.util.Collection;

public class XMLDifferences {
    private final Collection<XMLNodeDiff> diffList;
    private final String uuid;
    private final String currentContent;
    private final String testContent;

    private XMLDifferences(Builder builder) {
        diffList = builder.diffList;
        uuid = builder.uuid;
        currentContent = builder.currentContent;
        testContent = builder.testContent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<XMLNodeDiff> getDifferences() {
        return diffList;
    }

    public String getCurrentContent() {
        return currentContent;
    }

    public boolean isDifferent() {
        return getDifferences() != null && getDifferences().size() > 0;
    }

    public String getTestContent() {
        return testContent;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "XMLDifferences{" +
                "diffList=" + diffList +
                ", uuid='" + uuid + '\'' +
                ", currentContent='" + currentContent + '\'' +
                ", testContent='" + testContent + '\'' +
                '}';
    }

    public static final class Builder {
        private Collection<XMLNodeDiff> diffList;
        private String uuid;
        private String currentContent;
        private String testContent;

        private Builder() {
        }

        public Builder withDifferences(Collection<XMLNodeDiff> val) {
            diffList = val;
            return this;
        }

        public Builder withUuid(String val) {
            uuid = val;
            return this;
        }

        public Builder withActual(String val) {
            currentContent = val;
            return this;
        }

        public Builder withExpected(String val) {
            testContent = val;
            return this;
        }

        public XMLDifferences build() {
            return new XMLDifferences(this);
        }
    }
}
