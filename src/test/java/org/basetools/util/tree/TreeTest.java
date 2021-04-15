package org.basetools.util.tree;

import org.junit.jupiter.api.Test;
import org.modelui.util.json.JSONUtilities;

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
    void givenTree_whenSerialized_thenGetvalidJSON() {
        Tree tree = new Tree();
        tree.addPath("/mein/name/ist/hase", false, null);
        tree.addPath("/mein/name/war/hase", false, null);
        tree.addPath("/mein/name/war/oder", false, null);
        JSONSerializationTreeNodeRenderer renderer = new JSONSerializationTreeNodeRenderer();
        tree.getRoot().render(renderer);
        JsonObject current = renderer.build();
        JsonObject expected = JSONUtilities.createJson(current.toString());
        assertEquals(expected, current);
    }
}
