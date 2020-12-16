/*
 * Copyright (c) 2020, Bradley Willcott.
 * <http://www.bewsoftware.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  - Neither the name "Markdown" nor the names of its contributors may
 *    be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as
 * is" and any express or implied warranties, including, but not limited
 * to, the implied warranties of merchantability and fitness for a
 * particular purpose are disclaimed. In no event shall the copyright owner
 * or contributors be liable for any direct, indirect, incidental, special,
 * exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or
 * profits; or business interruption) however caused and on any theory of
 * liability, whether in contract, strict liability, or tort (including
 * negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 */
package com.bewsoftware.mdj.core;

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
        if (size > 0)
        {
            list = new TableRow[size];
        } else
        {
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
        if (row.hasAttrib())
        {
            if (row.getBorderWidth() == 0)
            {
                row.clearAttributes();
                reset();
            } else
            {
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
        if (hasNext())
        {
            if (++lastIdx == length)
            {
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
