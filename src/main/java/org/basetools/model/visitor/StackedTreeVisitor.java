package org.basetools.model.visitor;

import java.util.Stack;

public abstract class StackedTreeVisitor<T> {
    protected Stack<T> builderStack = new Stack();

    public T peek() {
        return builderStack.peek();
    }

    public T pop() {
        return builderStack.pop();
    }

    public T push(T item) {
        return builderStack.push(item);
    }
}
