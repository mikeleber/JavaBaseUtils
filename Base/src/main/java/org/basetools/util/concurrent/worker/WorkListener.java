package org.basetools.util.concurrent.worker;

public interface WorkListener<I> {
    void finished(WorkService<I>.RunnableWrapper<I> work);
}
