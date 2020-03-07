/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.markdownj;

/**
 * Stores {@link TableRow} objects for attribute reuse in subsequent rows
 * that do not have any attributes set.
 *
 * @author Bradley Willcott
 */
class TableRowList {

    private static final int DEFAULT_SIZE = 10;
    private TableRow[] list;
    private int length = 0;
    private int lastIdx = -1;

    /**
     * Sets default list size of 10.
     */
    public TableRowList() {
        list = new TableRow[DEFAULT_SIZE];
    }

    /**
     *
     * @param size Maximum number entries that can be stored in list.
     */
    public TableRowList(int size) {
        if (size > 0) {
            list = new TableRow[size];
        } else {
            list = new TableRow[DEFAULT_SIZE];
        }
    }

    /**
     * Stores {@link TableRow} instances that have attributes set.
     * <p>
     * A {@code TableRow} instance that has its borderWidth set to '0'(zero),
     * will cause this list to be reset to empty. This provides a means of
     * turning off the automatic reuse of previous rows' attribute settings,
     * for rows that don't have any set.
     * </p>
     *
     * @param row Row to add to list.
     */
    public void add(TableRow row) {
        if (row.hasAttrib()) {
            if (row.getBorderWidth() == 0) {
                row.clearAttributes();
                reset();
            } else {
                list[length++] = row;
            }
        }

        row.setReadOnly();
    }

    /**
     *
     * @return Next available TableRow.
     */
    public TableRow getNext() {
        if (hasNext()) {
            if (++lastIdx == length) {
                lastIdx = 0;
            }

            return list[lastIdx];
        }

        return null;
    }

    /**
     *
     * @return True if there is atleast one TableRow in list.
     */
    public boolean hasNext() {
        return length > 0;
    }

    /**
     *
     * @return Number of available TableRow objects in list.
     */
    public int length() {
        return length;
    }

    private void reset() {
        length = 0;
        lastIdx = -1;
    }
}
