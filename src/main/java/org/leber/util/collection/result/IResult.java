package org.leber.util.collection.result;

import java.util.List;
import java.util.function.Function;

/**
 * The interface Result.
 */
public interface IResult {

    /**
     * Gets column names.
     *
     * @return the column names
     */
    List<String> getColumnNames();

    /**
     * Sets column name.
     *
     * @param index the index
     * @param name  the name
     */
    void setColumnName(int index, String name);

    /**
     * Sets column data type.
     *
     * @param index    the index
     * @param dataType the data type
     */
    void setColumnDataType(int index, String dataType);

    /**
     * Gets column data types.
     *
     * @return the column data types
     */
    List<String> getColumnDataTypes();

    /**
     * Sets column data types.
     *
     * @param columnDataTypes the column data types
     */
    void setColumnDataTypes(List<String> columnDataTypes);

    /**
     * Sets column data types.
     *
     * @param <T>  the type parameter
     * @param <U>  the type parameter
     * @param from the from
     * @param func the func
     */
    <T, U> void setColumnDataTypes(List<T> from, Function<T, U> func);

    /**
     * Sets column names.
     *
     * @param <T>  the type parameter
     * @param <U>  the type parameter
     * @param from the from
     * @param func the func
     */
    <T, U> void setColumnNames(List<T> from, Function<T, U> func);

    /**
     * Gets column for name.
     *
     * @param name the name
     * @return the column for name
     */
    int getColumnForName(String name);

    /**
     * Gets column name.
     *
     * @param col the col
     * @return the column name
     */
    String getColumnName(int col);

    /**
     * Gets col count.
     *
     * @return the col count
     */
    int getColCount();

    /**
     * Get row object [ ].
     *
     * @param row the row
     * @return the object [ ]
     */
    Object[] getRow(int row);

    /**
     * Gets value.
     *
     * @param row     the row
     * @param colName the col name
     * @return the value
     */
    Object getValue(int row, String colName);

    /**
     * Gets value.
     *
     * @param row          the row
     * @param colName      the col name
     * @param defaultValue the default value
     * @return the value
     */
    Object getValue(int row, String colName, Object defaultValue);

    /**
     * Gets value.
     *
     * @param row          the row
     * @param col          the col
     * @param defaultValue the default value
     * @return the value
     */
    Object getValue(int row, int col, Object defaultValue);

    /**
     * Sets value.
     *
     * @param row   the row
     * @param col   the col
     * @param value the value
     */
    void setValue(int row, int col, Object value);

    /**
     * Add row object [ ].
     *
     * @param pos the pos
     * @return the object [ ]
     */
    Object[] addRow(int pos);

    /**
     * Add row object [ ].
     *
     * @return the object [ ]
     */
    Object[] addRow();

    /**
     * Add row object [ ].
     *
     * @param rowData the row data
     * @return the object [ ]
     */
    Object[] addRow(Object[] rowData);

    /**
     * Add row object [ ].
     *
     * @param rowData the row data
     * @return the object [ ]
     */
    Object[] addRow(List rowData);

    /**
     * Release result.
     */
    void releaseResult();

    /**
     * Gets value.
     *
     * @param row the row
     * @param col the col
     * @return the value
     */
    Object getValue(int row, int col);

    /**
     * @param col
     * @return
     */
    public String getDataType(int col);

    /**
     * @param colName
     * @return
     */
    public String getDataType(String colName);
}
