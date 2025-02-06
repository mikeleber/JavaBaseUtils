package org.basetools.util.mesh.mermaid;

import org.apache.commons.lang3.StringUtils;
import org.basetools.util.mesh.Mesh;
import org.basetools.util.mesh.NodeRelation;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.tree.TreeNode;
import org.basetools.util.tree.TreeVisitor;

import java.util.Iterator;
import java.util.List;

public class MermaidSubsystemTreeVisitor implements TreeVisitor {
    private StringBuilder builder;
    private StringBuilder globalRelationBuilder = new StringBuilder();

    public MermaidSubsystemTreeVisitor(StringBuilder builder) {
        this.builder = builder;
    }

    public static StringBuilder toGraph(int level, RelationalTreeNode targetTree) {
        StringBuilder builder = new StringBuilder();
        builder.append("graph TB" + Mesh.NEWLINE);

        ((List<RelationalTreeNode>) targetTree.getChildren()).stream().forEach((node) -> node.setIsSubgraph(true));
        MermaidSubsystemTreeVisitor visitor = new MermaidSubsystemTreeVisitor(builder);
        targetTree.accept(visitor);
        builder.append(visitor.globalRelationBuilder);
        return builder;
    }

    private static boolean drawSubgraph(TreeNode aNode) {
        return ((RelationalTreeNode) aNode).isSubgraph() || (aNode.getParent() != null && ((RelationalTreeNode) aNode.getParent()).isSubgraph());
    }

    @Override
    public void visitStart(TreeNode aNode) {
        System.out.println("visit " + aNode.getName());
        Iterator<NodeRelation> relations = ((RelationalTreeNode) aNode).getRelations().values().iterator();
        if (drawSubgraph(aNode)) {
            builder.append(" subgraph " + aNode.getID() + " [" + aNode.getID() + "]" + Mesh.NEWLINE);
            //do not use id twice. subgraph is like a node and need a unique name
            //if (!relations.hasNext() && !aNode.hasChildren()) {
            //  builder.append(aNode.getID() + Mesh.NEWLINE);
            //}
        }
        while (relations.hasNext()) {
            NodeRelation relation = relations.next();

//            if (drawSubgraph(aNode)) {
//                if (relation.isTargetingOutsideOrSelf(aNode)) {
//                    appendRelation(relation, globalRelationBuilder);
//                } else {
//                    appendRelation(relation, globalRelationBuilder);
//                }
//            } else {
            appendRelation(relation, globalRelationBuilder);
//            }
        }
    }

    private void appendRelation(NodeRelation relation, StringBuilder builder) {
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

    @Override
    public void visitEnd(TreeNode aNode) {
        if (drawSubgraph(aNode)) {
            builder.append("end" + Mesh.NEWLINE);
        }
    }

    @Override
    public boolean doBreak(TreeNode aNode) {
        return false;
    }
}
