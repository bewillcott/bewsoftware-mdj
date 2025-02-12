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

import com.bewsoftware.mdj.core.TextEditor;

/**
 * This class acts as a facade to the plugins.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class PluginInterlink
{
    private static final PluginController PLUGIN_CONTROLLER = new PluginController();

    private PluginInterlink()
    {
    }

    public static TextEditor cleanupMarkdownText(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("CleanupMarkdownText", text);
    }

    public static TextEditor doAnchors(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("Anchors", text);
    }

    public static TextEditor doAutoLinks(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("AutoLinks", text);
    }

    public static TextEditor doBlockQuotes(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("BlockQuotes", text);
    }

    public static TextEditor doCodeBlocks(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("CodeBlocks", text);
    }

    public static TextEditor doCodeSpans(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("CodeSpans", text);
    }

    public static TextEditor doDelIns(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("DelIns", text);
    }

    public static TextEditor doFencedCodeBlocks(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("FencedCodeBlocks", text);
    }

    public static TextEditor doHeaders(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("Headers", text);
    }

    public static TextEditor doHorizontalRules(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("HorizontalRules", text);
    }

    public static TextEditor doImages(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("Images", text);
    }

    public static TextEditor doLists(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("Lists", text);
    }

    public static TextEditor doStrongEmAndBoldItalics(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("StrongEmAndBoldItalics", text);
    }

    public static TextEditor doSubSup(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("SubSup", text);
    }

    public static TextEditor doTables(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("Tables", text);
    }

    public static TextEditor encodeAmpsAndAngles(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("EncodeAmpsAndAngles", text);
    }

    public static TextEditor encodeBackslashEscapes(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("EncodeBackslashEscapes", text);
    }

    public static TextEditor encodeCode(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("EncodeCode", text);
    }

    public static TextEditor escapeSpecialCharsWithinTagAttributes(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("EscapeSpecialCharsWithinTagAttributes", text);
    }

    public static TextEditor formParagraphs(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("FormParagraphs", text);
    }

    public static TextEditor hashHTMLBlocks(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("HashHTMLBlocks", text);
    }

    public static TextEditor runBlockGamut(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runBlockGamut(text);
    }

    public static TextEditor runSpanGamut(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runSpanGamut(text);
    }

    public static TextEditor stripLinkDefinitions(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("StripLinkDefinitions", text);
    }

    public static TextEditor unEscapeSpecialChars(final TextEditor text)
    {
        return PLUGIN_CONTROLLER.runPlugin("UnEscapeSpecialChars", text);
    }
}
