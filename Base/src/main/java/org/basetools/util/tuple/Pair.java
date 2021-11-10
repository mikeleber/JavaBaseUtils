package org.basetools.util.tuple;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {

    private static final long serialVersionUID = 7436234136177946306L;

    public L key;
    public R value;

    public Pair() {
        super();
    }

    public Pair(final L left, final R right) {
        super();
        this.key = left;
        this.value = right;
    }

    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }

    public int compareTo(final Pair<L, R> other) {
        return new CompareToBuilder().append(getLeft(), other.getLeft())
                .append(getRight(), other.getRight()).toComparison();
    }

    public L getLeft() {
        return getKey();
    }

    public void setLeft(final L left) {
        this.key = left;
    }

    public R getRight() {
        return getValue();
    }

    public void setRight(final R right) {
        this.value = right;
    }

    public L getKey() {
        return key;
    }

    public R getValue() {
        return value;
    }

    @Override
    public R setValue(final R value) {
        final R result = getRight();
        setRight(value);
        return result;
    }

    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^
                (getValue() == null ? 0 : getValue().hashCode());
    }

    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry<?, ?>) {
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return Objects.equals(getKey(), other.getKey())
                    && Objects.equals(getValue(), other.getValue());
        }
        return false;
    }

    public String toString() {
        return getLeft() + "=" + getRight();
    }

    public String toString(final String format) {
        return String.format(format, getLeft(), getRight());
    }
}