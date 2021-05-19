package org.basetools.util.collection.result;

import org.basetools.util.collection.ArrayUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResultListImpl extends ResultList<Object[]> implements IResult {
    private List<String> columnNames;
    private List<String> columnDataTypes;
    private int resultColumnCount = -1;

    public ResultListImpl() {

        this(new ArrayList(), null);
    }

    public ResultListImpl(int resultColumnCount) {

        this(createEmpty(resultColumnCount), null);
    }

    public ResultListImpl(List<String> colNames, Collection col) {
        super();
        resultColumnCount = colNames.size();
        columnNames = colNames;
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
        columnDataTypes = createEmpty(colNames.size());
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
    public Object getValue(int row, String colName) {
        return getValue(row, colName, null);
    }

    @Override
    public Object getValue(int row, String colName, Object defaultValue) {
        if (colName == null) {
            return null;
        }
        if (row < 0 || row >= size()) {
            return defaultValue;
        }
        int col = getColumnForName(colName);
        return getValue(row, col, defaultValue);
    }

    @Override
    public Object getValue(int row, int col, Object defaultValue) {
        if (row < 0 || row >= size()) {
            return defaultValue;
        }
        return ArrayUtil.get(getRow(row), col, defaultValue);
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
}
