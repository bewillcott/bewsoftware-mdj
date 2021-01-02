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

import static com.bewsoftware.mdj.core.Attributes.addClass;
import static com.bewsoftware.mdj.core.Attributes.addId;
import static com.bewsoftware.mdj.core.Attributes.addStyle;
import static com.bewsoftware.mdj.core.MarkdownProcessor.HTML_PROTECTOR;
import static com.bewsoftware.mdj.core.MarkdownProcessor.processGroupText;

/**
 * TableReplacement class moved out from {@link MarkdownProcessor} class.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
class TableReplacement implements Replacement {

    private final String CAPTION_BORDER = "border-left: %1$dpx solid black;border-top: %1$dpx solid black;border-right: %1$dpx solid black;padding: %2$dpx";
    private final String[] INDENT =
    {
        "", "  ", "    ", "      "
    };
    private final String ROW_BORDER = "border: %1$dpx solid black;padding: %2$dpx;";
    private final String TABLE_BORDER = "border: %1$dpx solid black;border-collapse: collapse;padding: %2$dpx";

    public TableReplacement() {
    }

    @Override
    public String replacement(Matcher m) {

        String rtn = m.group();

        String caption = m.group("caption");
        String header = processGroupText(m.group("header")).trim();
        String delrow = m.group("delrow").trim();
        String data = processGroupText(m.group("datarows")).trim();

        // Process <thead>
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

            if (caption != null && !caption.isEmpty())
            {
                boolean captionBorders = false;
                sb.append(INDENT[1]).append("<caption");
                caption = processGroupText(caption.trim());

                if (caption.startsWith("[") && caption.endsWith("]"))
                {
                    captionBorders = true;
                    caption = caption.substring(1, caption.length() - 1).trim();
                }

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

            // Process column formatting
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

            sb.append(INDENT[1]).append("<thead>\n")
                    .append(INDENT[2]).append("<tr");

            if (hRow.hasId())
            {
                sb.append(addId(hRow.getId()));
            }

            sb.append(">\n");

            // Process <th> attributes
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

            if (!data.trim().isEmpty())
            {
                String[] dataRows = data.split("\n");
                TableRowList rowList = new TableRowList(dataRows.length);

                sb.append(INDENT[1]).append("<tbody>\n");

                for (String dataRow1 : dataRows)
                {
                    TableRow dataRow = TableRow.parse(dataRow1);
                    rowList.add(dataRow);
                    sb.append(INDENT[2]).append("<tr");

                    if (dataRow.hasId())
                    {
                        sb.append(addId(dataRow.getId()));
                    }

                    sb.append(">\n");

                    for (int j = 0; j < dataRow.length() && j < delRow.length(); j++)
                    {
                        // Process <td> attributes
                        sb.append(INDENT[3]).append("<td");

                        if (!dataRow.hasAttrib())
                        {
                            if (rowList.hasNext())
                            {
                                TableRow attrib = rowList.getNext();

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
                            if (dataRow.hasBorder())
                            {
                                tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(), dataRow.getCellPadding());
                                sb.append(addStyle(tmp + delRow.getCell(j)));
                            } else
                            {
                                if (dataRow.hasClasses())
                                {
                                    sb.append(addClass(dataRow.getClasses()));
                                }

                                if (!delRow.getCell(j).isEmpty())
                                {
                                    sb.append(addStyle(delRow.getCell(j)));
                                }
                            }
                        }

                        sb.append(">\n").append(dataRow.getCell(j).trim()).append("\n")
                                .append(INDENT[3]).append("</td>\n");
                    }

                    if (dataRow.length() < hRow.length())
                    {
                        sb.append(INDENT[3]).append("<td");

                        for (int k = dataRow.length(); k < hRow.length(); k++)
                        {
                            if (!dataRow.hasAttrib())
                            {
                                if (rowList.hasNext())
                                {
                                    TableRow attrib = rowList.getNext();

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
                                if (dataRow.hasBorder())
                                {
                                    tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(), dataRow.getCellPadding());
                                    sb.append(addStyle(tmp + delRow.getCell(k)));
                                } else
                                {
                                    if (dataRow.hasClasses())
                                    {
                                        sb.append(addClass(dataRow.getClasses()));
                                    }

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

            rtn = "\n\n" + HTML_PROTECTOR.encode(out) + "\n\n";
        }

        return rtn;
    }
}
