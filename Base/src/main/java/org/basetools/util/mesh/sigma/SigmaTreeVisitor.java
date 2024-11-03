package org.basetools.util.mesh.sigma;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.TreeNode;
import org.basetools.util.tree.TreeVisitor;

import java.util.Iterator;

public class SigmaTreeVisitor implements TreeVisitor {
    //    var graph = {
//            nodes: [
//    { id: "n0", label: "A node", x: 0, y: 0, size: 3, color: '#008cc2' },
//    { id: "n1", label: "Another node", x: 3, y: 1, size: 2, color: '#008cc2' },
//    { id: "n2", label: "And a last one", x: 1, y: 3, size: 1, color: '#E57821' }
//  ],
//    edges: [
//    { id: "e0", source: "n0", target: "n1", color: '#282c34', type:'line', size:0.5 },
//    { id: "e1", source: "n1", target: "n2", color: '#282c34', type:'curve', size:1},
//    { id: "e2", source: "n2", target: "n0", color: '#FF0000', type:'line', size:2}
//  ]
//}
    private JSONObject builder;
    private JSONArray nodes;
    private JSONArray edges;

    public SigmaTreeVisitor(JSONObject root) {
        this.builder = root;
        nodes = new JSONArray();
        edges = new JSONArray();
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree) {
        JSONObject builder = new JSONObject();
        targetTree.accept(new SigmaTreeVisitor(builder));

        return new StringBuilder(builder.toJSONString());
    }

    @Override
    public void visitStart(TreeNode aNode) {
        if (aNode.isRoot()) {
            return;
        }
        Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();
        JSONObject node = new JSONObject()
                .appendField("x", (int) (Math.random() * 1000))
                .appendField("y", (int) (Math.random() * 1000))
                .appendField("label", aNode.getID())
                .appendField("id", aNode.getID());
        nodes.add(node);

        while (relations.hasNext()) {
            JSONArray singleEdge = new JSONArray();
            NodeRelation relation = relations.next();
            String fromTopLevelNodeId = relation.getRelationFrom().getID();
            TreeNode toTopLevelNode = relation.getRelationTo();
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();

            JSONObject edgeNode = new JSONObject()
                    .appendField("id", relationName)
                    .appendField("source", fromTopLevelNodeId)
                    .appendField("target", toTopLevelNodeId)
                    .appendField("type", "curve");

//            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
//                JsonObjectBuilder labelBuilder = new JSONObject();
//                labelBuilder.add("label", relationName);
//                JSONUtilities.add(singleEdge, labelBuilder);
//            }
            edges.add(edgeNode);
        }
    }

    @Override
    public void visitEnd(TreeNode aNode) {
        if (aNode.isRoot()) {
            builder.put("nodes", nodes);
            builder.put("edges", edges);
        }
    }

    @Override
    public boolean doBreak(TreeNode aNode) {
        return false;
    }
}
