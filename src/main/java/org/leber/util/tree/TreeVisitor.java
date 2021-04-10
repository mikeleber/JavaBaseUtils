package org.leber.util.tree;

public interface TreeVisitor<T, U> {
    public void visitStart(TreeNode<T, U> aNode);

    public void visitEnd(TreeNode<T, U> aNode);

    boolean doBreak(TreeNode<T, U> aNode);
}
