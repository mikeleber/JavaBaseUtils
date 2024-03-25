package org.basetools.util.collection.result;

import org.basetools.util.StringUtils;
import org.basetools.util.array.ArrayUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultListImpl extends ResultList<Object[]> implements IResult<Object[]> {
    private List<String> columnNames;
    private List<String> columnDataTypes;
    private int resultColumnCount = -1;

    public ResultListImpl() {
        this(new ArrayList(), null);
    }

    public ResultListImpl(int resultColumnCount) {
        this(createEmpty(resultColumnCount), null);
    }

    public ResultListImpl(List<String> colNames, Collection columnData) {
        super();
        resultColumnCount = colNames.size();
        columnNames = colNames;
        if (columnData != null) {
            Iterator valueIterator = columnData.iterator();
            while (valueIterator.hasNext()) {
                Object object = valueIterator.next();
                if (object != null) {
                    if (object instanceof Object[]) {
                        add((Object[]) object);
                    } else if (object instanceof List) {
                        add(((List) object).toArray());
                    } else {
                        add(new Object[]{object});
                    }
                } else {
                    add(new Object[resultColumnCount]);
                }
            }
        }
        columnDataTypes = createEmpty(colNames.size());
    }

    public ResultListImpl ResultListImpl(Collection col) {
        if (col != null) {
            Iterator valueIterator = col.iterator();
            while (valueIterator.hasNext()) {
                Object object = valueIterator.next();
                if (object != null) {
                    if (object instanceof Object[]) {
                        add((Object[]) object);
                    } else if (object instanceof List) {
                        add(((List) object).toArray());
                    } else {
                        add(new Object[]{object});
                    }
                } else {
                    add(new Object[resultColumnCount]);
                }
            }
        }
        return this;
    }

    public ResultListImpl withColNames(String... colNames) {
        resultColumnCount = colNames.length;
        columnNames = Stream.of(colNames).collect(Collectors.toList());
        columnDataTypes = createEmpty(colNames.length);
        return this;
    }
    public ResultListImpl withData(Object[][] data) {
        if (data != null) {
            for (int d = 0; d < data.length; d++) {
                add(data[d]);
            }
        }
        return this;
    }

    private static List<String> createEmpty(int resultColumnCount) {
        ArrayList colNames = new ArrayList();
        for (int i = 0; i < resultColumnCount; i++) {
            colNames.add(null);
        }
        return colNames;
    }

    @Override
    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] names) {
        setColumnNames((names != null ? Arrays.asList(names) : null));
    }

    public void setColumnNames(List<String> names) {
        columnNames = names;
        setResultColumnCount(names != null ? names.size() : 0);
    }

    @Override
    public void setColumnName(int index, String name) {

        if (index >= 0 && index < columnNames.size()) {
            columnNames.set(index, name);
        }
    }

    @Override
    public void setColumnDataType(int index, String dataType) {

        if (index >= 0 && index < columnNames.size()) {
            columnDataTypes.set(index, dataType);
        }
    }

    @Override
    public List<String> getColumnDataTypes() {
        return columnDataTypes;
    }

    @Override
    public void setColumnDataTypes(List<String> columnDataTypes) {
        this.columnDataTypes = columnDataTypes;
    }

    @Override
    public <T, U> void setColumnDataTypes(List<T> from, Function<T, U> func) {

        columnDataTypes = (List<String>) convertList(from, func);
    }

    @Override
    public <T, U> void setColumnNames(List<T> from, Function<T, U> func) {
        setColumnNames((List<String>) convertList(from, func));
    }

    private <T, U> Object convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    private void setResultColumnCount(int resultColumnCount) {
        this.resultColumnCount = resultColumnCount;
    }

    @Override
    public int getColumnForName(String name) {
        if (name != null && columnNames != null) {
            for (int c = 0; c < columnNames.size(); c++) {
                if (name.equalsIgnoreCase(columnNames.get(c))) {
                    return c;
                }
            }
        }
        return -1;
    }

    public void removeDuplicates(int[] idColumnPositions) {
        int rLength = size();
        if (rLength > 0) {
            List content = new ArrayList(rLength);
            HashMap duplicateCheckMap = new HashMap(rLength);
            for (int i = 0; i < rLength; i++) {
                Object[] row = get(i);
                String key = createRowKey(idColumnPositions, row);
                if (duplicateCheckMap.containsKey(key)) {
                } else {
                    content.add(row);
                    duplicateCheckMap.put(key, "");
                }
            }
            clear();
            addAll(content);
        }
    }

    public static String createRowKey(int[] posses, Object[] row) {
        return (posses == null ? StringUtils.toString(row, ",") : toString(posses, row, ","));
    }

    @Override
    public String getColumnName(int col) {
        if (columnNames != null) {
            if (col >= 0 && col < columnNames.size()) {
                return columnNames.get(col);
            }
        }
        return null;
    }

    @Override
    public String getDataType(int col) {
        if (columnDataTypes != null && col >= 0 && col < columnDataTypes.size()) {
            return columnDataTypes.get(col);
        } else {
            return null;
        }
    }

    @Override
    public String getDataType(String colName) {
        return getDataType(getColumnForName(colName));
    }

    @Override
    public int getColCount() {
        return resultColumnCount;
    }

    @Override
    public Object[] getRow(int row) {
        return get(row);
    }

    @Override
    public <T>T getValue(int row, String colName) {
        return getValue(row, colName, null);
    }

    @Override
    public <T> T getValue(int row, String colName, T defaultValue) {
        if (colName == null) {
            return null;
        }
        if (row < 0 || row >= size()) {
            return (T) defaultValue;
        }
        int col = getColumnForName(colName);
        return (T)getValue(row, col, defaultValue);
    }

    @Override
    public <T> T getValue(int row, int col, T defaultValue) {
        if (row < 0 || row >= size()) {
            return (T) defaultValue;
        }
        return (T) ArrayUtil.get(getRow(row), col, defaultValue);
    }

    @Override
    public void setValue(int row, int col, Object value) {
        Object[] aRow = get(row);
        if (col >= 0) {
            aRow[col] = value;
        }
    }

    /**
     * Adds a new empty row at the given position. If pos == -1 add row to the end
     *
     * @param pos
     * @return
     */
    @Override
    public Object[] addRow(int pos) {
        Object[] rowData = new Object[getColCount()];
        if (pos != -1) {
            if (pos >= 0 && pos < size()) {
                add(pos, rowData);
            } else {
                add(rowData);
            }
        } else {
            add(rowData);
        }
        return rowData;
    }

    @Override
    public Object[] addRow() {
        Object[] rowData = new Object[getColCount()];
        add(rowData);
        return rowData;
    }

    @Override
    public Object[] addRow(Object[] rowData) {
        add(rowData);
        return rowData;
    }

    @Override
    public Object[] addRow(List rowData) {
        Object[] toAdd = rowData.toArray();
        add(toAdd);
        return toAdd;
    }

    @Override
    public void releaseResult() {
        clear();
    }

    @Override
    public Object getValue(int row, int col) {
        if (col == -1) {
            return null;
        }
        Object[] aRow = get(row);
        return aRow[col];
    }

    public void merge(IResult with, int[] idCols, boolean zeroOnly) {
        if (with == null || with.size() == 0) {
            return;
        }
        if (size() == 0 || idCols == null || idCols.length == 0) {
            addAll(with);
        } else {
            ArrayList tempList = new ArrayList();
            for (int i = 0; i < with.size(); i++) {
                String[] withRow = (String[]) with.getRow(i);
                String fkey = (idCols != null ? toString(idCols, withRow, ",") : null);
                boolean found = false;
                for (int r = 0; r < size(); r++) {
                    String[] thisRow = (String[]) get(r);
                    String tKey = toString(idCols, thisRow, ",");
                    if (tKey.equals(fkey)) {
                        // merge where zero or null
                        if (zeroOnly) {
                            ArrayUtil.merge(thisRow, withRow);
                        } else {
                            remove(r);
                            add(r, withRow);
                        }
                        found = true;
                    }
                }
                if (!found) {
                    // add it to temp!
                    tempList.add(withRow);
                }
            }
            addAll(tempList);
        }
    }

    public static final String toString(int[] itemsPos, Object[] list, String delim) {
        return toString(itemsPos, list, delim, null);
    }

    public static final String toString(String delim, String quali, String prefix, String postfix, Object... parts) {
        StringBuilder buindings = new StringBuilder();
        if (prefix != null) {
            buindings.append(prefix);
        }
        for (int i = 0; i < parts.length; i++) {
            if (quali != null) {
                buindings.append(quali);
            }
            buindings.append(parts[i]);
            if (quali != null) {
                buindings.append(quali);
            }
            if (delim != null) {
                if (i + 1 < parts.length) {
                    buindings.append(delim);
                }
            }
        }
        if (postfix != null) {
            buindings.append(postfix);
        }
        return buindings.toString();
    }

    public static final String toString(int[] itemsPos, Object[] list, String delim, String qualifier) {
        int s = itemsPos.length;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s; i++) {
            if (qualifier != null) {
                result.append(qualifier);
            }
            result.append(list[itemsPos[i]]);
            if (qualifier != null) {
                result.append(qualifier);
            }
            if (i < (s - 1) && delim != null) {
                result.append(delim);
            }
        }
        return result.toString();
    }

    public void shrinkToLast(int[] idCols) {
        if (idCols == null || idCols.length == 0) {
            return;
        }
        for (int i = size() - 1; i >= 0; i--) {
            String[] withRow = (String[]) get(i);
            String fkey = (idCols.length == 1 ? withRow[idCols[0]] : toString(idCols, withRow, ","));
            int r = i - 1;
            for (; r >= 0; r--) {
                String[] thisRow = (String[]) get(r);
                String tKey = (idCols.length == 1 ? thisRow[idCols[0]] : toString(idCols, thisRow, ","));
                if (tKey.equals(fkey)) {
                    remove(r);
                    r--;
                    i--;
                }
            }
        }
    }
}
