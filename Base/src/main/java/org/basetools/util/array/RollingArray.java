package org.basetools.util.array;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RollingArray<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private T[] _array;
    private int _actSize;
    private int _actPos;
    private Class<T> _entryClazz;
    private int _startPos;

    public RollingArray(Class<T> clazz) {
        this(clazz, DEFAULT_CAPACITY);
    }

    public RollingArray(Class<T> clazz, int capacity) {
        _entryClazz = clazz;
        _array = (T[]) Array.newInstance(_entryClazz, capacity);
        makeEmpty();
    }

    public synchronized void clear() {
        _array = (T[]) Array.newInstance(_entryClazz, _array.length);
        makeEmpty();
    }

    public synchronized void reinitialize(int size) {
        _array = (T[]) Array.newInstance(_entryClazz, size);
        makeEmpty();
    }

    public boolean isEmpty() {
        return _actSize == 0;
    }

    private synchronized void makeEmpty() {
        _actSize = 0;
        _startPos = 0;
        _actPos = -1;
    }

    public synchronized T pop() {
        if (isEmpty()) {
            throw new UnderflowException("RollingStack pop");
        }
        _actSize--;
        T returnValue = _array[_actPos];
        _actPos = decrement(_actPos);
        return returnValue;
    }


    public String toString() {
        if (_actSize <= 0) {
            return "";
        }
        StringBuffer result = new StringBuffer();
        int start = _startPos;
        for (int i = 0; i < _actSize; i++) {
            result.append(_array[start++]);
            if (start >= _array.length) {
                start = 0;
            }
        }
        return result.toString();
    }

    private int decrement(int val) {
        val--;
        val = val >= 0 ? val : _array.length - 1;
        return val;
    }

    public synchronized T peek() {
        if (isEmpty()) {
            throw new UnderflowException("RollingStack peek");
        }
        return _array[_actPos];
    }

    public synchronized T first() {
        if (isEmpty()) {
            throw new UnderflowException("RollingStack:first");
        }
        return _array[_startPos];
    }

    public int size() {
        return _actSize;
    }

    public synchronized void push(T obj) {
        moveVorward();
        _array[_actPos] = obj;
    }

    public synchronized void moveVorward() {
        if (_actSize < _array.length) {
            _actSize++;
        } else {
            _startPos = increment(_startPos);
        }
        _actPos = increment(_actPos);
    }

    public synchronized void push(T[] vals) {
        for (int v = 0; v < vals.length; v++) {
            push(vals[v]);
        }
    }

    private int increment(int x) {
        if (++x == _array.length) {
            x = 0;
        }
        return x;
    }

    public String getInfo() {
        return "currentPos: " + _actPos + " currentSize: " + _actSize + " arraySize: " + _array.length;
    }

    private T[] getArray() {
        return _array;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>();
        traverse(entry -> result.add(entry));
        return result;
    }

    public T[] toArray() {
        T[] array = Arrays.copyOf(_array, _actSize);
        if (_actSize <= 0) {
            return array;
        }
        int start = _startPos;
        for (int i = 0; i < _actSize; i++) {
            array[i] = _array[start++];
            if (start >= _array.length) {
                start = 0;
            }
        }
        return array;
    }

    public synchronized void traverse(Consumer<T> consumer) {
        if (_actSize <= 0) {
            return;
        }
        int start = _startPos;
        for (int i = 0; i < _actSize; i++) {
            consumer.accept(_array[start++]);
            if (start >= _array.length) {
                start = 0;
            }
        }
    }

    public synchronized void drainOut(Consumer<T> consumer) {
        traverse(consumer);
        makeEmpty();
    }

    public CompletableFuture<RollingArray> drainOutAsync(Consumer<T> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            drainOut(consumer);
            return this;
        });

    }
}
