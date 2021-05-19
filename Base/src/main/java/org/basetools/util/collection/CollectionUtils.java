package org.basetools.util.collection;

import org.basetools.util.array.ArrayUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {

    /**
     * Wraps the stream map function to create a new converted list using function func.
     *
     * @param from
     * @param func
     * @return a Converted List
     */
    public static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    public static <T> List<T> removeDuplicates(List<T> aList) {
        if (aList == null) {
            return null;
        }
        if (aList.size() <= 1) {
            return aList;
        }
        HashSet<T> h = new HashSet<>(aList);
        if (h.size() != aList.size()) {
            aList.clear();
            aList.addAll(h);
        }
        return aList;
    }

    public static <E> void swap(List<E> a, int i, int j) {
        E tmp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, tmp);
    }

    public static <T> T getFirst(Collection<T> elements) {
        if (elements != null && !elements.isEmpty()) {
            return elements.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * This method returns a Vector containing the union of the objects contained in vectA and vectB. The resulting Vector will not contain any duplicates, even if vectA or vectB themselves contain repeated items. This method will always return a new, non-null Vector, even if vectA and/or vectB are
     * null.
     */
    public static <T> List<T> union(List<T> vectA, List<T> vectB) {
        int threshold = 0;
        if (vectA != null) {
            threshold = vectA.size();
        }
        if (vectB != null) {
            threshold += vectB.size();
        }
        if (threshold < 10) // I pulled 10 out of my ass
        {
            Vector result = new Vector(threshold);
            if (vectA != null) {
                for (int i = 0; i < vectA.size(); i++) {
                    Object obj = vectA.get(i);
                    if (!result.contains(obj)) {
                        result.addElement(obj);
                    }
                }
            }
            if (vectB != null) {
                for (int i = 0; i < vectB.size(); i++) {
                    Object obj = vectB.get(i);
                    if (!result.contains(obj)) {
                        result.addElement(obj);
                    }
                }
            }
            return result;
        } else {
            Hashtable workSet = new Hashtable();
            ArrayList result = new ArrayList(threshold);
            Iterator aTempEnum;
            Object item;
            /* -- */
            if (vectA != null) {
                aTempEnum = vectA.iterator();
                while (aTempEnum.hasNext()) {
                    item = aTempEnum.next();
                    workSet.put(item, item);
                }
            }
            if (vectB != null) {
                aTempEnum = vectB.iterator();
                while (aTempEnum.hasNext()) {
                    item = aTempEnum.next();
                    workSet.put(item, item);
                }
            }
            aTempEnum = workSet.elements().asIterator();
            while (aTempEnum.hasNext()) {
                result.add(aTempEnum.next());
            }
            result.trimToSize();
            return result;
        }
    }

    public static synchronized String[] toStringArray(List vals) {
        String[] result = new String[vals.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = vals.get(i).toString();
        }
        return result;
    }

    /**
     * <P>
     * This method adds obj to vect if and only if vect does not already contain obj.
     * </P>
     */
    public static <T> void  unionAdd(List<T> list, T obj) {
        if (obj == null) {
            return;
        }
        if (list.contains(obj)) {
            return;
        }
        list.add(obj);
    }

    /**
     * Returns true if vectA and vectB have any elements in common.
     */
    public static <T> boolean overlaps(List <T>listA, List <T>listB) {
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


    public static<T> List<T> intersection(List<T> vectA, List<T> listB) {
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

    public static <T> T getFirstCommonObject(List<T> listA, List<T> listB) {
        if (listA != null && listB != null && listB.size() > 0) {
            for (T o :listA)
                if (listB.contains(o)) {
                    return o;
                }
        }
        return null;
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
                unionAdd(result, item);
            }
            found.put(item, item);
        }
        return result;
    }


    public static <T> List<T> removeDuplicates2(List<T> aList) {
        if (aList == null) {
            return null;
        }
        if (aList.size() <= 1) {
            return aList;
        }
        HashSet<T> h = new HashSet<T>(aList);
        if (h.size() != aList.size()) {
            aList.clear();
            aList.addAll(h);
        }
        return aList;
    }

    public static List keepEvery(List array, int every) {
        if (array == null || array.size() == 0 || every == 1) {
            return array;
        }
        for (int i = array.size() - 1; i >= 0; i--) {
            if ((i + 1) % every != 0) {
                array.remove(i);
            }
        }
        return array;
    }

    public static List removeEvery(List array, int every) {
        if (array == null || array.size() == 0) {
            return array;
        }
        if (every == 1) {
            array.clear();
            return array;
        }
        for (int i = array.size() - 1; i >= 0; i--) {
            if ((i + 1) % every == 0) {
                array.remove(i);
            }
        }
        return array;
    }

    /**
     * removes all duplicate object entries except last! object sequence is maintained!
     *
     * @param aList
     * @return
     */
    public static <T> List<T> removeDuplicatesLastWins(List<T> aList) {
        if (aList == null) {
            return null;
        }
        if (aList.size() <= 1) {
            return aList;
        }
        LinkedHashMap found = new LinkedHashMap(aList.size());
        boolean dupFound = false;
        for (int i = aList.size() - 1; i >= 0; i--) {
            Object item = aList.get(i);
            if (!found.containsKey(item)) {
                found.put(item, item);
            } else {
                dupFound = true;
            }
        }
        if (dupFound) {
            aList.clear();
            aList.addAll(found.values());
            Collections.reverse(aList);
        }
        return aList;
    }

    public static List removeLast(List aList, int count) {
        if (aList == null) {
            return null;
        }
        int from = aList.size() - 1;
        int to = from - count;
        to = (to < 0 ? 0 : to);
        for (int i = from; i > to; i--) {
            aList.remove(i);
        }
        return aList;
    }


    public static <T> List<T> sameSequence(List <T>a, List<T> b) {
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
                    result = new ArrayList<T> ();
                }
                result.add((T)itemB);
            }
        }
        if (bSize > aSize) {
            for (int i = aSize; i < bSize; i++) {
                Object itemB = b.get(i);
                if (result == null) {
                    result = new ArrayList<T>();
                }
                result.add((T)itemB);
            }
        }
        if (result == null) {
            result = new ArrayList<T>();
        }
        return result;
    }

    /**
     * This method returns a Vector containing the elements of vectA minus the elements of vectB. If vectA has an element in the Vector 5 times and vectB has it 3 times, the result will have it two times. This method will always return a new, non-null Vector, even if vectA and/or vectB are null.
     */
    public static Vector minus(Vector vectA, Vector vectB) {
        Vector result = new Vector();
        Enumeration aTempEnum;
        Object item;
        /* -- */
        if (vectA == null) {
            return result;
        }
        result = (Vector) vectA.clone();
        if (vectB != null) {
            aTempEnum = vectB.elements();
            while (aTempEnum.hasMoreElements()) {
                item = aTempEnum.nextElement();
                if (result.contains(item)) {
                    result.removeElement(item);
                }
            }
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

    /**
     * This method takes a sepChars-separated string and converts it to a vector of fields. i.e., "gomod,jonabbey" -> a vector whose elements are "gomod" and "jonabbey". NOTE: this method will omit 'degenerate' fields from the output vector. That is, if input is "gomod,,, jonabbey" and sepChars is
     * ", ", then the result vector will still only have "gomod" and "jonabbey" as elements, even though one might wish to explicitly know about the blanks between commas. This method is intended mostly for creating email list vectors, rather than general file-parsing vectors.
     *
     * @param input
     *            the sepChars-separated string to test.
     * @param sepChars
     *            a string containing a list of characters which may occur as field separators. Any two fields in the input may be separated by one or many of the characters present in sepChars.
     */
    public static Vector stringVector(String input, String sepChars) {
        Vector results = new Vector();
        int index = 0;
        int oldindex = 0;
        String temp;
        char inputAry[] = input.toCharArray();
        /* -- */
        while (index != -1) {
            // skip any leading field-separator chars
            for (; oldindex < input.length(); oldindex++) {
                if (sepChars.indexOf(inputAry[oldindex]) == -1) {
                    break;
                }
            }
            if (oldindex == input.length()) {
                break;
            }
            index = findNextSep(input, oldindex, sepChars);
            if (index == -1) {
                temp = input.substring(oldindex);
                // System.err.println("+ " + temp + " +");
                results.addElement(temp);
            } else {
                temp = input.substring(oldindex, index);
                // System.err.println("* " + temp + " *");
                results.addElement(temp);
                oldindex = index + 1;
            }
        }
        return results;
    }

    /**
     * findNextSep() takes a string, a starting position, and a string of characters to be considered field separators, and returns the first index after startDex whose char is in sepChars. If there are no chars in sepChars past startdex in input, findNextSep() returns -1.
     */
    private static int findNextSep(String input, int startDex, String sepChars) {
        int currentIndex = input.length();
        char sepAry[] = sepChars.toCharArray();
        boolean foundSep = false;
        /* -- */
        // find the next separator
        for (int i = 0; i < sepAry.length; i++) {
            int tempdex = input.indexOf(sepAry[i], startDex);
            if (tempdex > -1 && tempdex <= currentIndex) {
                currentIndex = tempdex;
                foundSep = true;
            }
        }
        if (foundSep) {
            return currentIndex;
        } else {
            return -1;
        }
    }

    public static void main(String[] args) {
        // String testString = "jon, beth ross,,,darren,anna";
        // String testString = "jon, beth ross,,,darren,anna,,,,,";
        // Vector results = stringVector(testString, ", ");
        // for (int i = 0; i < results.size(); i++) {
        // System.out.println(i + ": " + results.elementAt(i));
        String[] testa = new String[] { "1" };
        String[] test2 = new String[] { "1" };
        System.out.println(testa.equals(test2));
        // }
        Vector a = new Vector();
        a.add("1");
        a.add("2");
        a.add("3");
        a.add("4");
        a.add("5");
        a.add("8");
        a.add("6");
        a.add("7");
        a.add("8");
        a.add("9");
        a.add("10");
        a.add("11");
        // removeDuplicatesLastWins2(a);
        sublist(a, a.get(3), true);
        removeLast(a, 3);
        keepEvery(a, 4);
        removeDuplicatesLastWins(a);
        Vector b = new Vector();
        b.add("a");
        b.add("b");
        b.add("c");
        b.add("d");
        sameSequence(a, b);
        Vector as = new Vector();
        as.add("a");
        as.add("b");
        as.add("c");
        ;
        b = new Vector();
        b.add("a");
        b.add("b");
        b.add("c");
        b.add("d");
        sameSequence(as, b);
        a = new Vector();
        a.add("a");
        a.add("b");
        a.add("c");
        a.add("d");
        Vector bs = new Vector();
        bs.add("a");
        bs.add("b");
        bs.add("c");
        bs.add(null);
        System.out.println(sameSequence(a, bs).stream().collect(
                Collectors.joining(",")));
        Vector an = new Vector();
        an.add("a");
        an.add(null);
        an.add("c");
        an.add("d");
        Vector bn = new Vector();
        bn.add("a");
        bn.add("b");
        bn.add("c");
        System.out.println(sameSequence(an, bn).stream().collect(
                Collectors.joining(",")));
    }

    public static List getStartsWith(List array, String sword) {
        List result = new ArrayList(5);
        for (int a = 0; a < array.size(); a++) {
            Object aObj = array.get(a);
            if (aObj != null && aObj.toString().startsWith(sword)) {
                result.add(aObj);
            }
        }
        return result;
    }

    public static List getValueStartsWith(List array, String sword) {
        List result = new ArrayList(5);
        for (int a = 0; a < array.size(); a++) {
            Object aObj = array.get(a);
            if (aObj != null && sword.startsWith(aObj.toString())) {
                result.add(aObj);
            }
        }
        return result;
    }

    /**
     * Filters all the elements given in the filter list out of the base list. This is used for filtering a list which contains an array as entry. (e.g. a string array)
     *
     * @author dwachter
     * @param base
     *            list which defines the base to be filterd, type of list must be a type which extends Object[]
     * @param filter
     *            list which holds the values to filter out of the base list, type of list must be a type which extends Object[]
     * @return returns a list of the same type as given for the base and filter
     */
    public static <T> List<T> filterOut(List<? extends Object[]> base, List<? extends Object[]> filter) {
        int rLength = base.size();
        if (rLength <= 0) {
            return null;
        }
        List foundElems = new ArrayList();
        boolean found = false;
        for (int i = 0; i < rLength; i++) {
            Object[] row = base.get(i);
            found = false;
            for (int d = 0; d < filter.size(); d++) {
                Object[] destRow = filter.get(d);
                if (ArrayUtil.compare(row, destRow)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                foundElems.add(row);
            }
        }
        return foundElems;
    }

    public static List getEndsWith(List array, String sword) {
        List result = new ArrayList(5);
        for (int a = 0; a < array.size(); a++) {
            Object aObj = array.get(a);
            if (aObj != null && aObj.toString().endsWith(sword)) {
                result.add(aObj);
            }
        }
        return result;
    }

    public static List getValueEndsWith(List array, String sword) {
        List result = new ArrayList(5);
        for (int a = 0; a < array.size(); a++) {
            Object aObj = array.get(a);
            if (aObj != null && sword.endsWith(aObj.toString())) {
                result.add(aObj);
            }
        }
        return result;
    }

    public static List getContainsEquals(List array, String sword) {
        List result = new ArrayList(5);
        for (int a = 0; a < array.size(); a++) {
            Object aObj = array.get(a);
            if (aObj != null && aObj.toString().equals(sword)) {
                result.add(aObj);
            }
        }
        return result;
    }

    public static List getContains(List array, String sword) {
        List result = new ArrayList(5);
        for (int a = 0; a < array.size(); a++) {
            Object aObj = array.get(a);
            if (aObj != null && aObj.toString().indexOf(sword) >= 0) {
                result.add(aObj);
            }
        }
        return result;
    }

    public static List getValueContains(List array, String sword) {
        List result = new ArrayList(5);
        for (int a = 0; a < array.size(); a++) {
            Object aObj = array.get(a);
            if (aObj != null && sword.indexOf(aObj.toString()) >= 0) {
                result.add(aObj);
            }
        }
        return result;
    }


    public static List sublist(List aList, Object startObj, boolean preceding) {
        int from = aList.indexOf(startObj);
        if (from != -1) {
            if (preceding) {
                return aList.subList(0, from);
            } else {
                return aList.subList(from + 1, aList.size());
            }
        }
        return null;
    }


    public static <T> T get(List<T> aList, int i) {
        if (aList != null && i >= 0 && i < aList.size()) {
            return aList.get(i);
        } else {
            return null;
        }
    }
}
