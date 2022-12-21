package org.basetools.util.mesh.sigma;

import org.basetools.util.json.JSONUtilities;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.TreeNode;
import org.basetools.util.tree.TreeVisitor;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
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
    private JsonObjectBuilder builder;
    private JsonArrayBuilder nodes;
    private JsonArrayBuilder edges;

    public SigmaTreeVisitor(JsonObjectBuilder root) {
        this.builder = root;
        nodes = Json.createArrayBuilder();
        edges = Json.createArrayBuilder();
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        targetTree.accept(new SigmaTreeVisitor(builder));

        return new StringBuilder(builder.build().toString());
    }

    @Override
    public void visitStart(TreeNode aNode) {
        if (aNode.isRoot()) {
            return;
        }
        Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();
        JsonObjectBuilder node = Json.createObjectBuilder()
                .add("x", (int) (Math.random() * 1000))
                .add("y", (int) (Math.random() * 1000))
                .add("label", aNode.getID())
                .add("id", aNode.getID());
        JSONUtilities.add(nodes, node);

        while (relations.hasNext()) {
            JsonArrayBuilder singleEdge = Json.createArrayBuilder();
            NodeRelation relation = relations.next();
            String fromTopLevelNodeId = relation.getRelationFrom().getID();
            TreeNode toTopLevelNode = relation.getRelationTo();
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();

            JsonObjectBuilder edgeNode = Json.createObjectBuilder()
                    .add("id", relationName)
                    .add("source", fromTopLevelNodeId)
                    .add("target", toTopLevelNodeId)
                    .add("type", "curve");

//            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
//                JsonObjectBuilder labelBuilder = Json.createObjectBuilder();
//                labelBuilder.add("label", relationName);
//                JSONUtilities.add(singleEdge, labelBuilder);
//            }
            edges.add(edgeNode);
        }
    }

    @Override
    public void visitEnd(TreeNode aNode) {
        if (aNode.isRoot()) {
            JSONUtilities.add(builder, "nodes", nodes);
            JSONUtilities.add(builder, "edges", edges);
        }
    }

    @Override
    public boolean doBreak(TreeNode aNode) {
        return false;
    }
}
