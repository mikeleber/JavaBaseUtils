package org.basetools.util.mesh.springy;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.TreeNode;
import org.basetools.util.tree.TreeVisitor;

import java.util.Iterator;

public class SpringyTreeVisitor implements TreeVisitor {
    private JSONObject builder;
    private JSONArray nodes;
    private JSONArray edges;

    public SpringyTreeVisitor(JSONObject root) {
        this.builder = root;
        nodes = new JSONArray();
        edges = new JSONArray();
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree) {
        JSONObject builder = new JSONObject();
        targetTree.accept(new SpringyTreeVisitor(builder));

        return new StringBuilder(builder.toJSONString());
    }

    @Override
    public void visitStart(TreeNode aNode) {
        if (aNode.isRoot()) {
            return;
        }
        Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();
        nodes.add(aNode.getID());
        while (relations.hasNext()) {
            JSONArray singleEdge = new JSONArray();
            NodeRelation relation = relations.next();
            String fromTopLevelNodeId = relation.getRelationFrom().getID();
            TreeNode toTopLevelNode = relation.getRelationTo();
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();

            singleEdge.add(fromTopLevelNodeId);
            singleEdge.add(toTopLevelNodeId);
            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
                JSONObject labelBuilder = new JSONObject();
                labelBuilder.put("label", relationName);
                singleEdge.add(labelBuilder);
            }
            edges.add(singleEdge);
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
