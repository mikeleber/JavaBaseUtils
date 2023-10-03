package org.basetools.util.sort;

import java.util.*;

public class MapUtil {
    public static <K, V> Map<K, V> sortByValue(Map<K, V> map,Comparator<Map.Entry<K,V>> comparator) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, comparator);

        Map<K, V> result = new LinkedHashMap<>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
//    Comparator defaultComp = new Comparator<Object>() {
//        @SuppressWarnings("unchecked")
//        public int compare(Object o1, Object o2) {
//            return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
//        }
//    };
}
