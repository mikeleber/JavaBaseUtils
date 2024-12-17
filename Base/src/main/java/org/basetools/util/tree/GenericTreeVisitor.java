package org.basetools.util.tree;

import org.basetools.visitor.StackedTreeVisitor;
import org.w3c.dom.Node;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class GenericTreeVisitor<T, U, R> extends StackedTreeVisitor<R> implements TreeVisitor<T, U> {
    private Predicate<Node> _doRender;
    private BiFunction<TreeNode<T, U>, R, R> _containerRenderer;
    private BiFunction<TreeNode<T, U>, R, R> _leafRenderer;
    private Predicate<TreeNode<T, U>> _breakPredicate;

    public GenericTreeVisitor() {
        this(null, null, null);
    }

    public GenericTreeVisitor(R rootElement, BiFunction<TreeNode<T, U>, R, R> containerRenderer, BiFunction<TreeNode<T, U>, R, R> leafRenderer) {
        _containerRenderer = containerRenderer;
        _leafRenderer = leafRenderer;
        push(_containerRenderer.apply(null, rootElement));
    }

    @Override
    public void visitStart(TreeNode<T, U> aNode) {
        R builder = peek();
        if (aNode.isLeaf()) {
            _leafRenderer.apply(aNode, builder);
        } else {
            push(_containerRenderer.apply(aNode, builder));
        }
    }

    @Override
    public void visitEnd(TreeNode<T, U> aNode) {
        if (!aNode.isLeaf()) {
            pop();
        }
    }

    @Override
    public boolean doBreak(TreeNode<T, U> aNode) {
        if (_breakPredicate != null) {
            return _breakPredicate.test(aNode);
        } else {
            return false;
        }
    }

    public <V extends TreeVisitor<T, U>> V withBreakPredicate(Predicate<TreeNode<T, U>> breakPredicate) {
        _breakPredicate = breakPredicate;
        return (V) this;
    }
}
