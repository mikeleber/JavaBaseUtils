package org.basetools.util.compare;

import java.util.Comparator;
import java.util.HashMap;

class StringSequenceComparator implements Comparator<String> {
    private HashMap<String, Integer> _map;
    private boolean _desc;

    public StringSequenceComparator(String... sequence) {
        _map = new HashMap<>();
        for (int s = 0; s < sequence.length; s++) {
            _map.put(sequence[s], s++);
        }
    }

    public StringSequenceComparator withOrder(boolean desc) {
        _desc = desc;
        return this;
    }

    public int compare(String s1, String s2) {
        if (_desc)
            return _map.get(s2) - _map.get(s1);
        else
            return _map.get(s1) - _map.get(s2);
    }
}