package org.leber.util.mesh;

import org.leber.util.tree.TreeNode;
import org.leber.util.tree.TreeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationalTreeNode<T, U> extends TreeNode<T, U> {
    protected boolean isSubgraph;
    private Map<String, NodeRelation> relations;
    private Mesh mesh;

    public RelationalTreeNode() {
        super();
    }

    public RelationalTreeNode(String id, T data) {
        super(id, data);
    }

    public static RelationalTreeNode of(RelationalTreeNode templateNode) {
        RelationalTreeNode newNode = new RelationalTreeNode<>();
        newNode.mesh = templateNode.mesh;
        newNode.relations = templateNode.relations;
        newNode.data = templateNode.data;
        newNode.userobject = templateNode.userobject;
        newNode._info = templateNode._info;
        newNode._properties = templateNode._properties;
        newNode._id = templateNode._id;
        newNode._description = templateNode._description;
        newNode._name = templateNode._name;
        newNode._isList = templateNode._isList;
        newNode.isSubgraph = templateNode.isSubgraph;
        return newNode;
    }

    public Map<String, NodeRelation> getRelations() {
        if (relations == null) {
            relations = new HashMap<>();
        }
        return relations;
    }

    public Map<String, NodeRelation> findRelations() {
        Map<String, NodeRelation> relations = new HashMap<>();
        findRelations(relations);
        return relations;
    }

    public List<TreeNode<T, U>> getNodesAtLevel(int level) throws IndexOutOfBoundsException {
        List<TreeNode<T, U>> result = new ArrayList<>();
        getNodesAtLevel(level, result);
        return result;
    }

    public Map<String, NodeRelation> findRelations(Map<String, NodeRelation> relations) {
        if (hasRelations()) {
            relations.putAll(getRelations());
        }
        for (TreeNode node : getChildren()) {
            if (node instanceof RelationalTreeNode) {
                ((RelationalTreeNode) node).findRelations(relations);
            }
        }
        return relations;
    }

    public boolean hasRelations() {
        return relations != null && relations.size() > 0;
    }

    public NodeRelation addRelation(String realtionID, RelationalTreeNode<T, U> relationNode) {
        NodeRelation relation = new NodeRelation(realtionID, this, relationNode);
        getRelations().put(realtionID, relation);
        return relation;
    }

    public void removeRelation(String realtionID) {
        getRelations().remove(realtionID);
    }

    public boolean hasDirectRelation(RelationalTreeNode<T, U> with) {
        boolean hasDirectRelation = getRelations().containsValue(with);
        return hasDirectRelation;
    }

    public List<String> getParentPaths(String relationID) {
        TreeNode current = null;
        List<String> ids = new ArrayList<>();
        if (getRelations().containsKey(relationID)) {
            while ((current = getParent()) != null) {
                ids.add(current.getID());
            }
        }
        return ids;
    }

    @Override
    public void accept(TreeVisitor<T, U> visitor) {
        super.accept(visitor);
    }

    public void initialize() {
        super.accept(new TreeVisitor<T, U>() {
            @Override
            public void visitStart(TreeNode<T, U> aNode) {
                ((RelationalTreeNode) aNode).initializeRelations();
            }

            @Override
            public void visitEnd(TreeNode<T, U> aNode) {

            }

            @Override
            public boolean doBreak(TreeNode<T, U> aNode) {
                return false;
            }
        });
    }

    private void initializeRelations() {
        if (hasRelations()) {
            for (NodeRelation relation : getRelations().values()) {
                RelationalTreeNode targetNode = getMesh().find(relation.getTargetNodeID());
                if (targetNode == null) {
                    System.out.println("targetnode not found: " + relation.getTargetNodeID());
                } else {
                    relation.setToRelation(targetNode);
                }
            }
        }
    }

    public Mesh getMesh() {
        if (mesh != null) {
            return mesh;
        }
        return ((RelationalTreeNode) getRootNode()).getMesh();
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public boolean isSubgraph() {
        return isSubgraph;
    }

    public void setIsSubgraph(boolean isContainer) {
        isSubgraph = isContainer;
    }
}
