package org.basetools.util.compare;

import org.basetools.util.collection.CollectionUtils;

import java.util.*;

public class CollectionCompareUtils {

    /**
     * Returns true if vectA and vectB have any elements in common.
     */
    public static <T> boolean overlaps(List<T> listA, List<T> listB) {
        if (listA == null || listB == null || listA.size() == 0 || listB.size() == 0) {
            return false;
        }
        if ((listA.size() + listB.size()) > 20) {
            Hashtable workSet = new Hashtable(listA.size());
            for (int i = 0; i < listA.size(); i++) {
                workSet.put(listA.get(i), listA.get(i));
            }
            for (int i = 0; i < listB.size(); i++) {
                if (workSet.containsKey(listB.get(i))) {
                    return true;
                }
            }
        } else {
            if (listA.size() > listB.size()) {
                for (int i = 0; i < listA.size(); i++) {
                    if (listB.contains(listA.get(i))) {
                        return true;
                    }
                }
            } else {
                for (int i = 0; i < listB.size(); i++) {
                    if (listA.contains(listB.get(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static <T> List<T> intersection(List<T> vectA, List<T> listB) {
        Hashtable workSetA = new Hashtable(), workSetB = new Hashtable(), resultSet = new Hashtable();
        Vector result = new Vector();
        Enumeration aTempEnum;
        Object item;
        /* -- */
        if (vectA != null) {
            for (Object o : vectA) {
                workSetA.put(o, o);
            }
        }
        if (listB != null) {
            for (Object o : listB) {
                workSetA.put(o, o);
            }
        }
        aTempEnum = workSetA.elements();
        while (aTempEnum.hasMoreElements()) {
            item = aTempEnum.nextElement();
            if (workSetB.containsKey(item)) {
                resultSet.put(item, item);
            }
        }
        aTempEnum = workSetB.elements();
        while (aTempEnum.hasMoreElements()) {
            item = aTempEnum.nextElement();
            if (workSetA.containsKey(item)) {
                resultSet.put(item, item);
            }
        }
        aTempEnum = resultSet.elements();
        while (aTempEnum.hasMoreElements()) {
            result.addElement(aTempEnum.nextElement());
        }
        return result;
    }


    /**
     * This method returns a Vector containing the set of objects contained in vectA that are not contained in vectB. This method will always return a new, non-null Vector, even if vectA and/or vectB are null.
     */
    public static Vector difference(Vector vectA, Vector vectB) {
        Vector result = new Vector();
        Object item;
        /* -- */
        if (vectA == null) {
            return result;
        }
        if (vectB == null) {
            return (Vector) vectA.clone();
        }
        if (vectA.size() + vectB.size() > 10) // ass
        {
            Hashtable workSetB = new Hashtable();
            Enumeration aTempEnum;
            /* -- */
            aTempEnum = vectB.elements();
            while (aTempEnum.hasMoreElements()) {
                item = aTempEnum.nextElement();
                workSetB.put(item, item);
            }
            aTempEnum = vectA.elements();
            while (aTempEnum.hasMoreElements()) {
                item = aTempEnum.nextElement();
                if (!workSetB.containsKey(item)) {
                    result.addElement(item);
                }
            }
        } else {
            for (int i = 0; i < vectA.size(); i++) {
                item = vectA.elementAt(i);
                if (!vectB.contains(item)) {
                    result.addElement(item);
                }
            }
        }
        return result;
    }

    public static List compareDifference(Collection vectA, Collection vectB) {
        Collection tempA = vectA, tempB = vectB;
        if (vectA != null && vectB != null) {
            if (vectB.size() > vectA.size()) {
                tempA = vectB;
                tempB = vectA;
            }
        }
        return difference(tempA, tempB);
    }

    public static List compareSequence(List vectA, List vectB) {
        List tempA = vectA, tempB = vectB;
        if (vectA != null && vectB != null) {
            if (vectB.size() > vectA.size()) {
                tempA = vectB;
                tempB = vectA;
            }
        }
        return sameSequence(tempA, tempB);
    }

    /**
     * This method returns a Collection containing the set of objects contained in vectA that are not contained in vectB. This method will always return a new, non-null List , even if vectA and/or vectB are null.
     */
    public static List difference(Collection vectA, Collection vectB) {
        Object item;
        /* -- */
        Collection temp = null;
        if (vectA != null && vectB != null && vectB.size() > vectA.size()) {
            temp = vectA;
            vectA = vectB;
            vectB = temp;
        }
        if (vectA == null) {
            return new ArrayList(0);
        }
        if (vectB == null) {
            return Arrays.asList(vectA.toArray());
        }
        ArrayList result = new ArrayList(vectA.size());
        if (vectA.size() + vectB.size() > 10) // ass
        {
            HashMap workSetB = new HashMap();
            Iterator aTempEnum;
            /* -- */
            aTempEnum = vectB.iterator();
            while (aTempEnum.hasNext()) {
                item = aTempEnum.next();
                workSetB.put(item, item);
            }
            aTempEnum = vectA.iterator();
            while (aTempEnum.hasNext()) {
                item = aTempEnum.next();
                if (!workSetB.containsKey(item)) {
                    result.add(item);
                }
            }
        } else {
            Iterator aTempEnum;
            /* -- */
            aTempEnum = vectA.iterator();
            while (aTempEnum.hasNext()) {
                item = aTempEnum.next();
                if (!vectB.contains(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * This method returns a Vector of items that appeared in the vector parameter more than once. If no duplicates are found or if vector is null, this method returns null.
     */
    public static Vector duplicates(Vector vector) {
        if (vector == null) {
            return null;
        }
        Vector result = null;
        Hashtable found = new Hashtable();
        for (int i = 0; i < vector.size(); i++) {
            Object item = vector.elementAt(i);
            if (found.containsKey(item)) {
                if (result == null) {
                    result = new Vector();
                }
                CollectionUtils.unionAdd(result, item);
            }
            found.put(item, item);
        }
        return result;
    }

    public static <T> List<T> sameSequence(List<T> a, List<T> b) {
        if (a == null || b == null) {
            return null;
        }
        ArrayList<T> result = null;
        int bSize = b.size();
        int aSize = a.size();
        for (int i = 0; i < aSize; i++) {
            Object itemA = a.get(i);
            Object itemB = null;
            if (i < bSize && (b.get(i) == itemA || (itemB != null && itemB.equals(itemA)))) {
                // everything is ok!
            } else {
                if (result == null) {
                    result = new ArrayList<T>();
                }
                result.add((T) itemB);
            }
        }
        if (bSize > aSize) {
            for (int i = aSize; i < bSize; i++) {
                Object itemB = b.get(i);
                if (result == null) {
                    result = new ArrayList<T>();
                }
                result.add((T) itemB);
            }
        }
        if (result == null) {
            result = new ArrayList<T>();
        }
        return result;
    }

    public static boolean isSame(List listA, List listB) {
        if (listA.size() != listB.size()) {
            return false;
        }
        for (int a = 0; a < listA.size(); a++) {
            Object oa = listA.get(a);
            Object ob = listB.get(a);
            if (!(oa == ob || oa != null && oa.equals(ob))) {
                return false;
            }
        }
        return true;
    }
}
