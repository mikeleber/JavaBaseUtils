package org.basetools.util.tree;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeTest {

    @Test
    void whenCallAddPath_withGivenXPaths_ThenCreateTree() {
        Tree tree = new Tree();
        tree.addPath("/mein/name/ist/hase", null);
        tree.addPath("/mein/name/war/hase", null);
        tree.addPath("/mein/name/war/oder", null);
        assertEquals("mein", tree.getRoot().getFirstChild().getID());
    }

    @Test
    void givenTree_whenSerialized_thenGetvalidJSON() throws ParseException {
        Tree tree = new Tree();
        tree.addPath("/mein/name/ist/hase", null);
        tree.addPath("/mein/name/war/hase", null);
        tree.addPath("/mein/name/war/oder", null);
        JSONSerializationTreeNodeRenderer renderer = new JSONSerializationTreeNodeRenderer();
        tree.getRoot().render(renderer);
        JSONObject current = renderer.build();
        JSONObject expected = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(current.toString());
        assertEquals(expected, current);
    }
}
