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
import static com.bewsoftware.mdj.core.MarkdownProcessor.CODE_BLOCK_BEGIN;
import static com.bewsoftware.mdj.core.MarkdownProcessor.CODE_BLOCK_END;
import static com.bewsoftware.mdj.core.MarkdownProcessor.HTML_PROTECTOR;
import static com.bewsoftware.mdj.core.MarkdownProcessor.LANG_IDENTIFIER;
import static com.bewsoftware.mdj.core.MarkdownProcessor.encodeCode;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * CodeBlockReplacement class moved out from {@link MarkdownProcessor} class.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
class CodeBlockReplacement implements Replacement {

    private final boolean fencedCode;
    private Matcher m;

    CodeBlockReplacement(boolean fencedCode) {
        this.fencedCode = fencedCode;
    }

    @Override
    public String replacement(Matcher m) {
        this.m = m;
        TextEditor ed = new TextEditor(m.group("body"));

        if (!fencedCode)
        {
            ed.outdent();
        }

        unHashBlocks(ed);
        encodeCode(ed);
        ed.detabify().deleteAll("\\A\\n+").deleteAll("\\s+\\z");

        String text = ed.toString();
        String out = "";

        if (m.group("class") != null)
        {
            out = languageBlock(m.group("class"), text);
        } else if (m.group("classes") != null)
        {
            out = classesBlock(m.group("classes"), text);
        } else
        {
            out = genericCodeBlock(text);
        }

        return "\n" + CODE_BLOCK_BEGIN + HTML_PROTECTOR.encode(out) + CODE_BLOCK_END + "\n";
    }

    private String classesBlock(String classes, String text) {
        Pattern p = compile("\\[(?:@(?<preClasses>\\p{Alpha}[^\\]]*)?)?\\]\\[(?:@(?<codeClasses>\\p{Alpha}[^\\]]*)?)?\\]");
        Matcher m2 = p.matcher(classes);

        if (m2.find())
        {
            String pre = "<pre" + addId(m.group("id")) + addClass(m2.group("preClasses")) + ">\n";
            String code = "    <code" + addClass(m2.group("codeClasses")) + ">\n";

            return pre + code + text + "\n    </code>\n</pre>";
        } else
        {
            return genericCodeBlock(text);
        }
    }

    private String genericCodeBlock(String text) {
        return "<pre" + addId(m.group("id")) + ">\n    <code>\n" + text + "\n    </code>\n</pre>";
    }

    private String languageBlock(String clazz, String text) {
        String lang = clazz.replaceFirst(LANG_IDENTIFIER, "").trim();

        return "<pre" + addId(m.group("id")) + addClass(lang) + ">\n    <code>\n" + text + "\n    </code>\n</pre>";
    }

    private void unHashBlocks(TextEditor ed) {
        Matcher mLocal = Pattern.compile(CharacterProtector.FIND_ENCODED, MULTILINE).matcher(ed.toString());

        while (mLocal.find())
        {
            String encoded = mLocal.group("encoded");
            String decoded = HTML_PROTECTOR.decode(encoded);

            if (decoded != null)
            {
                ed.replaceAllLiteral(encoded, decoded);
            }
        }
    }
}
