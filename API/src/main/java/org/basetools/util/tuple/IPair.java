package org.basetools.util.tuple;

public interface IPair<L, R> {
    static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    int compareTo(Pair<L, R> other);

    L getLeft();

    void setLeft(L left);

    R getRight();

    void setRight(R right);

    L getKey();

    R getValue();

    R setValue(R value);

    String toString(String format);
}
