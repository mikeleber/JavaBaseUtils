package org.basetools.model.visitor;

import java.util.Stack;

public abstract class StackedTreeVisitor<T> {
    protected Stack<T> builderStack = new Stack();

    T peek() {
        return builderStack.peek();
    }

    T pop() {
        return builderStack.pop();
    }

    T push(T item) {
        return builderStack.push(item);
    }
}
