package org.basetools.visitor;

import java.util.Stack;

public abstract class StackedTreeVisitor<T> {
    protected Stack<T> builderStack = new Stack();

    public T peek() {
        return builderStack.peek();
    }

    public boolean isEmpty() {
        return builderStack.isEmpty();
    }

    public T pop() {
        return builderStack.pop();
    }

    public T push(T item) {
        return builderStack.push(item);
    }
}
