package org.basetools.util.xml.diffing.preprocessor;

import org.basetools.util.xml.diffing.XmlDifferException;

public interface XMLPreProcessor {
    String process(String xml) throws XmlDifferException;
}
