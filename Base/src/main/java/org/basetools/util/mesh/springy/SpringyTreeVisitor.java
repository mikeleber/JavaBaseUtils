package org.basetools.util.mesh.springy;

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

public class SpringyTreeVisitor implements TreeVisitor {
    private JsonObjectBuilder builder;
    private JsonArrayBuilder nodes;
    private JsonArrayBuilder edges;

    public SpringyTreeVisitor(JsonObjectBuilder root) {
        this.builder = root;
        nodes = Json.createArrayBuilder();
        edges = Json.createArrayBuilder();
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        targetTree.accept(new SpringyTreeVisitor(builder));

        return new StringBuilder(builder.build().toString());
    }

    @Override
    public void visitStart(TreeNode aNode) {
        if (aNode.isRoot()) {
            return;
        }
        Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();
        JSONUtilities.add(nodes, aNode.getID());
        while (relations.hasNext()) {
            JsonArrayBuilder singleEdge = Json.createArrayBuilder();
            NodeRelation relation = relations.next();
            String fromTopLevelNodeId = relation.getRelationFrom().getID();
            TreeNode toTopLevelNode = relation.getRelationTo();
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();

            JSONUtilities.add(singleEdge, fromTopLevelNodeId);
            JSONUtilities.add(singleEdge, toTopLevelNodeId);
            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
                JsonObjectBuilder labelBuilder = Json.createObjectBuilder();
                labelBuilder.add("label", relationName);
                JSONUtilities.add(singleEdge, labelBuilder);
            }
            edges.add(singleEdge);
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
