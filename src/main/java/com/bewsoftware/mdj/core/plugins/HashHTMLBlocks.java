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

import com.bewsoftware.mdj.core.Replacement;
import com.bewsoftware.mdj.core.TextEditor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.utils.Constants.HTML_PROTECTOR;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.TAB_WIDTH;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Hashify HTML blocks:
 * We only want to do this for block-level HTML tags, such as headers,
 * lists, and tables. That's because we still want to wrap
 * '{@literal <} p{@literal >} 's around "paragraphs" that are wrapped in
 * non-block-level tags, such as anchors, phrase emphasis, and spans.
 * The list of tags we're looking for is hard-coded.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class HashHTMLBlocks implements TextConvertor
{
    private static final int LESS_THAN_TAB = TAB_WIDTH - 1;

    /*
     */
    private static final String[] TAGS_A =
    {
        "head",
        "p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "pre", "table",
        "dl", "ol", "ul", "script", "noscript", "form", "fieldset", "iframe", "math"
    };

    private static final String ALTERNATION_A = String.join("|", TAGS_A);

    private static final String[] TAGS_B =
    {
        "ins", "del"
    };

    private static final String ALTERNATION_B = ALTERNATION_A + "|" + String.join("|", TAGS_B);

    public HashHTMLBlocks()
    {
    }

    @Override
    public TextEditor execute(final TextEditor text)
    {
        // First, look for nested blocks, e.g.:
        //   <div>
        //       <div>
        //       tags for inner block must be indented.
        //       </div>
        //   </div>
        //
        // The outermost tags must start at the left margin for this to match, and
        // the inner nested divs must be indented.
        // We need to do this before the next, more liberal match, because the next
        // match will start at the first `<div>` and stop at the first `</div>`.
        final Pattern p1 = compile("(?:^<(" + ALTERNATION_B + ")\\b[^>]*>\n"
                + "(?:.*\\n)*?"
                + "^</\\1>[ ]*"
                + "(?=\\n+|\\Z))", MULTILINE | CASE_INSENSITIVE);

        final Replacement protectHTML = (Matcher m) ->
        {
            String literal = m.group();
            return "\n\n" + HTML_PROTECTOR.encode(literal) + "\n\n";
        };

        text.replaceAll(p1, protectHTML);

        // Now match more liberally, simply from `\n<tag>` to `</tag>\n`
        final Pattern p2 = compile("(?:^<(" + ALTERNATION_A + ")\\b[^>]*>"
                + "(((?!(<\\1|</\\1)).)*\\n)*?"
                + ".*</\\1\\b[^>]*>[ ]*"
                + "(?=\\n+|\\Z))", MULTILINE | CASE_INSENSITIVE);
        do
        {
            text.replaceAll(p2, protectHTML);
        } while (text.wasFound());

        // Special case for <hr>
        final Pattern p3 = compile("(?:"
                + "(?<=\\n\\n)"
                + "|"
                + "\\A\\n?"
                + ")"
                + "("
                + "[ ]{0," + LESS_THAN_TAB + "}"
                + "<(hr)"
                + "\\b"
                + "([^<>])*?"
                + "/?>"
                + "[ ]*"
                + "(?=\\n{2,}|\\Z))", CASE_INSENSITIVE);
        text.replaceAll(p3, protectHTML);

        // Special case for standalone HTML comments:
        final Pattern p4 = compile("(?:"
                + "(?<=\\n\\n)"
                + "|"
                + "\\A\\n?"
                + ")"
                + "("
                + "[ ]{0," + LESS_THAN_TAB + "}"
                + "(?s:"
                + "<!"
                + "(--.*?--\\s*)+"
                + ">"
                + ")"
                + "[ ]*"
                + "(?=\\n{2,}|\\Z)"
                + ")");
        text.replaceAll(p4, protectHTML);

        return text;
    }

}
