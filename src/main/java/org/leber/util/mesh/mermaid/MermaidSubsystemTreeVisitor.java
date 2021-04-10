package org.leber.util.mesh.mermaid;

import org.apache.commons.lang3.StringUtils;
import org.leber.util.mesh.Mesh;
import org.leber.util.mesh.NodeRelation;
import org.leber.util.mesh.RelationalTreeNode;
import org.leber.util.tree.TreeNode;
import org.leber.util.tree.TreeVisitor;

import java.util.Iterator;
import java.util.List;

public class MermaidSubsystemTreeVisitor implements TreeVisitor {
    private StringBuilder builder;

    public MermaidSubsystemTreeVisitor(StringBuilder builder) {
        this.builder = builder;
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree) {
        StringBuilder builder = new StringBuilder();
        builder.append("graph TB" + Mesh.NEWLINE);

        ((List<RelationalTreeNode>) targetTree.getChildren()).stream().forEach((node) -> node.setIsSubgraph(true));
        targetTree.accept(new MermaidSubsystemTreeVisitor(builder));
        return builder;
    }

    @Override
    public void visitStart(TreeNode aNode) {
        Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();
        if (((RelationalTreeNode) aNode).isSubgraph()) {
            builder.append(" subgraph <" + aNode.getID() + ">" + Mesh.NEWLINE);
            if (!relations.hasNext() && !aNode.hasChildren()) {
                builder.append(aNode.getID() + Mesh.NEWLINE);
            }
        }
        while (relations.hasNext()) {
            NodeRelation relation = relations.next();
            String fromTopLevelNodeId = relation.getRelationFrom().getID();
            builder.append(fromTopLevelNodeId + "-->");
            TreeNode toTopLevelNode = relation.getRelationTo();
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();
            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
                builder.append("|" + relation.getName() + "|");
            }
            builder.append(toTopLevelNodeId + Mesh.NEWLINE);
        }
    }

    @Override
    public void visitEnd(TreeNode aNode) {
        if (((RelationalTreeNode) aNode).isSubgraph()) {
            builder.append("end" + Mesh.NEWLINE);
        }
    }

    @Override
    public boolean doBreak(TreeNode aNode) {
        return false;
    }
}
