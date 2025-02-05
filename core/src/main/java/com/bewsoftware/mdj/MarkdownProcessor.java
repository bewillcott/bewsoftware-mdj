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
 *
 */
package com.bewsoftware.mdj;

import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.mdj.core.plugins.PluginInterlink;

/**
 * Convert Markdown text into HTML.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 0.7.5
 */
public interface MarkdownProcessor
{

//    static final String TAG_CLASS = "\\[@(\\p{Alpha}[^\\]]*?)(\\b\\p{Alpha}[^\\]]*?)*?\\]";
    /**
     * Perform the conversion from Markdown to HTML.
     *
     * @param markdown - text in markdown format
     *
     * @return HTML block converted from the markdown text passed in.
     */
    static String convert(final String markdown)
    {
        String rtn = "\n";

        if (markdown != null && !markdown.isBlank())
        {
            TextEditor text = new TextEditor(markdown);

            cleanupMarkdownText(text);
            hashHTMLBlocks(text);
            stripLinkDefinitions(text);
            text = runBlockGamut(text);
            unEscapeSpecialChars(text);
            text.append("\n");
            rtn = text.toString();
        }

        return rtn;
    }

    private static TextEditor cleanupMarkdownText(final TextEditor text)
    {
        return PluginInterlink.cleanupMarkdownText(text);
    }

    private static TextEditor hashHTMLBlocks(final TextEditor text)
    {
        return PluginInterlink.hashHTMLBlocks(text);
    }

    private static TextEditor runBlockGamut(final TextEditor text)
    {
        return PluginInterlink.runBlockGamut(text);
    }

    private static TextEditor stripLinkDefinitions(final TextEditor text)
    {
        return PluginInterlink.stripLinkDefinitions(text);
    }

    private static TextEditor unEscapeSpecialChars(final TextEditor text)
    {
        return PluginInterlink.unEscapeSpecialChars(text);
    }
}
