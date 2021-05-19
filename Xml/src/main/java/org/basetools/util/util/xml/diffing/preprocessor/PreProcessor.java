package org.basetools.util.util.xml.diffing.preprocessor;

import org.basetools.util.util.xml.diffing.XmlDifferException;

public interface PreProcessor {
    String process(String xml) throws XmlDifferException;
}
