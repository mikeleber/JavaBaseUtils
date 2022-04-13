package org.basetools.util.sort;

import org.basetools.util.StringUtils;

import java.util.Comparator;

public class XPathComparator<T> implements Comparator<T> {
    private String _rootName;

    public XPathComparator(String rootName) {
        _rootName = rootName;
    }

    @Override
    public int compare(T a, T b) {
        String pathA = null;
        String pathB = null;

        if (a instanceof String && b instanceof String) {
            // NLS-XPATH sorting
            String aString = (String) a;
            String bString = (String) b;
            pathA = aString.substring(0, aString.indexOf("="));
            pathB = bString.substring(0, bString.indexOf("="));
        }
        if (pathA != null && pathB != null) {
            if (!pathA.startsWith(_rootName) && pathB.startsWith(_rootName)) {
                return 1;
            }
            if (!pathB.startsWith(_rootName) && pathA.startsWith(_rootName)) {
                return -1;
            }
            int tokensA = StringUtils.countChars(pathA, '/', false);
            int tokensB = StringUtils.countChars(pathB, '/', false);
            if (tokensA == tokensB) {
                int cp = pathA.compareTo(pathB);
                // if a equals b then the included is the least xui component!
                if (cp == 0) {
                    return (1);
                }
                return (cp < 0) ? 1 : -1;
            }
            return (tokensA < tokensB) ? 1 : -1;
        }
        return -1;
    }
}
