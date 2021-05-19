package org.basetools.util.xml.diffing;

import org.basetools.util.xml.diffing.preprocessor.PreProcessor;

import java.util.Map;
import java.util.TreeMap;

/**
 * Custom xml differ which compares two xml's in order to identify the differences
 * We also evaluated XmlUnit for this task, but resulting diffing output wasn't usable, since it included primarily false findings.
 */
public abstract class XmlDiffer {
    private final Map<Integer, PreProcessor> preProcessors = new TreeMap<>();

    public abstract XMLDifferences diff(String expected, String actual) throws XmlDifferException;

    /**
     * Registers the given {@link PreProcessor}.
     * It will be executed for each xml before the diffing.
     */
    public void register(PreProcessor preProcessor) {
        this.preProcessors.put(preProcessors.size() + 1, preProcessor);
    }

    /**
     * Removes all registered {@link PreProcessor}
     */
    public void clearPreProcessors() {
        this.preProcessors.clear();
    }

    protected String preProcess(String xml) throws XmlDifferException {
        String preProcessedXml = xml;
        for (PreProcessor preProcessor : preProcessors.values()) {
            preProcessedXml = preProcessor.process(preProcessedXml);
        }
        return preProcessedXml;
    }
}
