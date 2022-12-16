package org.basetools.util.mesh;

import org.basetools.util.tree.TreeNode;

public class NodeRelation<T, U> {
    private RelationalTreeNode<T, U> toRelation;
    private String id, targetNodeID;
    private String name;
    private RelationalTreeNode<T, U> fromRelation;

    public NodeRelation(String realtionID, RelationalTreeNode<T, U> from, RelationalTreeNode<T, U> to) {
        toRelation = to;
        fromRelation = from;
        id = realtionID;
    }

    public String getId() {
        return id;
    }

    public RelationalTreeNode<T, U> getRelationFrom() {
        return fromRelation;
    }

    public String getTargetNodeID() {
        return targetNodeID;
    }

    public NodeRelation<T, U> setTargetNodeID(String id) {
        targetNodeID = id;
        return this;
    }

    public RelationalTreeNode<T, U> getRelationTo() {
        return toRelation;
    }

    public String getName() {
        return name;
    }

    public NodeRelation<T, U> setName(String name) {
        this.name = name;
        return this;
    }

    public NodeRelation<T, U> setToRelation(RelationalTreeNode<T, U> toRelation) {
        this.toRelation = toRelation;
        return this;
    }

    public boolean isTargetingOutsideOrSelf(TreeNode aNode) {
        return aNode == fromRelation || aNode.isChild(fromRelation, true) || aNode.isChild(toRelation, true);
    }
}
