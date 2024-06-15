package org.basetools.util.xml.diffing;

import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class XMLDifferences<T> {
    private Collection<XMLNodeDiff> diffList = new ArrayList<>();
    private T actualContent;
    private T expectedContent;

    private XMLDifferences(Builder builder) {
        diffList = builder.diffList;
        actualContent = (T) builder.actualContent;
        expectedContent = (T) builder.expectedContent;
    }

    public XMLDifferences(T actual, T expected) {
        actualContent = actual;
        expectedContent = expected;
    }

    public static Builder builder() {
        return new Builder();
    }

    public T getActualContent() {
        return actualContent;
    }

    public boolean isDifferent() {
        return getDifferences() != null && getDifferences().size() > 0;
    }

    public Collection<XMLNodeDiff> getDifferences() {
        return diffList;
    }

    public T getExpectedContent() {
        return expectedContent;
    }

    @Override
    public String toString() {
        return "XMLDifferences{" +
                "diffList=" + diffList +
                ", currentContent='" + actualContent + '\'' +
                ", testContent='" + expectedContent + '\'' +
                '}' + getDifferences().stream().map(Object::toString).collect(Collectors.joining("<br>"));
    }

    public void add(XMLNodeDiff diff) {
        if (!getDifferences().contains(diff)){

            getDifferences().add(diff);
        }else{
            //duplicate found, skip
        }
    }

    public long countErrors() {
        return getDifferences().stream().filter(diff -> diff.getSeverity() == XMLNodeDiff.Servity.ERROR).count();
    }

    public static final class Builder<T> {
        private Collection<XMLNodeDiff> diffList;
        private T actualContent;
        private T expectedContent;

        private Builder() {
        }

        public Builder withDifferences(Collection<XMLNodeDiff> val) {
            diffList = val;
            return this;
        }

        public Builder withActual(T val) {
            actualContent = val;
            return this;
        }

        public Builder withExpected(T val) {
            expectedContent = val;
            return this;
        }

        public XMLDifferences build() {
            return new XMLDifferences(this);
        }
    }
}
