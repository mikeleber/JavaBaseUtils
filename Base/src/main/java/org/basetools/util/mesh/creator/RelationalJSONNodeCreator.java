package org.basetools.util.mesh.creator;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import org.basetools.util.json.JSONSmartUtil;
import org.basetools.util.mesh.Mesh;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.creator.JSONNodeCreator;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class RelationalJSONNodeCreator implements JSONNodeCreator<RelationalTreeNode> {
    private Mesh _mesh;

    public RelationalJSONNodeCreator(Mesh mesh) {
        _mesh = mesh;
    }

    public RelationalTreeNode createNode(RelationalTreeNode current, JSONAware def) {
        if (def instanceof JSONObject) {
            JSONObject aDef = (JSONObject) def;

            RelationalTreeNode node = new RelationalTreeNode();
            if (aDef.containsKey("id")) {
                node.setId(aDef.getAsString("id"));
            }
            if (aDef.containsKey("name")) {
                node.setName(aDef.getAsString("name"));
            }
            if (aDef.containsKey("isSubgraph")) {
                node.setIsSubgraph(JSONSmartUtil.isTrue(aDef.get("isSubgraph"), false));
            }
            if (aDef.containsKey("description")) {
                node.setDescription(aDef.getAsString("description"));
            }
            _mesh.addNode(current, node);
            return node;
        } else return null;
    }

    @Override
    public boolean createNode(String key, RelationalTreeNode current, JSONAware value) {


        if ("relation".equals(key)) {
            if (value instanceof JSONObject)
                parseRelation(current, (JSONObject) value);

            else if (value instanceof JSONArray)
                parseRelation(current, (JSONArray) value);
            return true;
        }
        return false;
    }

    protected void parseRelation(RelationalTreeNode current, JSONArray array) {
        Iterator<Object> objects = array.iterator();
        while (objects.hasNext()) {
            Object o = objects.next();
            if (o instanceof JSONObject) {
                parseRelation(current, (JSONObject) o);
            } else if (o instanceof JSONArray) {
                parseRelation(current, (JSONArray) o);
            } else {
                System.out.println("value reached:" + o);
            }

        }
    }

    private void parseRelation(RelationalTreeNode current, JSONObject aDef) {
        String id = null;
        String targetNodeID = aDef.getAsString("target");
        String currentNodeID = current.getID();
        String name = null;
        if (aDef.containsKey("id")) {
            id = aDef.getAsString("id");
        }
        String relationID = Objects.toString(currentNodeID + "_" + id + "_" + targetNodeID, UUID.randomUUID().toString());
        NodeRelation relation = current.addRelation(relationID, null);

        if (aDef.containsKey("name")) {
            relation.setName(aDef.getAsString("name"));
        }
        relation.setTargetNodeID(targetNodeID);
    }
}
