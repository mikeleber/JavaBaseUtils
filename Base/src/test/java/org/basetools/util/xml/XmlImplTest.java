package org.basetools.util.xml;

import org.basetools.util.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;

class XmlImplTest {

    @Test
    void children() {
        Xml aXml = new Xml("name");
        aXml.addChild(new Xml("child"));
        aXml.addChild(new Xml("child2"));
        aXml.addChild(new Xml("child3"));
        aXml.children("child").stream().map(aChild -> aChild.getFirstChildName());
    }
    @Test
    void completXsd() throws IOException {
       Xml xsd=  Xml.from(FileUtils.readAsStringFromClass(getClass(),"./xui.xsd","utf-8"),"schema");
        System.out.println(xsd.toXML());
    }
}