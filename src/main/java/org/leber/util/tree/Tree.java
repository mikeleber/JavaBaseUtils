package org.leber.util.tree;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree<T, U> {
    private final Map<Object, TreeNode<T, U>> cache = new HashMap<>();
    private TreeNode<T, U> root;

    public Tree() {
        super();
    }

    public TreeNode<T, U> getRoot() {
        return root;
    }

    public void setRoot(TreeNode<T, U> root) {
        cache.clear();
        cache.put(root.getID(), root);
        this.root = root;
    }

    public int getNumberOfNodes() {
        int numberOfNodes = 0;
        if (root != null) {
            numberOfNodes = auxiliaryGetNumberOfNodes(root) + 1; // 1 for the root!
        }
        return numberOfNodes;
    }

    public List<TreeNode<T, U>> build(GenericTreeTraversalOrderEnum traversalOrder) {
        List<TreeNode<T, U>> returnList = null;
        if (root != null) {
            returnList = build(root, traversalOrder);
        }
        return returnList;
    }

    public List<TreeNode<T, U>> build(TreeNode<T, U> node, GenericTreeTraversalOrderEnum traversalOrder) {
        List<TreeNode<T, U>> traversalResult = new ArrayList<>();
        if (traversalOrder == GenericTreeTraversalOrderEnum.PRE_ORDER) {
            buildPreOrder(node, traversalResult);
        } else if (traversalOrder == GenericTreeTraversalOrderEnum.POST_ORDER) {
            buildPostOrder(node, traversalResult);
        }
        return traversalResult;
    }

//	private void buildPreOrder(TreeNode<T, U> node, List<TreeNode<T, U>> traversalResult) {
//		traversalResult.add(node);
//		for (TreeNode<T, U> child : node.getChildren()) {
//			buildPreOrder(child, traversalResult);
//		}
//	}
//
//	private void buildPostOrder(TreeNode<T, U> node, List<TreeNode<T, U>> traversalResult) {
//		for (TreeNode<T, U> child : node.getChildren()) {
//			buildPostOrder(child, traversalResult);
//		}
//		traversalResult.add(node);
//	}

    private int auxiliaryGetNumberOfNodes(TreeNode<T, U> node) {
        int numberOfNodes = node.getNumberOfChildren();
        for (TreeNode<T, U> child : node.getChildren()) {
            numberOfNodes += auxiliaryGetNumberOfNodes(child);
        }
        return numberOfNodes;
    }

    public boolean exists(TreeNode<T, U> nodeToFind) {
        return (find(nodeToFind) != null);
    }

    public TreeNode<T, U> find(TreeNode<T, U> nodeToFind) {
        TreeNode<T, U> returnNode = null;
        if (root != null) {
            returnNode = auxiliaryFind(root, nodeToFind);
        }
        return returnNode;
    }

    private void manipulateTree(TreeNode<T, U> currNode, Compactor modifier) {
        currNode.manipulateStructure(modifier);
    }

    public TreeNode<T, U> compactTree(TreeNode<T, U> startNode, Compactor modifier, boolean evalNewRootNode) {
        manipulateTree(startNode, modifier);
        TreeNode<T, U> topnode = startNode;
        List<TreeNode<T, U>> leafs = findLeafNodes(startNode == null ? getRoot() : startNode);
        for (TreeNode<T, U> leaf : leafs) {
            TreeNode<T, U> aParent = null;
            TreeNode<T, U> currLeaf = leaf;
            while ((aParent = currLeaf.getParent()) != null) {
                if (modifier.compact(aParent)) {
                    if (aParent != startNode) {
                        if (aParent.getParent() != null) {
                            aParent.getParent().removeChild(aParent, true);
                        }
                    } else {
                        break;
                    }
                }
                currLeaf = aParent;
            }
        }
        // topnode.removeFromParent();
        if (evalNewRootNode) {
            if (topnode.getDepth() > 2) {
                // GenericTreeNode<T, U> childNode = topnode.getChildAt(0);
                TreeNode<T, U> childNode = topnode;
                while (childNode.getNumberOfChildren() > 0) {
                    TreeNode<T, U> newChildNode = childNode.getChildAt(0);
                    if (modifier.compactParent(childNode)) {
                        if (childNode == startNode) {
                            // never remove root node!
                            childNode = newChildNode;
                            topnode = newChildNode;
                        } else {
                            childNode.removeFromParent();
                            childNode = newChildNode;
                        }
                    } else {
                        break;
                    }
                }
                // topnode = childNode;
            }
        }
        return topnode;
    }

    public void clearTreeStructure(TreeNode<T, U> startNode, Compactor modifier) {
        List<TreeNode<T, U>> leafs = findLeafNodes(startNode == null ? getRoot() : startNode);
        for (TreeNode<T, U> leaf : leafs) {
            if (!modifier.isRelevant(leaf)) {
                TreeNode<T, U> aParent = leaf.getParent();
                leaf.removeFromParent();
                clearParentTreeStructure(aParent, modifier);
            }
        }
    }

    private void clearParentTreeStructure(TreeNode<T, U> startNode, Compactor modifier) {
        if (!modifier.isRelevant(startNode)) {
            TreeNode<T, U> aParent = startNode.getParent();
            startNode.removeFromParent();
            if (aParent != null) {
                clearParentTreeStructure(aParent, modifier);
            }
        }
    }

    public List<TreeNode<T, U>> findLeafNodes(TreeNode<T, U> startNode) {
        return startNode.findLeafNodes();
    }

    private TreeNode<T, U> auxiliaryFind(TreeNode<T, U> currentNode, TreeNode<T, U> nodeToFind) {
        TreeNode<T, U> returnNode = null;
        int i = 0;
        if (currentNode.equals(nodeToFind)) {
            returnNode = currentNode;
        } else if (currentNode.getData() == nodeToFind.getData() && currentNode.getUserObject() == nodeToFind.getUserObject()) {
            returnNode = currentNode;
        } else if (currentNode.hasChildren()) {
            i = 0;
            while (returnNode == null && i < currentNode.getNumberOfChildren()) {
                returnNode = auxiliaryFind(currentNode.getChildAt(i), nodeToFind);
                i++;
            }
        }
        return returnNode;
    }

    public boolean isEmpty() {
        return (root == null);
    }

    private void buildPreOrder(TreeNode<T, U> node, List<TreeNode<T, U>> traversalResult) {
        traversalResult.add(node);
        for (TreeNode<T, U> child : node.getChildren()) {
            buildPreOrder(child, traversalResult);
        }
    }

    private void buildPostOrder(TreeNode<T, U> node, List<TreeNode<T, U>> traversalResult) {
        for (TreeNode<T, U> child : node.getChildren()) {
            buildPostOrder(child, traversalResult);
        }
        traversalResult.add(node);
    }

    private void buildPreOrderWithDepth(TreeNode<T, U> node, Map<TreeNode<T, U>, Integer> traversalResult, int depth) {
        traversalResult.put(node, depth);
        for (TreeNode<T, U> child : node.getChildren()) {
            buildPreOrderWithDepth(child, traversalResult, depth + 1);
        }
    }

    private void buildPostOrderWithDepth(TreeNode<T, U> node, Map<TreeNode<T, U>, Integer> traversalResult, int depth) {
        for (TreeNode<T, U> child : node.getChildren()) {
            buildPostOrderWithDepth(child, traversalResult, depth + 1);
        }
        traversalResult.put(node, depth);
    }

    /**
     * Inserts a node and returns a reference to the new node.
     */
    private TreeNode<T, U> insertNode(Object what, TreeNode<T, U> where) {
        // GenericTreeNode<T, U> node = new GenericTreeNode<T, U>(what);
        // insertNodeInto(node, where);
        return null;
    } // insertNode(Node,MutableTreeNode):MutableTreeNode

    /**
     * Inserts the document node.
     */
    private TreeNode<T, U> insertDocumentNode(Node what, TreeNode<T, U> where) {
        TreeNode<T, U> treeNode = insertNode(what, where);
        return treeNode;
    }

    /**
     * Inserts a text node.
     */
    private TreeNode<T, U> insertTextNode(Node what, TreeNode<T, U> where) {
        U value = (U) what.getNodeValue();
        if (value != null) {
            if (value instanceof String) {
                value = (U) ((String) value).trim();
            }
            where.setUserObject(value);
        }
        return null;
    }

    /**
     * Inserts a text node.
     */
    private TreeNode<T, U> insertAttributeNode(Node what, TreeNode<T, U> where) {
        T name = (T) what.getNodeName();
        U value = (U) what.getNodeValue();
        if (value instanceof String) {
            value = (U) ((String) value).trim();
        }
        TreeNode<T, U> newNode = new TreeNode<>(name, value);
        if (where != null) {
            where.addChild(newNode);
        }
        return newNode;
    }

    /**
     * Inserts a CData Section Node.
     */
    private TreeNode<T, U> insertCDataSectionNode(Node what, TreeNode<T, U> where) {
        StringBuffer CSectionBfr = new StringBuffer();
        T name = (T) "CDATA";
        // --- optional --- CSectionBfr.append( "<![CDATA[" );
        CSectionBfr.append(what.getNodeValue());
        // --- optional --- CSectionBfr.append( "]]>" );
        if (CSectionBfr.length() > 0) {
            TreeNode<T, U> newNode = new TreeNode<>(name, (U) CSectionBfr.toString());
            if (where != null) {
                where.addChild(newNode);
            }
            return newNode;
        }
        return null;
    }

    public void addNode(TreeNode<T, U> parent, TreeNode<T, U> child) {
        cache.put(child.getID(), child);
        parent.addChild(child);
    }

    public boolean addNode(Object parentID, TreeNode<T, U> child) {
        TreeNode<T, U> parentNode = cache.get(parentID);
        if (parentNode != null) {
            cache.put(child.getID(), child);
            parentNode.addChild(child);
            return true;
        }
        return false;
    }

    public void clearCache() {
        cache.clear();
    }

    public TreeNode<T, U> find(Object parentID) {
        return cache.get(parentID);
    }

    public TreeNode<T, U> getLastNode() {
        return getRoot().getLastChild();
        // TreeNode last = null;
        // if (nodes != null && nodes.size() > 0) {
        // last = getNode(nodes.get(nodes.size() - 1));
        // }
        // return last;
    }

    public TreeNode<T, U> getFirstNode() {
        return getRoot().getFirstChild();
    }

    public <G> void addNode(String[] nodePath, T data) {
        if (nodePath != null) {
            TreeNode<T, U> baseNode = getRoot();
            if (baseNode == null) {
                baseNode = new TreeNode<>("root", null);
                setRoot(baseNode);
            }
            for (int i = 0; i < nodePath.length; i++) {
                String pNode = nodePath[i];
                if (pNode != null && pNode.length() > 0) {
                    TreeNode<T, U> aChild = baseNode.getChildByID(pNode);
                    if (aChild == null) {
                        aChild = new TreeNode<>(pNode, null);
                        baseNode.addChild(aChild);
                    }
                    baseNode = aChild;
                    if (i + 1 == nodePath.length) {
                        aChild.setData(data);
                    }
                }
            }
        }
    }

    public void addPath(String path, boolean check, T data) {
        if (path != null) {
            String[] elems = path.split("/");
            addNode(elems, data);
        }
    }

    public void addPaths(NodePathFacade<T> facade, List<T> data) {
        if (facade != null) {
            if (data != null) {
                for (int d = 0; d < data.size(); d++) {
                    String[] elems = facade.buildPath(data.get(d));
                    addNode(elems, data.get(d));
                }
            }
        }
    }

    public void addPath(NodePathFacade<T> facade, T data) {
        if (facade != null) {
            String[] elems = facade.buildPath(data);
            addNode(elems, data);
        }
    }
}
