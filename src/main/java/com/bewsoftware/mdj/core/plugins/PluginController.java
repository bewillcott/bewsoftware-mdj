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
import com.bewsoftware.utils.Ref;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class instantiates all the plugins and stores them for later use.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class PluginController
{
    private static final String[] BLOCK_GAMIT_PLUGINS =
    {
        "FencedCodeBlocks",
        "Headers",
        "Tables",
        "HorizontalRules",
        "Lists",
        "CodeBlocks",
        "BlockQuotes",
        // We already ran 'HashHTMLBlocks' before, in convert(), but that
        // was to escape raw HTML in the original Markdown source. This time,
        // we're escaping the text we've just created, so that we don't wrap
        // <p> tags around block-level tags.
        "HashHTMLBlocks",
        "FormParagraphs"
    };

    private static final String[] MISC_PLUGINS =
    {
        "CleanupMarkdownText",
        "EncodeCode",
        "StripLinkDefinitions",
        "UnEscapeSpecialChars"
    };

    private static final String PLUGIN_PACKAGE = "com.bewsoftware.mdj.core.plugins.";

    private static final String[] SPAN_GAMIT_PLUGINS =
    {
        "EscapeSpecialCharsWithinTagAttributes",
        "CodeSpans",
        "EncodeBackslashEscapes",
        "SubSup",
        "DelIns",
        "Images",
        "Anchors",
        "AutoLinks",
        // Fix for BUG #1357582
        // We must run 'EscapeSpecialCharsWithinTagAttributes' a second time to
        // escape the contents of any attributes generated by the prior methods.
        // - Nathan Winant, nw@exegetic.net, 8/29/2006
        "EscapeSpecialCharsWithinTagAttributes",
        "EncodeAmpsAndAngles",
        "StrongEmAndBoldItalics"
    };

    private final List<String> blockGamitList = Arrays.asList(BLOCK_GAMIT_PLUGINS);

    private final ConcurrentHashMap<String, TextConvertor> plugins;

    private final List<String> spanGamitList = Arrays.asList(SPAN_GAMIT_PLUGINS);

    public PluginController()
    {
        this.plugins = new ConcurrentHashMap<>();
        final SortedSet<String> classNames = new TreeSet<>();

        classNames.addAll(blockGamitList);
        classNames.addAll(spanGamitList);
        classNames.addAll(Arrays.asList(MISC_PLUGINS));

        classNames.stream().forEachOrdered((className) ->
        {
            try
            {
                final TextConvertor tc = (TextConvertor) Class.forName(PLUGIN_PACKAGE + className)
                        .getDeclaredConstructor().newInstance();

                plugins.put(className, tc);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
                    | InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex)
            {
                Logger.getLogger(PluginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /**
     * Run all block level processing.
     *
     * @param text
     *
     * @return
     */
    public TextEditor runBlockGamut(final TextEditor text)
    {
        return runGamit(blockGamitList, text);
    }

    public TextEditor runPlugin(final String className, final TextEditor text)
    {
        final TextConvertor tc = plugins.get(className);

        return tc != null ? tc.execute(text) : text;
    }

    /**
     * Run &lt;span&gt; level processing.
     *
     * @param text
     *
     * @return
     */
    public TextEditor runSpanGamut(final TextEditor text)
    {
        final TextEditor gText = runGamit(spanGamitList, text);

        return gText.replaceAll(" {2,}\n", " <br>\n");
    }

    private TextEditor runGamit(final List<String> gamit, final TextEditor text)
    {
        final Ref<TextEditor> textRef = Ref.val(text);

        gamit.stream().forEachOrdered(className
                -> textRef.val = runPlugin(className, textRef.val)
        );

        return textRef.val;
    }
}
