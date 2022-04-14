package org.basetools.util.compare;

import org.basetools.util.StringUtils;

import java.util.Comparator;

public class PathComparator<T> implements Comparator<String> {
    private String _baseName;
    private Order _ascending;

    public PathComparator(String baseName) {
        this(baseName, Order.asc);
    }

    public PathComparator(String baseName, Order ascending) {
        _baseName = baseName;
        _ascending = ascending;
    }

    @Override
    public int compare(String aPath, String bPath) {
        aPath = aPath.substring(0, aPath.indexOf("="));
        bPath = bPath.substring(0, bPath.indexOf("="));

        if (aPath != null && bPath != null) {
            if (!aPath.startsWith(_baseName) && bPath.startsWith(_baseName)) {
                return checkInvert(1);
            }
            if (!bPath.startsWith(_baseName) && aPath.startsWith(_baseName)) {
                return checkInvert(0);
            }
            int tokensA = StringUtils.countChars(aPath, '/', false);
            int tokensB = StringUtils.countChars(bPath, '/', false);
            if (tokensA == tokensB) {
                int cp = aPath.compareTo(bPath);
                if (cp == 0) {
                    return checkInvert(1);
                }
                return (cp < 0) ? checkInvert(1) : checkInvert(0);
            }
            return (tokensA < tokensB) ? checkInvert(1) : checkInvert(0);
        }
        return checkInvert(0);
    }

    private int checkInvert(int value) {
        if (_ascending == Order.asc) {
            switch (value) {
                case -1:
                    return 1;
                case 1:
                    return -1;
            }
        }
        return value;
    }

    public enum Order {asc, desc}
}

