package org.basetools.util.mesh.graphviz;

import org.basetools.util.StringUtils;
import org.basetools.util.mesh.Mesh;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.TreeNode;
import org.basetools.util.tree.TreeVisitor;

import java.util.Iterator;
import java.util.List;

public class GraphvizTreeVisitor implements TreeVisitor {
    private StringBuilder builder;

    public GraphvizTreeVisitor(StringBuilder builder) {
        this.builder = builder;
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree, String graphConfig) {
        StringBuilder builder = new StringBuilder();
        if (graphConfig == null) {
            builder.append("digraph G {" + Mesh.NEWLINE);
            builder.append("graph [fontsize=10 fontname=\"Verdana\" compound=true];" + Mesh.NEWLINE);
            builder.append("node [shape=record fontsize=10 fontname=\"Verdana\"];" + Mesh.NEWLINE);
        } else {
            builder.append(graphConfig + Mesh.NEWLINE);
        }
        ((List<RelationalTreeNode>) targetTree.getChildren()).stream().forEach((node) -> node.setIsSubgraph(true));
        targetTree.accept(new GraphvizTreeVisitor(builder));

        Iterator<NodeRelation> evalRelations = targetTree.findRelations().values().iterator();
        while (evalRelations.hasNext()) {
            NodeRelation relation = evalRelations.next();
            TreeNode toTopLevelNode = relation.getRelationTo();
            String toTopLevelNodeId = toTopLevelNode.getID();
            StringUtils.wrapQuoted(builder, relation.getRelationFrom().getID()).append("->");
            StringUtils.wrapQuoted(builder, toTopLevelNodeId);
            StringBuilder extension = new StringBuilder();
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(relation.getName())) {
                extension.append("label = " + StringUtils.wrapQuoted(relation.getName()));
            }

            if (relation.getRelationFrom().isSubgraph() || relation.getRelationFrom().hasChildren()) {
                extension.append(" ltail=cluster_" + relation.getRelationFrom().getID());
            }
            if (((RelationalTreeNode) toTopLevelNode).isSubgraph() || toTopLevelNode.hasChildren()) {
                extension.append(" lhead=cluster_" + toTopLevelNode.getID());
            }
            if (extension.length() > 0) {
                builder.append("[");
                builder.append(extension);
                builder.append("]");
            }
            builder.append(";");
        }
        builder.append("}" + Mesh.NEWLINE);
        return builder;
    }

    @Override
    public void visitStart(TreeNode aNode) {
        if (!aNode.isRoot()) {
            Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();
            if ((((RelationalTreeNode) aNode).isSubgraph() || aNode.hasChildren())) {
                builder.append("subgraph cluster_" + aNode.getID());
                builder.append("{" + Mesh.NEWLINE);
                builder.append("node [style=filled];" + Mesh.NEWLINE);
                builder.append("label = \"" + aNode.getID() + "\";" + Mesh.NEWLINE);
                builder.append("color=blue;" + Mesh.NEWLINE);
            } else {
                builder.append("\"" + aNode.getID() + "\" ");
                if (aNode.getNextSibling() == null) {
                    builder.append(";" + Mesh.NEWLINE);
                }
            }
        }
    }

    @Override
    public void visitEnd(TreeNode aNode) {
        if (!aNode.isRoot()) {
            if ((((RelationalTreeNode) aNode).isSubgraph() || aNode.hasChildren())) {
                builder.append("}" + Mesh.NEWLINE);
            }
        }
    }

    @Override
    public boolean doBreak(TreeNode aNode) {
        return false;
    }
}
