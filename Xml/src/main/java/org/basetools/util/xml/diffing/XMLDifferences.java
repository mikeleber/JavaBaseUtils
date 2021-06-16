package org.basetools.util.xml.diffing;

import java.util.Collection;

public class XMLDifferences {
    private final Collection<XMLNodeDiff> diffList;
    private final String actualContent;
    private final String expectedContent;

    private XMLDifferences(Builder builder) {
        diffList = builder.diffList;
        actualContent = builder.actualContent;
        expectedContent = builder.expectedContent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<XMLNodeDiff> getDifferences() {
        return diffList;
    }

    public String getActualContent() {
        return actualContent;
    }

    public boolean isDifferent() {
        return getDifferences() != null && getDifferences().size() > 0;
    }

    public String getExpectedContent() {
        return expectedContent;
    }


    @Override
    public String toString() {
        return "XMLDifferences{" +
                "diffList=" + diffList +
                ", currentContent='" + actualContent + '\'' +
                ", testContent='" + expectedContent + '\'' +
                '}';
    }

    public static final class Builder {
        private Collection<XMLNodeDiff> diffList;
        private String actualContent;
        private String expectedContent;

        private Builder() {
        }

        public Builder withDifferences(Collection<XMLNodeDiff> val) {
            diffList = val;
            return this;
        }


        public Builder withActual(String val) {
            actualContent = val;
            return this;
        }

        public Builder withExpected(String val) {
            expectedContent = val;
            return this;
        }

        public XMLDifferences build() {
            return new XMLDifferences(this);
        }
    }
}
