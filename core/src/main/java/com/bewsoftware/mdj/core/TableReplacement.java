/*
 * Copyright (c) 2005, Martian Software
 * Authors: Pete Bevin, John Mutchek
 * http://www.martiansoftware.com/markdownj
 *
 * Copyright (c) 2020 Bradley Willcott
 * Modifications to the code.
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

import static com.bewsoftware.mdj.core.Attributes.addClass;
import static com.bewsoftware.mdj.core.Attributes.addId;
import static com.bewsoftware.mdj.core.Attributes.addStyle;
import static com.bewsoftware.mdj.core.MarkdownProcessor.CLASS_REGEX;
import static com.bewsoftware.mdj.core.MarkdownProcessor.HTML_PROTECTOR;
import static com.bewsoftware.mdj.core.MarkdownProcessor.processGroupText;
import static java.util.regex.Pattern.compile;

/**
 * TableReplacement class moved out from {@link MarkdownProcessor} class.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
class TableReplacement implements Replacement {

    private static final String CAPTION_BORDER = "border-left: %1$dpx solid black;border-top: %1$dpx solid black;border-right: %1$dpx solid black;padding: %2$dpx";
    private static final String[] INDENT =
    {
        "", "  ", "    ", "      "
    };
    private static final String ROW_BORDER = "border: %1$dpx solid black;padding: %2$dpx;";
    private static final String TABLE_BORDER = "border: %1$dpx solid black;border-collapse: collapse;padding: %2$dpx";

    TableReplacement() {
    }

    @Override
    public String replacement(Matcher m) {

        String rtn = m.group();

        String caption = m.group("caption");
        String header = processGroupText(m.group("header")).trim();
        String delrow = m.group("delrow").trim();
        String data = processGroupText(m.group("datarows")).trim();

        //
        // Build <table>.
        //
        // Parse the 'header': hRow.
        //
        TableRow hRow = TableRow.parse(header);
        hRow.setReadOnly();
        TableRow delRow = TableRow.parse(delrow);

        int align = 0;
        String tmp = "";

        if (hRow.length() == delRow.length())
        {
            StringBuilder sb = new StringBuilder();

            sb.append("<table");

            if (delRow.hasAttrib())
            {
                if (delRow.hasId())
                {
                    sb.append(addId(delRow.getId()));
                }

                if (delRow.hasBorder())
                {
                    tmp = String.format(TABLE_BORDER, delRow.getBorderWidth(), delRow.getCellPadding());
                    sb.append(addStyle(tmp));
                } else
                {
                    sb.append(addClass(delRow.getClasses()));
                }
            }

            sb.append(">\n");

            //
            // Add <caption>, if any.
            //
            if (caption != null && !caption.isBlank())
            {
                boolean captionBorders = false;
                sb.append(INDENT[1]).append("<caption");
                caption = processGroupText(caption.trim());

                if (caption.startsWith("[") && caption.endsWith("]"))
                {
                    captionBorders = true;
                    caption = caption.substring(1, caption.length() - 1).trim();
                } else
                {
                    //
                    // Process caption looking for classes.
                    //
                    TableCaption tc = new TableCaption(caption);

                    if (tc.hasClasses())
                    {
                        caption = tc.caption;
                        sb.append(addClass(tc.classes));
                    }
                }

                //
                // Caption borders and attributes.
                //
                if (captionBorders && delRow.hasAttrib())
                {
                    if (delRow.hasBorder())
                    {
                        tmp = String.format(CAPTION_BORDER, delRow.getBorderWidth(), delRow.getCellPadding());
                        sb.append(addStyle(tmp));
                    }
                }

                sb.append(">\n").append(caption).append("\n")
                        .append(INDENT[1]).append("</caption>\n");
            }

            //
            // Process column formatting.
            //
            // Text alignment.
            //
            for (int i = 0; i < delRow.length(); i++)
            {
                String delCol = delRow.getCell(i).trim();
                align = (delCol.startsWith(":") ? 1 : 0);
                align = align + (delCol.endsWith(":") ? 2 : 0);

                switch (align)
                {
                    case 0:

                        if (delCol.matches("[-]+?[:][-]+?"))
                        {
                            delRow.setCell(i, "text-align: center");
                        } else
                        {
                            delRow.setCell(i, "");
                        }

                        break;

                    case 1:
                        delRow.setCell(i, "text-align: left");
                        break;

                    case 2:
                        delRow.setCell(i, "text-align: right");
                        break;

                    case 3:
                        delRow.setCell(i, "text-align: justify");
                        break;

                    default:
                        break;
                }
            }

            delRow.setReadOnly();

            //
            // Process <head>.
            //
            sb.append(INDENT[1]).append("<thead>\n")
                    .append(INDENT[2]).append("<tr");

            if (hRow.hasId())
            {
                sb.append(addId(hRow.getId()));
            }

            sb.append(">\n");

            //
            // Process <th> attributes
            //
            for (int i = 0; i < delRow.length(); i++)
            {

                sb.append(INDENT[3]).append("<th");

                if (!hRow.hasAttrib())
                {
                    if (!delRow.getCell(i).isEmpty())
                    {
                        sb.append(addStyle(delRow.getCell(i)));
                    }
                } else
                {
                    if (hRow.hasBorder())
                    {
                        tmp = String.format(ROW_BORDER, hRow.getBorderWidth(), hRow.getCellPadding());
                        sb.append(addStyle(tmp + delRow.getCell(i)));
                    } else
                    {
                        if (hRow.hasClasses())
                        {
                            sb.append(addClass(hRow.getClasses()));
                        }

                        if (!delRow.getCell(i).isEmpty())
                        {
                            sb.append(addStyle(delRow.getCell(i)));
                        }
                    }
                }

                sb.append(">\n").append(hRow.getCell(i).trim()).append("\n")
                        .append(INDENT[3]).append("</th>\n");
            }

            sb.append(INDENT[2]).append("</tr>\n")
                    .append(INDENT[1]).append("</thead>\n");

            //
            // Process data rows.
            //
            if (!data.trim().isEmpty())
            {
                String[] dataRows = data.split("\n");

                //
                // Stores rotating list of row attributes.
                //
                TableRowList rowList = new TableRowList(dataRows.length);

                sb.append(INDENT[1]).append("<tbody>\n");

                //
                // Process individual data rows.
                //
                for (String dataRowString : dataRows)
                {
                    TableRow dataRow = TableRow.parse(dataRowString);
                    rowList.add(dataRow);
                    sb.append(INDENT[2]).append("<tr");

                    if (dataRow.hasId())
                    {
                        sb.append(addId(dataRow.getId()));
                    }

                    sb.append(">\n");
                    TableRow attrib = null;

                    if (rowList.hasNext())
                    {
                        attrib = rowList.getNext();
                    }

                    //
                    // Process row -> columns.
                    //
                    for (int j = 0; j < dataRow.length() && j < delRow.length(); j++)
                    {
                        // Process <td> attributes
                        sb.append(INDENT[3]).append("<td");

                        //
                        // If 'row' has attributes.
                        //
                        if (!dataRow.hasAttrib())
                        {
                            if (attrib != null)
                            {

                                if (attrib.hasBorder())
                                {
                                    tmp = String.format(ROW_BORDER, attrib.getBorderWidth(), attrib.getCellPadding());
                                    sb.append(addStyle(tmp + delRow.getCell(j)));
                                } else
                                {
                                    if (attrib.hasClasses())
                                    {
                                        sb.append(addClass(attrib.getClasses()));
                                    }

                                    if (!delRow.getCell(j).isEmpty())
                                    {
                                        sb.append(addStyle(delRow.getCell(j)));
                                    }
                                }
                            } else if (!delRow.getCell(j).isEmpty())
                            {
                                sb.append(addStyle(delRow.getCell(j)));
                            }
                        } else
                        {
                            //
                            // If 'row' has border settings.
                            //
                            if (dataRow.hasBorder())
                            {
                                tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(), dataRow.getCellPadding());
                                sb.append(addStyle(tmp + delRow.getCell(j)));
                            } else
                            {
                                //
                                // If 'row' has class attributes.
                                //
                                if (dataRow.hasClasses())
                                {
                                    sb.append(addClass(dataRow.getClasses()));
                                }

                                //
                                // If delimiter row has column settings.
                                //
                                if (!delRow.getCell(j).isEmpty())
                                {
                                    sb.append(addStyle(delRow.getCell(j)));
                                }
                            }
                        }

                        sb.append(">\n").append(dataRow.getCell(j).trim()).append("\n")
                                .append(INDENT[3]).append("</td>\n");
                    }

                    //
                    // If 'row' has fewer column than the delimiter row.
                    //
                    if (dataRow.length() < hRow.length())
                    {
                        sb.append(INDENT[3]).append("<td");

                        //
                        // Add missing columns.
                        //
                        for (int k = dataRow.length(); k < hRow.length(); k++)
                        {
                            //
                            // If 'row' has attributes.
                            //
                            if (!dataRow.hasAttrib())
                            {
                                if (attrib != null)
                                {
                                    if (attrib.hasBorder())
                                    {
                                        tmp = String.format(ROW_BORDER, attrib.getBorderWidth(), attrib.getCellPadding());
                                        sb.append(addStyle(tmp + delRow.getCell(k)));
                                    } else
                                    {
                                        if (attrib.hasClasses())
                                        {
                                            sb.append(addClass(attrib.getClasses()));
                                        }

                                        if (!delRow.getCell(k).isEmpty())
                                        {
                                            sb.append(addStyle(delRow.getCell(k)));
                                        }
                                    }
                                } else if (!delRow.getCell(k).isEmpty())
                                {
                                    sb.append(addStyle(delRow.getCell(k)));
                                }
                            } else
                            {
                                //
                                // If 'row' has border settings.
                                //
                                if (dataRow.hasBorder())
                                {
                                    tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(), dataRow.getCellPadding());
                                    sb.append(addStyle(tmp + delRow.getCell(k)));
                                } else
                                {
                                    //
                                    // If 'row' has class attributes.
                                    //
                                    if (dataRow.hasClasses())
                                    {
                                        sb.append(addClass(dataRow.getClasses()));
                                    }

                                    //
                                    // If delimiter row has column settings.
                                    //
                                    if (!delRow.getCell(k).isEmpty())
                                    {
                                        sb.append(addStyle(delRow.getCell(k)));
                                    }
                                }
                            }

                            sb.append(">\n&nbsp;\n").append(INDENT[3]).append("</td>\n");
                        }
                    }

                    sb.append(INDENT[2]).append("</tr>\n");
                }

                sb.append(INDENT[1]).append("</tbody>\n");
            }

            String out = sb.append("</table>\n").toString();

            //
            // Encode table html to protect it from further processing.
            //
            rtn = "\n\n" + HTML_PROTECTOR.encode(out) + "\n\n";
        }

        return rtn;
    }

    /**
     * Class contains the processed version of the 'caption'.
     */
    private static class TableCaption {

        /**
         * Class instance of pattern.
         */
        private static final Pattern PATTERN = compile("(?<caption>.*?)[ ]*" + CLASS_REGEX);

        /**
         * The processed caption text.
         */
        public final String caption;

        /**
         * The classes in raw form.
         */
        public final String classes;
        /**
         * The raw caption text.
         */
        public final String text;

        /**
         * Instantiate class with caption text.
         *
         * @param text The caption.
         */
        private TableCaption(String text) {
            this.text = text;

            Matcher m = PATTERN.matcher(text);

            if (m.find())
            {
                this.caption = m.group("caption").trim();
                this.classes = m.group("classes").trim();
            } else
            {
                this.caption = text.trim();
                this.classes = null;
            }
        }

        /**
         * Classes exist.
         *
         * @return {@code true} if exist, {@code false} otherwise.
         */
        public boolean hasClasses() {
            return classes != null;
        }

        @Override
        public String toString() {
            return "TableCaption{\n"
                   + "    text = " + text + ",\n"
                   + "    classes = " + classes + "\n"
                   + "}";
        }

    }
}
