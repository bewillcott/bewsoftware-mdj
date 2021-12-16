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
package com.bewsoftware.mdj.core.plugins;

import com.bewsoftware.mdj.core.TextEditor;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.Attributes.addClass;
import static com.bewsoftware.mdj.core.Attributes.addId;
import static com.bewsoftware.mdj.core.MarkdownProcessor.CLASS_REGEX_OPT;
import static com.bewsoftware.mdj.core.MarkdownProcessor.CODE_BLOCK_BEGIN;
import static com.bewsoftware.mdj.core.MarkdownProcessor.CODE_BLOCK_END;
import static com.bewsoftware.mdj.core.MarkdownProcessor.HTML_PROTECTOR;
import static com.bewsoftware.mdj.core.plugins.PluginInterlink.runSpanGamut;
import static java.util.regex.Pattern.compile;

/**
 * FormParagraphs class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
public class FormParagraphs implements TextConvertor
{
    private static final String ID_REGEX = "\\[#(?<id>\\w+)\\]";

    public FormParagraphs()
    {
    }

    /**
     * Return a Tag containing any Id and/or classes.
     * <p>
     * <b>Changes:</b>
     * <ul>
     * <li>Added {@code classes}.</li>
     * </ul>
     * Bradley Willcott (03/01/2021)
     *
     * @param text to process.
     *
     * @return new Tag with data.
     */
    private static Tag tag(String text)
    {
        Pattern pId = compile("(?<!\\\\)" + ID_REGEX);
        Pattern pClasses = compile("^(?<!\\\\)" + CLASS_REGEX_OPT);
        final ArrayList<String> ids = new ArrayList<>();
        final ArrayList<String> classes = new ArrayList<>();
        TextEditor ted = new TextEditor(text);

        return new Tag(
                ted.replaceAll(pId, m ->
                {
                    ids.add(m.group("id"));

                    return "";
                }).replaceAll(pClasses, m ->
                {
                    classes.add(m.group("classes"));

                    return "";
                })
                        .toString(),
                classes.isEmpty() ? null : classes.get(0),
                ids.isEmpty() ? null : ids.get(0));
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        text.deleteAll("\\A\\n+");
        text.deleteAll("\\n+\\z");
        text.replaceAll("(?:\\A|\\n)" + CODE_BLOCK_BEGIN + "(\\w+?)" + CODE_BLOCK_END + "(?:\\n|\\Z)", "\n\n$1\n\n");

        String[] paragraphs;

        if (text.isEmpty())
        {
            paragraphs = new String[0];
        } else
        {

            paragraphs = compile("\\n{2,}").split(text.toString());
        }

        for (int i = 0; i < paragraphs.length; i++)
        {
            String paragraph = paragraphs[i];
            String decoded = HTML_PROTECTOR.decode(paragraph);
            if (decoded != null)
            {
                paragraphs[i] = decoded;
            } else
            {
                paragraph = runSpanGamut(new TextEditor(paragraph)).toString();

                //
                // Changed Tag to include "class" attribute.
                //
                // Bradley Willcott (03/01/2021)
                Tag tag = tag(paragraph);
                paragraphs[i] = "<p" + tag.id + tag.classes + ">" + tag.text + "</p>";
            }
        }

        return new TextEditor(String.join("\n\n", paragraphs));
    }
//
//    @Override
//    public String toString()
//    {
//        return "Markdown Processor for Java (v0.6.8)";
//    }

    /**
     * Used by {@link #tag(java.lang.String) tag()}.
     *
     * @author Bradley Willcott
     *
     * @since 0.6
     * @version 0.6.8
     */
    private static class Tag
    {
        public final String text;

        public final String classes;

        public final String id;

        private Tag(final String text, final String classes, final String id)
        {
            this.text = text;
            this.classes = addClass(classes);
            this.id = addId(id);
        }
    }
}
