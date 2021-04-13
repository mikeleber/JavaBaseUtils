package org.basetools.util.mesh.creator;

import org.basetools.util.mesh.Mesh;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.creator.JSONNodeCreator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class RelationalJSONNodeCreator implements JSONNodeCreator<RelationalTreeNode> {
    private Mesh _mesh;

    public RelationalJSONNodeCreator(Mesh mesh) {
        _mesh = mesh;
    }

    public RelationalTreeNode createNode(RelationalTreeNode current, JsonObject aDef) {

        RelationalTreeNode node = new RelationalTreeNode();
        if (aDef.containsKey("id")) {
            node.setId(aDef.getString("id"));
        }
        if (aDef.containsKey("name")) {
            node.setName(aDef.getString("name"));
        }
        if (aDef.containsKey("isSubgraph")) {
            node.setIsSubgraph(aDef.getBoolean("isSubgraph"));
        }
        if (aDef.containsKey("description")) {
            node.setDescription(aDef.getString("description"));
        }
        _mesh.addNode(current, node);
        return node;
    }

    @Override
    public boolean createNode(String key, RelationalTreeNode current, JsonValue value) {
        JsonValue.ValueType type = value.getValueType();

        if ("relation".equals(key)) {
            switch (type) {
                case OBJECT:
                    parseRelation(current, value.asJsonObject());
                    break;
                case ARRAY:
                    parseRelation(current, value.asJsonArray());
                    break;
            }
            return true;
        }
        return false;
    }

    protected void parseRelation(RelationalTreeNode current, JsonArray array) {
        Iterator<JsonValue> objects = array.iterator();
        while (objects.hasNext()) {
            parseRelation(current, objects.next().asJsonObject());
        }
    }

    private void parseRelation(RelationalTreeNode current, JsonObject aDef) {
        String id = null;
        String name = null;
        if (aDef.containsKey("id")) {
            id = aDef.getString("id");
        }
        NodeRelation relation = current.addRelation(Objects.toString(id, UUID.randomUUID().toString()), null);

        if (aDef.containsKey("name")) {
            relation.setName(aDef.getString("name"));
        }
        relation.setTargetNodeID(aDef.getString("target"));
    }
}
