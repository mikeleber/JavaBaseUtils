package org.basetools.util.mesh.cytoscape;

import org.apache.commons.lang3.StringUtils;
import org.basetools.util.json.JSONUtilities;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.TreeNode;
import org.basetools.util.tree.TreeVisitor;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.Iterator;

public class CytoscapeTreeVisitor implements TreeVisitor {
    private JsonArrayBuilder builder;
    private JsonArrayBuilder nodes;
    private JsonArrayBuilder edges;

    public CytoscapeTreeVisitor(JsonArrayBuilder root) {
        this.builder = root;
        // nodes = Json.createArrayBuilder();
        edges = nodes = root;//Json.createArrayBuilder();
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        targetTree.accept(new CytoscapeTreeVisitor(builder));

        return new StringBuilder(builder.build().toString());
    }

//    elements: {
//        nodes: [
//        { data: { id: 'j', name: 'Jerry', weight: 65, faveColor: '#6FB1FC', faveShape: 'triangle' } },
//        { data: { id: 'e', name: 'Elaine', weight: 45, faveColor: '#EDA1ED', faveShape: 'ellipse' } },
//        { data: { id: 'k', name: 'Kramer', weight: 75, faveColor: '#86B342', faveShape: 'octagon' } },
//        { data: { id: 'g', name: 'George', weight: 70, faveColor: '#F5A45D', faveShape: 'rectangle' } }
//    ],
//        edges: [
//        { data: { source: 'j', target: 'e', faveColor: '#6FB1FC', strength: 90 } },
//        { data: { source: 'j', target: 'e', faveColor: '#000000', strength: 120 } },
//        { data: { source: 'j', target: 'k', faveColor: '#6FB1FC', strength: 70 } },
//        { data: { source: 'j', target: 'g', faveColor: '#6FB1FC', strength: 80 } },
//
//        { data: { source: 'e', target: 'j', faveColor: '#EDA1ED', strength: 95 } },
//        { data: { source: 'e', target: 'k', faveColor: '#EDA1ED', strength: 60 }, classes: 'questionable' },
//
//        { data: { source: 'k', target: 'j', faveColor: '#86B342', strength: 100 } },
//        { data: { source: 'k', target: 'e', faveColor: '#86B342', strength: 100 } },
//        { data: { source: 'k', target: 'g', faveColor: '#86B342', strength: 100 } },
//
//        { data: { source: 'g', target: 'j', faveColor: '#F5A45D', strength: 90 } },
//        { data: { source: 'g', target: 'g', faveColor: '#F5A45D', strength: 90 } },
//        { data: { source: 'g', target: 'g', faveColor: '#F5A45D', strength: 90 } },
//        { data: { source: 'g', target: 'g', faveColor: '#F5A45D', strength: 90 } }
//    ]
//    }

    //    elements: [
//    // nodes
//    { data: { id: 'a' } },
//    { data: { id: 'b' } },
//    { data: { id: 'c' } },
//    { data: { id: 'd' } },
//    { data: { id: 'e' } },
//    { data: { id: 'f' } },
//    // edges
//    {
//        data: {
//            id: 'ab',
//                    source: 'a',
//                    target: 'b'
//        }
//    },
    @Override
    public void visitStart(TreeNode aNode) {
        Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();

        JsonObjectBuilder cytoscapeNode = Json.createObjectBuilder();
        JsonObjectBuilder nodeData = Json.createObjectBuilder();
        nodeData.add("id", aNode.getID());
        if (aNode.getName() != null) {
            nodeData.add("name", aNode.getName());
        }
        if (aNode.getParent() != null && !aNode.getParent().isRoot()) {
            nodeData.add("parent", aNode.getParent().getID());
        }
        nodeData.add("faveShape", "rectangle");
        // nodeData.add("faveColor", "rectangle");
        nodeData.add("weight", ((RelationalTreeNode) aNode).getRelations().size());
        cytoscapeNode.add("data", nodeData);
        JSONUtilities.add(nodes, cytoscapeNode);

        while (relations.hasNext()) {
            JsonObjectBuilder singleEdge = Json.createObjectBuilder();
            JsonObjectBuilder singleEdgeData = Json.createObjectBuilder();
            NodeRelation relation = relations.next();
            String fromTopLevelNodeId = relation.getRelationFrom().getID();
            TreeNode toTopLevelNode = relation.getRelationTo();
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();

            singleEdgeData.add("source", fromTopLevelNodeId);
            singleEdgeData.add("target", toTopLevelNodeId);
            //   singleEdgeData.add("strength", 1);
            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
                singleEdgeData.add("label", relationName);
            }
            singleEdge.add("data", singleEdgeData);
            nodes.add(singleEdge);
        }
    }

    @Override
    public void visitEnd(TreeNode aNode) {
//        if (aNode.isRoot()) {
//            JSONHelper.add(builder, "data", nodes);
//            // JSONHelper.add(builder, "edges", edges);
//        }
    }

    @Override
    public boolean doBreak(TreeNode aNode) {
        return false;
    }
}
