package org.basetools.util.tree.xpath;

import org.basetools.util.tree.Tree;
import org.junit.jupiter.api.Test;

import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeNodeXPathExecuterImplTest {

    @Test
    void processXPathJaxen() throws TransformerException {
        Tree tree = new Tree<>();
        tree.addPath("/mein/name/ist/hase", false, null);
        tree.addPath("/mein/name/war/hase", false, null);
        tree.addPath("/mein/name/war/oder", false, null);
        assertEquals("mein", tree.getRoot().getFirstChild().getID());

        assertEquals(7, TreeNodeXPathExecuterImpl.getInstance().processXPathJaxen("//*", tree.getRoot()).size());
        assertEquals(2, TreeNodeXPathExecuterImpl.getInstance().processXPathJaxen("//hase", tree.getRoot()).size());
        assertEquals(1, TreeNodeXPathExecuterImpl.getInstance().processXPathJaxen("/*/name", tree.getRoot()).size());
        assertEquals(1, TreeNodeXPathExecuterImpl.getInstance().processXPathJaxen("/mein/name", tree.getRoot()).size());
    }
}
