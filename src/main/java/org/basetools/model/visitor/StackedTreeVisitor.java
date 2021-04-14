package org.basetools.model.visitor;

import java.util.Stack;

public abstract class StackedTreeVisitor<T> {
    protected Stack<T> builderStack = new Stack();

    protected T peek() {
        return builderStack.peek();
    }

    protected T pop() {
        return builderStack.pop();
    }

    protected T push(T item) {
        return builderStack.push(item);
    }
}
