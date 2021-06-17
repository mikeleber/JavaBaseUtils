package org.basetools.util.xml.diffing;

import org.basetools.util.xml.diffing.preprocessor.XMLPreProcessor;

import java.util.Map;
import java.util.TreeMap;

public abstract class XmlDiffer {
    private final Map<Integer, XMLPreProcessor> preProcessors = new TreeMap<>();

    public abstract XMLDifferences diff(String expected, String actual) throws XmlDifferException;

    public void register(XMLPreProcessor XMLPreProcessor) {
        this.preProcessors.put(preProcessors.size() + 1, XMLPreProcessor);
    }

    public void clearPreProcessors() {
        this.preProcessors.clear();
    }

    protected String preProcess(String xml) throws XmlDifferException {
        String preProcessedXml = xml;
        for (XMLPreProcessor XMLPreProcessor : preProcessors.values()) {
            preProcessedXml = XMLPreProcessor.process(preProcessedXml);
        }
        return preProcessedXml;
    }
}
