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

import com.bewsoftware.mdj.core.Replacement;
import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.mdj.core.utils.LinkDefinition;
import com.bewsoftware.utils.Ref;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.doSubSup;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.protectEmphasis;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.replaceAll;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addId;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.*;
import static com.bewsoftware.utils.string.Strings.isBlank;
import static com.bewsoftware.utils.string.Strings.notBlank;
import static com.bewsoftware.utils.string.Strings.notEmpty;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

/**
 * Build various types of anchors.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class Anchors implements TextConvertor
{
    public Anchors()
    {
    }

    @Override
    public TextEditor execute(final TextEditor text)
    {
        processInternalLinks(text);
        processInlineLinks(text);
        processReferenceShortcuts(text);
        return text;
    }

    private void finalizeReplacementText(
            final StringBuilder replacementText,
            final String idAttrib,
            final String classAtrib,
            final String titleTag,
            final String targetTag,
            final String linkText
    )
    {
        replacementText.append(idAttrib).append(classAtrib).append(titleTag)
                .append(targetTag).append(">").append(linkText).append("</a>");
    }

    private void processInlineLinks(final TextEditor text)
    {
        text.replaceAll(InlineLink.PATTERN, new InlineLink());
    }

    private void processInternalLinks(final TextEditor text)
    {
        text.replaceAll(InternalLink.PATTERN, new InternalLink());
    }

    private void processReferenceShortcuts(final TextEditor text)
    {
        text.replaceAll(ReferenceShortcut.PATTERN, new ReferenceShortcut());
    }

    private String processTitle(final LinkDefinition defn)
    {
        String title = defn.title;
        String titleTag = "";

        if (notBlank(title))
        {
            title = protectEmphasis(title);
            titleTag = " title=\"" + title + "\"";
        }

        return titleTag;
    }

    private void processUrl(final LinkDefinition defn, final StringBuilder replacementText)
    {
        String url = defn.url;

        if (url != null)
        {
            url = protectEmphasis(url);
            replacementText.append(" href=\"").append(url).append("\"");
        }
    }

    private class InlineLink implements Replacement
    {
        // Inline-style links: [link text]!( url "optional title")
        //
        // The space before and after the 'url' is now required.
        // This is to allow for a url of: "javascript:void(0)".
        // The closing braket ')' was being grabbed by the end of
        // the regex pattern.
        //
        // Added class attribute to anchors.
        // [link text]![@class]( url "optional title")
        //
        // Bradley Willcott (15/12/2020)
        //
        public static final Pattern PATTERN = compile(
                // Whole match = $1
                "\\[(?<linkText>[^\\[\\]]*?)\\]"
                // Link text = $2
                + "(?<target>!)?"
                + CLASS_REGEX_OPT// BW
                + "\\("
                + "[ ]*"
                + "<?(?<url>.*?)>?"
                // href = $4
                + "[ ]*"
                + "("
                + "(?<quote>['\"])"
                // Quote character = $6
                + "(?<title>.*?)"
                // Title = $7
                + "\\k<quote>"
                + ")?"
                + "\\)", DOTALL);

        private InlineLink()
        {
        }

        @Override
        public String process(final Matcher m)
        {
            final String linkText = m.group("linkText");
            final String url = m.group("url");
            final String title = m.group("title");
            final String target = m.group("target");
            final String classes = m.group("classes");
            final StringBuilder result = new StringBuilder("<a");

            processUrl(url, result);
            processClasses(classes, result);
            processTitle(title, result);
            processTarget(target, result);
            finalizeResult(result, linkText);

            return result.toString();
        }

        private void appendTitle(final StringBuilder result, final String title)
        {
            result.append(" title=\"");
            result.append(title);
            result.append("\"");
        }

        private void finalizeResult(final StringBuilder result, final String linkText)
        {
            result.append(">").append(linkText);
            result.append("</a>");
        }

        private void processClasses(final String classes, final StringBuilder result)
        {
            final String classAtrib = notBlank(classes) ? addClass(classes) : "";

            result.append(classAtrib);
        }

        private void processTarget(final String target, final StringBuilder result)
        {
            if (notEmpty(target))
            {
                result.append(TARGET);
            }
        }

        private void processTitle(final String title, final StringBuilder result)
        {
            if (title != null)
            {
                final String pTitle = protectEmphasis(title);
                final String rpTitle = replaceAll(pTitle, "\"", "&quot;");
                appendTitle(result, rpTitle);
            }
        }

        private void processUrl(final String url, final StringBuilder result)
        {
            if (url != null)
            {
                final String pUrl = protectEmphasis(url);
                result.append(" href=\"").append(pUrl).append("\"");
            }
        }
    }

    private class InternalLink implements Replacement
    {
        // Internal references: [link text][id]!
        //
        // Added class attribute to anchors.
        // [link text][id]![@class]
        //
        // Bradley Willcott (15/12/2020)
        //
        public static final Pattern PATTERN = compile(
                "\\[(?<linkText>[^\\[\\]]*?)\\]"
                // Link text = $1
                // + "[ ]?(?:\\n[ ]*)?"  // No whitespace between the brackets (GFM)
                + "\\[(?<linkId>[^\\]]*?)\\]"
                // ID = $2
                + "(?<target>!)?"
                + CLASS_REGEX_OPT); // BW

        private InternalLink()
        {
        }

        @Override
        public String process(final Matcher m)
        {
            final String replacementText;
            final String wholeMatch = m.group();
            final String linkText = m.group("linkText");
            final String linkId = m.group("linkId");
            final String targetTag = m.group("target") != null ? TARGET : "";
            final String classes = m.group("classes");

            final String pLinkId = processLinkId(linkId, linkText);
            final LinkDefinition defn = linkDefinitions.get(pLinkId);

            if (defn != null)
            {
                replacementText = processLinkDefinition(defn, classes, targetTag, linkText);
            } else
            {
                replacementText = wholeMatch;
            }

            return replacementText;
        }

        private String processLinkDefinition(
                final LinkDefinition defn,
                final String classes,
                final String targetTag,
                final String linkText
        )
        {
            final StringBuilder replacementText = new StringBuilder("<a");
            final String classAtrib = addClass(defn.classes, classes);
            final String titleTag = processTitle(defn);

            processUrl(defn, replacementText);
            finalizeReplacementText(replacementText, "", classAtrib, titleTag, targetTag, linkText);

            return replacementText.toString();
        }

        private String processLinkId(final String linkId, final String linkText)
        {
            return isBlank(linkId) ? linkText : linkId;
        }
    }

    private class ReferenceShortcut implements Replacement
    {
        // Last, handle reference-style shortcuts: [link text]!
        // These must come last in case you've also got [link test][lt]
        // or [link test](/foo)
        //
        // Added class attribute to anchors.
        // [link text]![@class]
        //
        // Bradley Willcott (15/12/2020)
        //
        // Added negative forward look for:
        // - '[#'  - id attribute,
        // - '[\d' - Table border width.
        //
        // Added acceptable character sequence:
        // - '[^'  - Footnote link.
        // Bradley Willcott (03/01/2021)
        //
        // Added optional 'id'
        // Bradley Willcott (19/01/2021)
        //
        public static final Pattern PATTERN = compile(
                "(?!\\[#|\\[\\d)" // BW:
                + "\\["
                + "(?<footnote>\\^)?" // BW:
                + "(?<linkText>[^\\[\\]]+?)"
                // link text can't contain ']'
                + "\\]"
                + "(?<target>!)?"
                + ID_REGEX_OPT // BW
                + CLASS_REGEX_OPT, // BW
                DOTALL);

        private ReferenceShortcut()
        {
        }

        @Override
        public String process(final Matcher m)
        {
            final String replacementText;
            final String wholeMatch = m.group();
            final String footnote = m.group("footnote");
            final String linkText = m.group("linkText");
            final String targetTag = m.group("target") != null ? TARGET : "";
            final String id = m.group("id");
            // link id is now case sensitive
            final String linkId = linkText.replaceAll("[ ]?\\n", " "); // change embedded newlines into spaces
            final String classes = m.group("classes");

            final LinkDefinition defn = linkDefinitions.get(linkId);

            if (defn != null)
            {
                replacementText = processLinkDefinition(defn, classes, footnote, linkText, id, targetTag);
            } else
            {
                replacementText = wholeMatch;
            }

            return replacementText;
        }

        private void appendUrl(final String url, final StringBuilder replacementText)
        {
            if (url != null)
            {
                replacementText.append(" href=\"").append(url).append("\"");
            }
        }

        //TODO: Anchors.processFootnote(...) - footnote not processed?
        private String processFootnote(
                final String footnote,
                final Ref<String> linkTextRef,
                final String url,
                final String idAttrib,
                final String id
        )
        {
            String rtn = idAttrib;

            if (footnote != null)
            {
                linkTextRef.val = processFootnoteLink(linkTextRef.val, url);
                rtn = processId(id, idAttrib);
            }

            return rtn;
        }

        private String processFootnoteLink(final String linkText, final String url)
        {
            String rtn = linkText;
            final Matcher footnote = compile("\\A(\\d+)\\z").matcher(linkText);

            if (footnote.find())
            {
                rtn = doSubSup(
                        new TextEditor("++" + CHAR_PROTECTOR.encode("[")
                                + (url != null ? linkText : "*") + "]++")
                ).toString();
            }

            return rtn;
        }

        private String processId(final String id, final String idAttrib)
        {
            return notBlank(id) ? addId(id) : idAttrib;
        }

        private String processLinkDefinition(
                final LinkDefinition defn,
                final String classes,
                final String footnote,
                final String linkText,
                final String id,
                final String targetTag)
        {
            final StringBuilder replacementText = new StringBuilder("<a");
            final String url = processUrl(defn);
            final String classAtrib = addClass(defn.classes, classes);
            final String titleTag = processTitle(defn);
            String idAttrib = "";
            final Ref<String> linkTextRef = Ref.val(linkText);

            idAttrib = processFootnote(footnote, linkTextRef, url, idAttrib, id);
            appendUrl(url, replacementText);
            finalizeReplacementText(replacementText, idAttrib, classAtrib, titleTag,
                    targetTag, linkTextRef.val);

            return replacementText.toString();
        }

        private String processUrl(final LinkDefinition defn)
        {
            String url = defn.url;

            if (url != null)
            {
                url = protectEmphasis(url);
            }

            return url;
        }
    }
}
