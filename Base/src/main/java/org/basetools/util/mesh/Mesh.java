package org.basetools.util.mesh;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.basetools.util.tree.TreeNode;

import java.util.*;

public class Mesh<T, U> {
    public static final String NEWLINE = "\n";
    private List<RelationalTreeNode<T, U>> roots;
    private Map<Object, RelationalTreeNode<T, U>> cache = new HashMap<>();

    public List<RelationalTreeNode<T, U>> getRoots() {
        if (roots == null) {
            roots = new ArrayList<>();
        }
        return roots;
    }

    public void addNode(RelationalTreeNode<T, U> parent, RelationalTreeNode<T, U> child) {
        parent.addChild(child);
        Object id = child.getID();
        cache.put(id, child);
    }

    public Map<Object, RelationalTreeNode<T, U>> getCache() {
        return cache;
    }

    public void addRoot(RelationalTreeNode root) {

        cache.clear();
        cache.put(root.getID(), root);
        getRoots().add(root);
    }

    public void clearCache() {
        cache.clear();
    }

    public RelationalTreeNode<T, U> find(Object parentID) {
        return cache.get(parentID);
    }

    public StringBuilder toMermaidGraph(int level) {
        StringBuilder builder = new StringBuilder();
        builder.append("graph TD;");

        List<NodeRelation> evalRelations = evalRelations(1);
        for (NodeRelation relation : evalRelations) {
            builder.append(relation.getRelationFrom().goUpToLevel(level).getID() + "-->");
            TreeNode<T, U> toTopLevelNode = relation.getRelationTo().goUpToLevel(level);
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();
            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
                builder.append("|" + relation.getName() + "|");
            }
            builder.append(toTopLevelNodeId + ";");
        }
        return builder;
    }

    public StringBuilder toMermaidGraph2(int level) {
        StringBuilder builder = new StringBuilder();
        builder.append("graph TD;");

        List<NodeRelation> evalRelations = evaluateRelationMap(roots);
        for (NodeRelation relation : evalRelations) {
            String fromTopLevelNodeId = relation.getRelationFrom().goUpToLevel(level).getID();
            builder.append(fromTopLevelNodeId + "-->");
            TreeNode<T, U> toTopLevelNode = relation.getRelationTo().goUpToLevel(level);
            String toTopLevelNodeId = toTopLevelNode.getID();
            String relationName = relation.getName();
            if (StringUtils.isNotEmpty(relationName) && !relationName.equals(toTopLevelNodeId)) {
                builder.append("|" + relation.getName() + "|");
            }
            builder.append(toTopLevelNodeId + ";");
        }
        return builder;
    }

    public RelationalTreeNode createSystemTree(int level, Set<RelationalTreeNode> selection) {
        RelationalTreeNode targetTree = new RelationalTreeNode();
        Set<RelationalTreeNode> referencesTo = new HashSet<>();

        List<NodeRelation> evalRelations = evaluateRelationMap(roots);
        for (NodeRelation relation : evalRelations) {
            if (isInSelectionRange(selection, relation.getRelationFrom(), relation.getRelationTo())) {

                RelationalTreeNode<T, U> from = relation.getRelationFrom();
                RelationalTreeNode<T, U> to = relation.getRelationTo();
                if (from != null && to != null) {

                    RelationalTreeNode<T, U> fromTopLevelNode = (RelationalTreeNode) from.goUpToLevel(level);
                    RelationalTreeNode<T, U> toTopLevelNode = (RelationalTreeNode) to.goUpToLevel(level);
                    referencesTo.add(relation.getRelationTo());
                    List creationPath = fromTopLevelNode.getPathToParent(false);
                    creationPath.remove(0);
                    RelationalTreeNode<T, U> createdFromNode = (RelationalTreeNode<T, U>) targetTree.addNode(creationPath, (node, templateNode) -> {

                        RelationalTreeNode newNode = RelationalTreeNode.of((RelationalTreeNode) templateNode);
                        //newNode.setIsSubgraph(templateNode.getNumberOfChildren() > 0);
                        return newNode;
                    });
                    createdFromNode.addRelation(relation.getId(), fromTopLevelNode).setName(relation.getName()).setToRelation(toTopLevelNode);
                    creationPath = toTopLevelNode.getPathToParent(false);
                    creationPath.remove(0);
                    RelationalTreeNode<T, U> createdToNode = (RelationalTreeNode<T, U>) targetTree.addNode(creationPath, (node, templateNode) -> {
                        RelationalTreeNode newNode = RelationalTreeNode.of((RelationalTreeNode) templateNode);
                        // newNode.setIsSubgraph(templateNode.getNumberOfChildren() > 0);
                        return newNode;
                    });
                } else {
                    //from or to not found
                }
            }
        }

        return targetTree;
    }

    private boolean isInSelectionRange(Set<RelationalTreeNode> selection, RelationalTreeNode relationFrom, RelationalTreeNode relationTo) {
        if (CollectionUtils.isNotEmpty(selection)) {
            for (RelationalTreeNode selected : selection) {
                if (relationFrom.equals(selected) || relationFrom.hasParent(selected) || relationTo.equals(selected) || relationTo.hasParent(selected)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public List<NodeRelation> evalRelations(int level) {
        List<NodeRelation> relations = new ArrayList<>();
        HashMap<RelationalTreeNode, Map<String, NodeRelation>> relationCache = new HashMap();

        for (RelationalTreeNode root : roots) {
            List<RelationalTreeNode<T, U>> bases = root.getNodesAtLevel(level);
            for (RelationalTreeNode base : bases) {

                Map<String, NodeRelation> nodeRelations = evaluateRelationMap(relationCache, base);

                for (RelationalTreeNode rootTo : bases) {
                    if (base != rootTo) {
                        Iterator<Map.Entry<String, NodeRelation>> relationsIt = nodeRelations.entrySet().iterator();
                        while (relationsIt.hasNext()) {
                            Map.Entry<String, NodeRelation> aEntry = relationsIt.next();
                            RelationalTreeNode relationToNode = aEntry.getValue().getRelationTo();
                            if (relationToNode == null) {
                                System.out.println("relationToNode not found for target:" + aEntry.getValue().getTargetNodeID());
                            } else if (Objects.equals(relationToNode, rootTo) || relationToNode.hasParent(rootTo)) {
                                relations.add(aEntry.getValue());
                            }
                        }
                    }
                }
            }
        }
        return relations;
    }

    public List<NodeRelation> evaluateRelationMap(List<RelationalTreeNode<T, U>> baseNodes) {
        List<NodeRelation> relations = new ArrayList<>();
        HashMap<RelationalTreeNode, Map<String, NodeRelation>> relationCache = new HashMap();

        for (RelationalTreeNode base : baseNodes) {

            Map<String, NodeRelation> nodeRelations = evaluateRelationMap(relationCache, base);

            Iterator<Map.Entry<String, NodeRelation>> relationsIt = nodeRelations.entrySet().iterator();
            while (relationsIt.hasNext()) {
                Map.Entry<String, NodeRelation> aEntry = relationsIt.next();
                relations.add(aEntry.getValue());
            }
        }
        return relations;
    }

    private Map<String, NodeRelation> evaluateRelationMap(HashMap<RelationalTreeNode, Map<String, NodeRelation>> relationCache, RelationalTreeNode root) {
        Map<String, NodeRelation> relations = relationCache.get(root);
        if (relations == null) {
            relations = new HashMap<>();
            relationCache.put(root, relations);
            root.findRelations(relations);
        }
        return relations;
    }
}
