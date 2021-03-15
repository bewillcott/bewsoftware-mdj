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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.MarkdownProcessor.CLASS_REGEX;
import static com.bewsoftware.mdj.core.MarkdownProcessor.ID_REGEX_OPT;
import static java.lang.Integer.parseInt;

/**
 * Holds all the information parsed from, and about, a table row.
 *
 * @author Bradley Willcott
 */
class TableRow {

    private static final int DEFAULT_BORDERWIDTH = 1;
    private static final int DEFAULT_CELLPADDING = 5;

    /**
     * Parses the row text, breaking out and storing all its components.
     *
     * @param text The text of the row to parse and store.
     *
     * @return A fully configured new instance of this class.
     */
    public static TableRow parse(final String text) {
        TableRow tr = new TableRow(text);
        String data = text.substring(1).trim();
        tr.cells = data.split("\\|");
        tr.length = tr.cells.length;

        //
        // Last 'cell' may be the bracketed attributes
        //
        String attrib = tr.cells[tr.length - 1];

        if (attrib.startsWith("[") && attrib.endsWith("]"))
        {
            if (attrib.length() == 2)
            {
                tr.status.setBorder();
            } else
            {
                tr.classes = attrib;
                tr.status.setClasses();
            }
        }

        //
        // If 'row' has attributes
        //
        if (tr.status.hasAttribute())
        {
            tr.length -= 1;

            //
            // If 'row' has claases
            //
            if (tr.status.hasClasses())
            {
                //
                // If is number, then it is border settings
                //
                if (tr.classes.substring(1, attrib.length() - 1).trim().matches("^\\d+$"))
                {
                    tr.borderWidth = parseInt(tr.classes.substring(1, attrib.length() - 1).trim());
                    tr.status.setBorder();
                } else
                {
                    //
                    // Check for 'id' attribute
                    //
                    Matcher m = Pattern.compile("^" + ID_REGEX_OPT + "$").matcher(tr.classes);

                    if (m.find())
                    {
                        tr.id = m.group("id");
                        tr.status.setId();
                        tr.status.unsetClasses();

                    } else
                    {
                        //
                        // Check for 'id' with border settings
                        //
                        Matcher m2 = Pattern.compile("^" + ID_REGEX_OPT
                                                     + "\\[(?<border>(?<borderWidth>\\d+)"
                                                     + "(?:(?:[, ][ ]*)(?<cellPadding>\\d+))?)?\\]$")
                                .matcher(tr.classes);

                        if (m2.find())
                        {
                            tr.id = m2.group("id");

                            if (tr.id != null && !tr.id.isBlank())
                            {
                                tr.status.setId();
                            }

                            String border = m2.group("border");

                            if (border == null)
                            {
                                tr.status.setBorder();
                            } else
                            {
                                tr.borderWidth = parseInt(m2.group("borderWidth"));
                                String cellPadding = m2.group("cellPadding");

                                if (cellPadding != null)
                                {
                                    tr.cellPadding = parseInt(cellPadding);
                                }

                                tr.status.setBorder();
                            }
                        } else
                        {
                            //
                            // Check for 'id' and 'class' attributes.
                            //
                            Matcher m3 = Pattern.compile("^" + ID_REGEX_OPT + CLASS_REGEX + "$").matcher(tr.classes);

                            if (m3.find())
                            {
                                tr.id = m3.group("id");

                                if (tr.id != null && !tr.id.isBlank())
                                {
                                    tr.status.setId();
                                }

                                tr.classes = m3.group("classes");
                            } else
                            {
                                tr.status.unsetClasses();
                            }
                        }
                    }
                }
            }
        }

        return tr;
    }

    /**
     * The raw unprocessed row text.
     */
    public final String text;

    private int borderWidth = -1;
    private int cellPadding = -1;
    private String[] cells;
    private String classes = null;
    private String id = "";
    private int length = 0;
    private boolean readOnly = false;
    private final Status status = new Status();

    // Restrict instantiation to the static method: parse()
    private TableRow(String text) {
        this.text = text;
    }

    /**
     *
     * @return Either the valued parsed from the row text, or the default value.
     */
    public int getBorderWidth() {
        return hasBorderWidth() ? borderWidth : DEFAULT_BORDERWIDTH;
    }

    /**
     *
     * @param index Must be: {@code 0 <= index < length}
     *
     * @return Contents of the indexed cell.
     */
    public String getCell(int index) {
        if (index >= 0 && index < length)
        {
            return cells[index];
        } else
        {
            throw new IndexOutOfBoundsException(index);
        }
    }

    /**
     *
     * @return Either the valued parsed from the row text, or the default value.
     */
    public int getCellPadding() {
        return hasCellPadding() ? cellPadding : DEFAULT_CELLPADDING;
    }

    /**
     * Returns the text of the classes parsed from the row text.
     * <p>
     * You might want to test {@link #hasClasses()} before calling this method.
     * </p>
     *
     * @return Either the classes text, or {@code null} if not set.
     */
    public String getClasses() {
        return hasClasses() ? classes : null;
    }

    /**
     * Returns the text of the id parsed from the row text.
     * <p>
     * You might want to test {@link #hasId()} before calling this method.
     * </p>
     *
     * @return Either the id text, or {@code null} if not set.
     */
    public String getId() {
        return hasId() ? id : null;
    }

    /**
     *
     * @return {@code true} if attributes stored.
     */
    public boolean hasAttrib() {
        return status.hasAttribute();
    }

    /**
     *
     * @return {@code true} if the border attribute parsed from row text.
     */
    public boolean hasBorder() {
        return status.hasBorder();
    }

    /**
     *
     * @return {@code true} if borderWidth attribute parsed from row text.
     */
    public boolean hasBorderWidth() {
        return hasBorder() && borderWidth > -1;
    }

    /**
     *
     * @return {@code true} if cellPadding attribute parsed from row text.
     */
    public boolean hasCellPadding() {
        return hasBorder() && cellPadding > -1;
    }

    /**
     *
     * @return {@code true} if one or more class attributes were parsed from row text.
     */
    public boolean hasClasses() {
        return status.hasClasses();
    }

    /**
     *
     * @return {@code true} if id was parsed from row text.
     */
    public boolean hasId() {
        return status.hasId();
    }

    /**
     * This is the number of columns within this row.
     *
     * @return The number of columns.
     */
    public int length() {
        return length;
    }

    /**
     * Sets the contents of the cell at index.
     *
     * @param index The cell[index].
     * @param text  The text to store.
     *
     * @return {@code true} if stored, {@code false} otherwise.
     */
    public boolean setCell(int index, String text) {
        boolean rtn = false;

        if (!readOnly)
        {
            if (index >= 0 && index < length)
            {
                cells[index] = text;
                rtn = true;
            } else
            {
                throw new IndexOutOfBoundsException(index);
            }
        }

        return rtn;
    }

    /**
     * Makes this instance immutable.
     * <p>
     * <b>WARNING:</b> This cannot be undone!
     * </p>
     */
    public void setReadOnly() {
        readOnly = true;
    }

    /**
     * Used by {@link TableRowList#add(TableRow) TableRowList.add(row)} when the row's borderWidth
     * attribute is set to '0' (zero).
     */
    void clearAttributes() {
        if (!readOnly)
        {
            status.clear();

        }
    }

    /**
     * Holds the current status information for: Border, Classes, and Id.
     */
    private class Status {

        private static final int BORDER = 1;
        private static final int CLASSES = 4;
        private static final int ID = 2;
        private int status = 0;

        private Status() {
        }

        public void clear() {
            status = 0;
        }

        public boolean hasAttribute() {
            return status > 0;
        }

        public boolean hasBorder() {
            return (status & BORDER) > 0;
        }

        public void setBorder() {
            status |= BORDER;
            unsetClasses();
        }

        public void unsetBorder() {
            if (hasBorder())
            {
                status ^= BORDER;
            }
        }

        public boolean hasClasses() {
            return (status & CLASSES) > 0;
        }

        public void setClasses() {
            status |= CLASSES;
            unsetBorder();
        }

        public void unsetClasses() {
            if (hasClasses())
            {
                status ^= CLASSES;
            }
        }

        public boolean hasId() {
            return (status & ID) > 0;
        }

        public void setId() {
            status |= ID;
        }

        public void unsetId() {
            if (hasId())
            {
                status ^= ID;
            }
        }
    }
}
