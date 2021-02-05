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

import com.bewsoftware.common.InvalidParameterValueException;

/**
 * Stores {@link TableRow} objects for attribute re-use in subsequent rows
 * that do not have any attributes set.
 *
 * @author Bradley Willcott
 */
class TableRowList {

    @Deprecated
    private static final int DEFAULT_SIZE = 10;

    private int lastIdx = -1;
    private int length = 0;
    private final TableRow[] list;

    /**
     * Sets default list size of 10.
     *
     * @deprecated To be removed from future version.
     */
    @Deprecated
    public TableRowList() {
        list = new TableRow[DEFAULT_SIZE];
    }

    /**
     * Instantiate class with storage capacity of: {@code size}.
     *
     * @param size Maximum number entries that can be stored.
     */
    public TableRowList(int size) {
        if (size > 0)
        {
            list = new TableRow[size];
        } else
        {
            throw new InvalidParameterValueException("Size must be greater than zero.");
        }
    }

    /**
     * Stores {@link TableRow} instances that have attributes set.
     * <p>
     * A {@code TableRow} instance that has its borderWidth set to '0'(zero),
     * will cause this list to be reset to empty. This provides a means of
     * turning off the automatic re-use of previous rows attribute settings,
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
     * Returns the next available entry. Rotates to the first entry when the end
     * of the list is reached.
     *
     * @return next available TableRow.
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
     * Checks whether or not there is another entry.
     *
     * @return {@code true} if there is, {@code false} otherwise.
     */
    public boolean hasNext() {
        return length > 0;
    }

    /**
     * The number of entries available.
     *
     * @return number of available TableRow objects.
     */
    public int length() {
        return length;
    }

    /**
     * Reset list.
     */
    private void reset() {
        length = 0;
        lastIdx = -1;
    }
}
