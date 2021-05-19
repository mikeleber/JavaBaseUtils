package org.basetools.util.xml.diffing.preprocessor;

import org.basetools.util.xml.diffing.XmlDifferException;

public interface PreProcessor {
    String process(String xml) throws XmlDifferException;
}
