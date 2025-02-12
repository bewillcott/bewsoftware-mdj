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
package com.bewsoftware.mdj.core.plugins;

import com.bewsoftware.annotations.jcip.Immutable;
import com.bewsoftware.mdj.core.TextEditor;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.runSpanGamut;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addId;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CLASS_REGEX_OPT;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CODE_BLOCK_BEGIN;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CODE_BLOCK_END;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.HTML_PROTECTOR;
import static java.util.regex.Pattern.compile;

/**
 * FormParagraphs class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
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
    private static Tag tag(final String text)
    {
        final Pattern pId = compile("(?<!\\\\)" + ID_REGEX);
        final Pattern pClasses = compile("^(?<!\\\\)" + CLASS_REGEX_OPT);
        final ArrayList<String> ids = new ArrayList<>();
        final ArrayList<String> classes = new ArrayList<>();
        final TextEditor ted = new TextEditor(text);

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
    public TextEditor execute(final TextEditor text)
    {
        text.deleteAll("\\A\\n+");
        text.deleteAll("\\n+\\z");
        text.replaceAll("(?:\\A|\\n)" + CODE_BLOCK_BEGIN + "(\\w+?)" + CODE_BLOCK_END + "(?:\\n|\\Z)", "\n\n$1\n\n");

        final String[] paragraphs;

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
            final String decoded = HTML_PROTECTOR.decode(paragraph);

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
                final Tag tag = tag(paragraph);
                paragraphs[i] = "<p" + tag.id + tag.classes + ">" + tag.text + "</p>";
            }
        }

        return new TextEditor(String.join("\n\n", paragraphs));
    }

    /**
     * Used by {@link #tag(java.lang.String) tag()}.
     *
     * @author Bradley Willcott
     *
     * @since 0.6
     * @version 0.8.0
     */
    @Immutable
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
