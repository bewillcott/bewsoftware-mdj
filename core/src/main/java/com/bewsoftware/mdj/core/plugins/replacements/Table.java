/*
 * Copyright (c) 2005, Martian Software
 * Authors: Pete Bevin, John Mutchek
 * http://www.martiansoftware.com/markdownj
 *
 * Copyright (c) 2020, 2021 Bradley Willcott
 * Modifications to the code.
 * Refactored.
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
package com.bewsoftware.mdj.core.plugins.replacements;

import com.bewsoftware.mdj.core.MarkdownProcessor;
import com.bewsoftware.mdj.core.utils.TextEditor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.runSpanGamut;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addId;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addStyle;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CLASS_REGEX;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.HTML_PROTECTOR;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Table class moved out from {@link MarkdownProcessor} class.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.3
 * @version 0.6.13
 */
public class Table implements Replacement
{

    private static final String CAPTION_BORDER
            = "border-left: %1$dpx solid black;border-top: %1$dpx solid black;"
            + "border-right: %1$dpx solid black;padding: %2$dpx";

    private static final String[] INDENT =
    {
        "", "  ", "    ", "      "
    };

    private static final String ROW_BORDER = "border: %1$dpx solid black;padding: %2$dpx;";

    private static final String TABLE_BORDER
            = "border: %1$dpx solid black;border-collapse: collapse;padding: %2$dpx";

    private static final String TAG_ID = "\\[#\\w+\\]";

    /*
     * "caption" is the Title for the table
     * "header" is the first row of the table
     * "delrow" is the delimiter row
     * "datarows" contains the body of the table
     * "tail" contains straggler rows just following the table
     *
     * The table should be followed by a blank line
     */
    public static final Pattern PATTERN = compile("(?<=^\\n+)"
            + "(?<caption>^(?:[ ]*\\[\\w+[^\\]]*?\\][ ]*|[ ]*.*?\\w+.*?[ ]*)\\n)?"
            + "(?<header>^\\|(?:.+?\\|)+?(?:\\[[^\\]]*?\\])?)[ ]*\\n"
            + "(?<delrow>\\|(?:[ ]*([:-]{1}[-]+?[:-]{1}|[-]+?[:][-]+?)[ ]*\\|)+?(?:"
            + TAG_ID + ")?(?:\\[[^\\]]*?\\])?)[ ]*\\n"
            + "(?<datarows>(?:\\|(?:[^\\|\\n]*\\|)+(?:"
            + TAG_ID + ")?(?:\\[[^\\]]*?\\])?[ ]*\\n)*?)?"
            // Find end of table
            + "(?="
            // Blank line
            + "^\\n"
            // End of data
            + "|\\Z)"
            + "", MULTILINE);

    public Table()
    {
    }

    private static String processGroupText(final String text)
    {
        if (text != null && !text.isBlank())
        {
            // Escaped pipes need to be handled
            return runSpanGamut(new TextEditor(text)).toString();
        } else
        {
            return "";
        }
    }

    private static boolean rowIsShorterThanHeader(TableRow dataRow, TableRow headerRow)
    {
        return dataRow.length() < headerRow.length();
    }

    private static boolean validRowLength(TableRow hRow, TableRow delRow)
    {
        return hRow.length() == delRow.length();
    }

    @Override
    public String process(Matcher m)
    {

        String rtn = m.group();

        String caption = m.group("caption");
        String header = processGroupText(m.group("header")).trim();
        String delrow = m.group("delrow").trim();
        String datarows = processGroupText(m.group("datarows")).trim();

        TableRow headerRow = TableRow.parse(header);
        headerRow.setReadOnly();

        TableRow delimiterRow = TableRow.parse(delrow);

        if (validRowLength(headerRow, delimiterRow))
        {
            rtn = buildTable(delimiterRow, caption, headerRow, datarows);
        }

        return rtn;
    }

    private void addBorderStyle(TableRow headerRow, StringBuilder sb, TableRow delimiterRow, int i)
    {
        String tmp = String.format(ROW_BORDER, headerRow.getBorderWidth(),
                headerRow.getCellPadding());
        sb.append(addStyle(tmp + delimiterRow.getCell(i)));
    }

    private void addDataRowAttribute(TableRow dataRow, StringBuilder sb, TableRow delimiterRow, int j)
    {
        if (dataRow.hasBorder())
        {
            String tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(),
                    dataRow.getCellPadding());
            sb.append(addStyle(tmp + delimiterRow.getCell(j)));
        } else
        {
            processClassesAndDefaultStyle(dataRow, sb, delimiterRow, j);
        }
    }

    private void addDefaultRowAttribute(TableRow defaultRowAttribute, StringBuilder sb, TableRow delimiterRow, int j)
    {
        if (defaultRowAttribute.hasBorder())
        {
            String tmp = String.format(ROW_BORDER, defaultRowAttribute.getBorderWidth(),
                    defaultRowAttribute.getCellPadding());
            sb.append(addStyle(tmp + delimiterRow.getCell(j)));
        } else
        {
            processClassesAndDefaultStyle(defaultRowAttribute, sb, delimiterRow, j);
        }
    }

    private void addDelimiterRowDefault(TableRow delimiterRow, int i, StringBuilder sb)
    {
        if (!delimiterRow.getCell(i).isEmpty())
        {
            sb.append(addStyle(delimiterRow.getCell(i)));
        }
    }

    private void addMissingColumns(TableRow dataRow, TableRow headerRow, StringBuilder sb, TableRow defaultRowAttribute, TableRow delimiterRow)
    {
        for (int k = dataRow.length(); k < headerRow.length(); k++)
        {
            processTDTag(sb, dataRow, defaultRowAttribute, delimiterRow, k, "&nbsp;");
        }
    }

    private String buildTable(TableRow delimiterRow, String caption, TableRow headerRow, String datarows)
    {
        String rtn;
        StringBuilder sb = new StringBuilder();

        processDelimiterRow(sb, delimiterRow, caption);
        processTHeadTag(sb, delimiterRow, headerRow);

        if (!datarows.trim().isEmpty())
        {
            processTBodyTag(datarows, sb, delimiterRow, headerRow);
        }

        String out = sb.append("</table>\n").toString();
        //
        // Encode table html to protect it from further processing.
        //
        rtn = "\n\n" + HTML_PROTECTOR.encode(out) + "\n\n";

        return rtn;
    }

    private void processCaption(StringBuilder sb, String caption, TableRow delimiterRow)
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
            caption = processCaptionLookingForClasses(caption, sb);
        }

        processCaptionBordersAndAttributes(captionBorders, delimiterRow, sb);

        sb.append(">\n").append(caption).append("\n")
                .append(INDENT[1]).append("</caption>\n");
    }

    private void processCaptionBordersAndAttributes(boolean captionBorders,
            TableRow delimiterRow, StringBuilder sb)
    {
        if (captionBorders && delimiterRow.hasAttribute() && delimiterRow.hasBorder())
        {
            String tmp = String.format(CAPTION_BORDER, delimiterRow.getBorderWidth(),
                    delimiterRow.getCellPadding());
            sb.append(addStyle(tmp));
        }
    }

    private void processCaptionIfAny(String caption, StringBuilder sb, TableRow delimiterRow)
    {
        if (caption != null && !caption.isBlank())
        {
            processCaption(sb, caption, delimiterRow);
        }
    }

    private String processCaptionLookingForClasses(String caption, StringBuilder sb)
    {
        TableCaption tc = new TableCaption(caption);

        if (tc.hasClasses())
        {
            caption = tc.caption;
            sb.append(addClass(tc.classes));
        }

        return caption;
    }

    private void processClassesAndDefaultStyle(TableRow headerRow, StringBuilder sb,
            TableRow delimiterRow, int i)
    {
        processHeaderRowClasses(headerRow, sb);
        addDelimiterRowDefault(delimiterRow, i, sb);
    }

    private void processColumnFormatting(TableRow delimiterRow)
    {
        for (int i = 0; i < delimiterRow.length(); i++)
        {
            String delimiterColumn = delimiterRow.getCell(i).trim();
            int textAlignment = (delimiterColumn.startsWith(":") ? 1 : 0);
            textAlignment = textAlignment + (delimiterColumn.endsWith(":") ? 2 : 0);

            switch (textAlignment)
            {
                case 0 ->
                {
                    if (delimiterColumn.matches("[-]+?[:][-]+?"))
                    {
                        delimiterRow.setCell(i, "text-align: center");
                    } else
                    {
                        delimiterRow.setCell(i, "");
                    }
                }

                case 1 ->
                    delimiterRow.setCell(i, "text-align: left");

                case 2 ->
                    delimiterRow.setCell(i, "text-align: right");

                case 3 ->
                    delimiterRow.setCell(i, "text-align: justify");

                default ->
                {
                }
            }
        }
    }

    private void processDataRow(String dataRowString, TableRowList rotatingListOfRowAttributes,
            StringBuilder sb, TableRow delimiterRow, TableRow headerRow)
    {
        TableRow dataRow = TableRow.parse(dataRowString);
        rotatingListOfRowAttributes.add(dataRow);
        sb.append(INDENT[2]).append("<tr");

        processDataRowId(dataRow, sb);

        sb.append(">\n");
        TableRow defaultRowAttribute = null;

        if (rotatingListOfRowAttributes.hasNext())
        {
            defaultRowAttribute = rotatingListOfRowAttributes.getNext();
        }

        processRowColumns(dataRow, delimiterRow, sb, defaultRowAttribute);

        if (rowIsShorterThanHeader(dataRow, headerRow))
        {
            addMissingColumns(dataRow, headerRow, sb, defaultRowAttribute, delimiterRow);
        }

        sb.append(INDENT[2]).append("</tr>\n");
    }

    private void processDataRowId(TableRow dataRow, StringBuilder sb)
    {
        if (dataRow.hasId())
        {
            sb.append(addId(dataRow.getId()));
        }
    }

    private void processDataRows(String[] dataRows, TableRowList rotatingListOfRowAttributes,
            StringBuilder sb, TableRow delimiterRow, TableRow headerRow)
    {
        for (String dataRow : dataRows)
        {
            processDataRow(dataRow, rotatingListOfRowAttributes, sb, delimiterRow, headerRow);
        }
    }

    private void processDelimiterRow(StringBuilder sb, TableRow delimiterRow, String caption)
    {
        processTableTag(sb, delimiterRow);
        processCaptionIfAny(caption, sb, delimiterRow);
        processColumnFormatting(delimiterRow);
        delimiterRow.setReadOnly();
    }

    private void processDelimiterRowAttributes(TableRow delimiterRow, StringBuilder sb)
    {
        if (delimiterRow.hasAttribute())
        {
            processDelimiterRowId(delimiterRow, sb);

            if (delimiterRow.hasBorder())
            {
                String tmp = String.format(TABLE_BORDER, delimiterRow.getBorderWidth(),
                        delimiterRow.getCellPadding());
                sb.append(addStyle(tmp));
            } else
            {
                sb.append(addClass(delimiterRow.getClasses()));
            }
        }
    }

    private void processDelimiterRowId(TableRow delimiterRow, StringBuilder sb)
    {
        if (delimiterRow.hasId())
        {
            sb.append(addId(delimiterRow.getId()));
        }
    }

    private void processHeaderRowClasses(TableRow headerRow, StringBuilder sb)
    {
        if (headerRow.hasClasses())
        {
            sb.append(addClass(headerRow.getClasses()));
        }
    }

    private void processHeaderRowId(TableRow headerRow, StringBuilder sb)
    {
        if (headerRow.hasId())
        {
            sb.append(addId(headerRow.getId()));
        }
    }

    private void processRowColumns(TableRow dataRow, TableRow delimiterRow,
            StringBuilder sb, TableRow defaultRowAttribute)
    {
        for (int i = 0; i < dataRow.length() && i < delimiterRow.length(); i++)
        {
            processTDTag(sb, dataRow, defaultRowAttribute, delimiterRow, i, dataRow.getCell(i).trim());
        }
    }

    private void processTBodyTag(String datarows, StringBuilder sb, TableRow delimiterRow,
            TableRow headerRow)
    {
        String[] dataRows = datarows.split("\n");

        TableRowList rotatingListOfRowAttributes = new TableRowList(dataRows.length);

        sb.append(INDENT[1]).append("<tbody>\n");

        processDataRows(dataRows, rotatingListOfRowAttributes, sb, delimiterRow, headerRow);

        sb.append(INDENT[1]).append("</tbody>\n");
    }

    private void processTDTag(StringBuilder sb, TableRow dataRow, TableRow defaultRowAttribute,
            TableRow delimiterRow, int index, String cellContent)
    {
        sb.append(INDENT[3]).append("<td");

        processTDTagAttributes(dataRow, sb, delimiterRow, index, defaultRowAttribute);

        sb.append(">\n").append(cellContent).append("\n")
                .append(INDENT[3]).append("</td>\n");
    }

    private void processTDTagAttributes(TableRow dataRow, StringBuilder sb, TableRow delimiterRow, int index, TableRow defaultRowAttribute)
    {
        if (dataRow.hasAttribute())
        {
            addDataRowAttribute(dataRow, sb, delimiterRow, index);
        } else
        {
            if (defaultRowAttribute != null)
            {
                addDefaultRowAttribute(defaultRowAttribute, sb, delimiterRow, index);
            } else
            {
                addDelimiterRowDefault(delimiterRow, index, sb);
            }
        }
    }

    private void processTHAttributes(TableRow headerRow, StringBuilder sb, TableRow delimiterRow, int i)
    {
        if (headerRow.hasBorder())
        {
            addBorderStyle(headerRow, sb, delimiterRow, i);
        } else
        {
            processClassesAndDefaultStyle(headerRow, sb, delimiterRow, i);
        }
    }

    private void processTHTag(StringBuilder sb, TableRow delimiterRow, TableRow headerRow, int i)
    {
        sb.append(INDENT[3]).append("<th");

        if (headerRow.hasAttribute())
        {
            processTHAttributes(headerRow, sb, delimiterRow, i);
        } else
        {
            addDelimiterRowDefault(delimiterRow, i, sb);
        }

        sb.append(">\n").append(headerRow.getCell(i).trim()).append("\n")
                .append(INDENT[3]).append("</th>\n");
    }

    private void processTHTags(StringBuilder sb, TableRow delimiterRow, TableRow headerRow)
    {
        for (int i = 0; i < delimiterRow.length(); i++)
        {
            processTHTag(sb, delimiterRow, headerRow, i);
        }
    }

    private void processTHeadTag(StringBuilder sb, TableRow delimiterRow, TableRow headerRow)
    {
        sb.append(INDENT[1]).append("<thead>\n")
                .append(INDENT[2]).append("<tr");

        processHeaderRowId(headerRow, sb);

        sb.append(">\n");

        processTHTags(sb, delimiterRow, headerRow);

        sb.append(INDENT[2]).append("</tr>\n")
                .append(INDENT[1]).append("</thead>\n");
    }

    private void processTableTag(StringBuilder sb, TableRow delimiterRow)
    {
        sb.append("<table");
        processDelimiterRowAttributes(delimiterRow, sb);
        sb.append(">\n");
    }

    /**
     * Class contains the processed version of the 'caption'.
     */
    private static class TableCaption
    {

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
        private TableCaption(String text)
        {
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
        public boolean hasClasses()
        {
            return classes != null;
        }

        @Override
        public String toString()
        {
            return "TableCaption{\n"
                    + "    text = " + text + ",\n"
                    + "    classes = " + classes + "\n"
                    + "}";
        }

    }
}
