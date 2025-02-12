/*
 * Copyright (c) 2005, Martian Software
 * Authors: Pete Bevin, John Mutchek
 * http://www.martiansoftware.com/markdownj
 *
 * Copyright (c) 2020, 2021, 2025 Bradley Willcott
 * https://github.com/bewillcott/bewsoftware-mdj
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

import com.bewsoftware.mdj.MarkdownProcessor;
import com.bewsoftware.mdj.core.Replacement;
import com.bewsoftware.mdj.core.TextEditor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.runSpanGamut;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addId;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addStyle;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CLASS_REGEX;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.HTML_PROTECTOR;
import static com.bewsoftware.utils.string.Strings.notBlank;
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
        if (notBlank(text))
        {
            // Escaped pipes need to be handled
            return runSpanGamut(new TextEditor(text)).toString();
        } else
        {
            return "";
        }
    }

    private static boolean rowIsShorterThanHeader(final TableRow dataRow, final TableRow headerRow)
    {
        return dataRow.length() < headerRow.length();
    }

    private static boolean validRowLength(final TableRow hRow, final TableRow delRow)
    {
        return hRow.length() == delRow.length();
    }

    @Override
    public String process(final Matcher m)
    {

        String rtn = m.group();

        final String caption = m.group("caption");
        final String header = processGroupText(m.group("header")).trim();
        final String delrow = m.group("delrow").trim();
        final String datarows = processGroupText(m.group("datarows")).trim();

        final TableRow headerRow = TableRow.parse(header);
        headerRow.setReadOnly();

        final TableRow delimiterRow = TableRow.parse(delrow);

        if (validRowLength(headerRow, delimiterRow))
        {
            rtn = buildTable(delimiterRow, caption, headerRow, datarows);
        }

        return rtn;
    }

    private void addBorderStyle(
            final TableRow headerRow,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final int i
    )
    {
        final String tmp = String.format(ROW_BORDER, headerRow.getBorderWidth(),
                headerRow.getCellPadding());
        sb.append(addStyle(tmp + delimiterRow.getCell(i)));
    }

    private void addDataRowAttribute(
            final TableRow dataRow,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final int j
    )
    {
        if (dataRow.hasBorder())
        {
            final String tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(),
                    dataRow.getCellPadding());
            sb.append(addStyle(tmp + delimiterRow.getCell(j)));
        } else
        {
            processClassesAndDefaultStyle(dataRow, sb, delimiterRow, j);
        }
    }

    private void addDefaultRowAttribute(
            final TableRow defaultRowAttribute,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final int j
    )
    {
        if (defaultRowAttribute.hasBorder())
        {
            final String tmp = String.format(ROW_BORDER, defaultRowAttribute.getBorderWidth(),
                    defaultRowAttribute.getCellPadding());
            sb.append(addStyle(tmp + delimiterRow.getCell(j)));
        } else
        {
            processClassesAndDefaultStyle(defaultRowAttribute, sb, delimiterRow, j);
        }
    }

    private void addDelimiterRowDefault(
            final TableRow delimiterRow,
            final int i,
            final StringBuilder sb
    )
    {
        if (!delimiterRow.getCell(i).isEmpty())
        {
            sb.append(addStyle(delimiterRow.getCell(i)));
        }
    }

    private void addMissingColumns(
            final TableRow dataRow,
            final TableRow headerRow,
            final StringBuilder sb,
            final TableRow defaultRowAttribute,
            final TableRow delimiterRow
    )
    {
        for (int k = dataRow.length(); k < headerRow.length(); k++)
        {
            processTDTag(sb, dataRow, defaultRowAttribute, delimiterRow, k, "&nbsp;");
        }
    }

    private String buildTable(
            final TableRow delimiterRow,
            final String caption,
            final TableRow headerRow,
            final String datarows
    )
    {
        String rtn;
        final StringBuilder sb = new StringBuilder();

        processDelimiterRow(sb, delimiterRow, caption);
        processTHeadTag(sb, delimiterRow, headerRow);

        if (!datarows.trim().isEmpty())
        {
            processTBodyTag(datarows, sb, delimiterRow, headerRow);
        }

        final String out = sb.append("</table>\n").toString();
        //
        // Encode table html to protect it from further processing.
        //
        rtn = "\n\n" + HTML_PROTECTOR.encode(out) + "\n\n";

        return rtn;
    }

    private void processCaption(
            final StringBuilder sb,
            final String caption,
            final TableRow delimiterRow
    )
    {
        String text = caption;

        boolean captionBorders = false;
        sb.append(INDENT[1]).append("<caption");
        text = processGroupText(text.trim());

        if (text.startsWith("[") && text.endsWith("]"))
        {
            captionBorders = true;
            text = text.substring(1, text.length() - 1).trim();
        } else
        {
            text = processCaptionLookingForClasses(text, sb);
        }

        processCaptionBordersAndAttributes(captionBorders, delimiterRow, sb);

        sb.append(">\n").append(text).append("\n")
                .append(INDENT[1]).append("</caption>\n");
    }

    private void processCaptionBordersAndAttributes(
            final boolean captionBorders,
            final TableRow delimiterRow,
            final StringBuilder sb
    )
    {
        if (captionBorders && delimiterRow.hasAttribute() && delimiterRow.hasBorder())
        {
            final String tmp = String.format(CAPTION_BORDER, delimiterRow.getBorderWidth(),
                    delimiterRow.getCellPadding());
            sb.append(addStyle(tmp));
        }
    }

    private void processCaptionIfAny(
            final String caption,
            final StringBuilder sb,
            final TableRow delimiterRow
    )
    {
        if (notBlank(caption))
        {
            processCaption(sb, caption, delimiterRow);
        }
    }

    private String processCaptionLookingForClasses(final String caption, final StringBuilder sb)
    {
        String rtn = caption;

        final TableCaption tc = new TableCaption(caption);

        if (tc.hasClasses())
        {
            rtn = tc.caption;
            sb.append(addClass(tc.classes));
        }

        return rtn;
    }

    private void processClassesAndDefaultStyle(
            final TableRow headerRow,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final int i
    )
    {
        processHeaderRowClasses(headerRow, sb);
        addDelimiterRowDefault(delimiterRow, i, sb);
    }

    private void processColumnFormatting(final TableRow delimiterRow)
    {
        for (int i = 0; i < delimiterRow.length(); i++)
        {
            String delimiterColumn = delimiterRow.getCell(i).trim();
            int textAlignment = (delimiterColumn.startsWith(":") ? 1 : 0);
            textAlignment += (delimiterColumn.endsWith(":") ? 2 : 0);

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

    private void processDataRow(
            final String dataRowString,
            final TableRowList rotatingListOfRowAttributes,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final TableRow headerRow
    )
    {
        final TableRow dataRow = TableRow.parse(dataRowString);
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

    private void processDataRowId(final TableRow dataRow, final StringBuilder sb)
    {
        if (dataRow.hasId())
        {
            sb.append(addId(dataRow.getId()));
        }
    }

    private void processDataRows(
            final String[] dataRows,
            final TableRowList rotatingListOfRowAttributes,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final TableRow headerRow
    )
    {
        for (String dataRow : dataRows)
        {
            processDataRow(dataRow, rotatingListOfRowAttributes, sb, delimiterRow, headerRow);
        }
    }

    private void processDelimiterRow(
            final StringBuilder sb,
            final TableRow delimiterRow,
            final String caption
    )
    {
        processTableTag(sb, delimiterRow);
        processCaptionIfAny(caption, sb, delimiterRow);
        processColumnFormatting(delimiterRow);
        delimiterRow.setReadOnly();
    }

    private void processDelimiterRowAttributes(final TableRow delimiterRow, final StringBuilder sb)
    {
        if (delimiterRow.hasAttribute())
        {
            processDelimiterRowId(delimiterRow, sb);

            if (delimiterRow.hasBorder())
            {
                final String tmp = String.format(TABLE_BORDER, delimiterRow.getBorderWidth(),
                        delimiterRow.getCellPadding());
                sb.append(addStyle(tmp));
            } else
            {
                sb.append(addClass(delimiterRow.getClasses()));
            }
        }
    }

    private void processDelimiterRowId(final TableRow delimiterRow, final StringBuilder sb)
    {
        if (delimiterRow.hasId())
        {
            sb.append(addId(delimiterRow.getId()));
        }
    }

    private void processHeaderRowClasses(final TableRow headerRow, final StringBuilder sb)
    {
        if (headerRow.hasClasses())
        {
            sb.append(addClass(headerRow.getClasses()));
        }
    }

    private void processHeaderRowId(final TableRow headerRow, final StringBuilder sb)
    {
        if (headerRow.hasId())
        {
            sb.append(addId(headerRow.getId()));
        }
    }

    private void processRowColumns(
            final TableRow dataRow,
            final TableRow delimiterRow,
            final StringBuilder sb,
            final TableRow defaultRowAttribute
    )
    {
        for (int i = 0; i < dataRow.length() && i < delimiterRow.length(); i++)
        {
            processTDTag(sb, dataRow, defaultRowAttribute, delimiterRow, i, dataRow.getCell(i).trim());
        }
    }

    private void processTBodyTag(
            final String datarows,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final TableRow headerRow
    )
    {
        final String[] dataRows = datarows.split("\n");

        final TableRowList rotatingListOfRowAttributes = new TableRowList(dataRows.length);

        sb.append(INDENT[1]).append("<tbody>\n");

        processDataRows(dataRows, rotatingListOfRowAttributes, sb, delimiterRow, headerRow);

        sb.append(INDENT[1]).append("</tbody>\n");
    }

    private void processTDTag(
            final StringBuilder sb,
            final TableRow dataRow,
            final TableRow defaultRowAttribute,
            final TableRow delimiterRow,
            final int index,
            final String cellContent
    )
    {
        sb.append(INDENT[3]).append("<td");

        processTDTagAttributes(dataRow, sb, delimiterRow, index, defaultRowAttribute);

        sb.append(">\n").append(cellContent).append("\n")
                .append(INDENT[3]).append("</td>\n");
    }

    private void processTDTagAttributes(
            final TableRow dataRow,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final int index,
            final TableRow defaultRowAttribute
    )
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

    private void processTHAttributes(
            final TableRow headerRow,
            final StringBuilder sb,
            final TableRow delimiterRow,
            final int i
    )
    {
        if (headerRow.hasBorder())
        {
            addBorderStyle(headerRow, sb, delimiterRow, i);
        } else
        {
            processClassesAndDefaultStyle(headerRow, sb, delimiterRow, i);
        }
    }

    private void processTHTag(
            final StringBuilder sb,
            final TableRow delimiterRow,
            final TableRow headerRow,
            final int i
    )
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

    private void processTHTags(
            final StringBuilder sb,
            final TableRow delimiterRow,
            final TableRow headerRow
    )
    {
        for (int i = 0; i < delimiterRow.length(); i++)
        {
            processTHTag(sb, delimiterRow, headerRow, i);
        }
    }

    private void processTHeadTag(
            final StringBuilder sb,
            final TableRow delimiterRow,
            final TableRow headerRow
    )
    {
        sb.append(INDENT[1]).append("<thead>\n")
                .append(INDENT[2]).append("<tr");

        processHeaderRowId(headerRow, sb);

        sb.append(">\n");

        processTHTags(sb, delimiterRow, headerRow);

        sb.append(INDENT[2]).append("</tr>\n")
                .append(INDENT[1]).append("</thead>\n");
    }

    private void processTableTag(final StringBuilder sb, final TableRow delimiterRow)
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
        private TableCaption(final String text)
        {
            this.text = text;

            final Matcher m = PATTERN.matcher(text);

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
