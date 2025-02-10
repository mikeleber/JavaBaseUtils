package org.basetools.util.xml;

import org.junit.jupiter.api.Test;

class XmlImplTest {

    @Test
    void children() {
        Xml aXml = new Xml("name");
        aXml.addChild(new Xml("child"));
        aXml.addChild(new Xml("child2"));
        aXml.addChild(new Xml("child3"));
        aXml.children("child").stream().map(aChild -> aChild.getFirstChildName());
    }
}