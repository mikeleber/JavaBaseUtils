package org.basetools.util.tree;

public interface Compactor<T, U> {
    boolean compact(TreeNode<T, U> node);

    boolean compactParent(TreeNode<T, U> node);

    public void expand(TreeNode<T, U> node);

    boolean canExpand();

    public boolean isRelevant(TreeNode<T, U> node);
}
