package org.basetools.util.tree;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.basetools.util.json.JSONUtilities;
import org.junit.jupiter.api.Test;

import javax.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeTest {

    @Test
    void whenCallAddPath_withGivenXPaths_ThenCreateTree() {
        Tree tree = new Tree();
        tree.addPath("/mein/name/ist/hase", false, null);
        tree.addPath("/mein/name/war/hase", false, null);
        tree.addPath("/mein/name/war/oder", false, null);
        assertEquals("mein", tree.getRoot().getFirstChild().getID());
    }

    @Test
    void givenTree_whenSerialized_thenGetvalidJSON() throws ParseException {
        Tree tree = new Tree();
        tree.addPath("/mein/name/ist/hase", false, null);
        tree.addPath("/mein/name/war/hase", false, null);
        tree.addPath("/mein/name/war/oder", false, null);
        JSONSerializationTreeNodeRenderer renderer = new JSONSerializationTreeNodeRenderer();
        tree.getRoot().render(renderer);
        JSONObject current = renderer.build();
        JSONObject expected = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(current.toString());
        assertEquals(expected, current);
    }
}
