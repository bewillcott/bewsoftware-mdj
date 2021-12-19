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
import com.bewsoftware.utils.struct.Ref;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.doSubSup;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.protectEmphasis;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.replaceAll;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addId;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.*;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

/**
 * Build various types of anchors.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public class Anchors implements TextConvertor
{
    public Anchors()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        processInternalLinks(text);
        processInlineLinks(text);
        processReferenceShortcuts(text);
        return text;
    }

    private void finalizeReplacementText(StringBuilder replacementText, String idAttrib,
            String classAtrib, String titleTag, String targetTag, String linkText)
    {
        replacementText.append(idAttrib).append(classAtrib).append(titleTag)
                .append(targetTag).append(">").append(linkText).append("</a>");
    }

    private void processInlineLinks(TextEditor text)
    {
        text.replaceAll(InlineLink.PATTERN, new InlineLink());
    }

    private void processInternalLinks(TextEditor text)
    {
        text.replaceAll(InternalLink.PATTERN, new InternalLink());
    }

    private void processReferenceShortcuts(TextEditor text)
    {
        text.replaceAll(ReferenceShortcut.PATTERN, new ReferenceShortcut());
    }

    private String processTitle(LinkDefinition defn)
    {
        String title = defn.title;
        String titleTag = "";

        if (title != null && !title.isBlank())
        {
            title = protectEmphasis(title);
            titleTag = " title=\"" + title + "\"";
        }

        return titleTag;
    }

    private void processUrl(LinkDefinition defn, StringBuilder replacementText)
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
        public String process(Matcher m)
        {
            String linkText = m.group("linkText");
            String url = m.group("url");
            String title = m.group("title");
            String target = m.group("target");
            String classes = m.group("classes");
            StringBuilder result = new StringBuilder("<a");

            processUrl(url, result);
            processClasses(classes, result);
            processTitle(title, result);
            processTarget(target, result);
            finalizeResult(result, linkText);

            return result.toString();
        }

        private void appendTitle(StringBuilder result, String title)
        {
            result.append(" title=\"");
            result.append(title);
            result.append("\"");
        }

        private void finalizeResult(StringBuilder result, String linkText)
        {
            result.append(">").append(linkText);
            result.append("</a>");
        }

        private void processClasses(String classes, StringBuilder result)
        {
            String classAtrib = classes != null && !classes.isBlank()
                    ? addClass(classes) : "";
            result.append(classAtrib);
        }

        private void processTarget(String target, StringBuilder result)
        {
            if (target != null && !target.isEmpty())
            {
                result.append(TARGET);
            }
        }

        private void processTitle(String title, StringBuilder result)
        {
            if (title != null)
            {
                title = protectEmphasis(title);
                title = replaceAll(title, "\"", "&quot;");
                appendTitle(result, title);
            }
        }

        private void processUrl(String url, StringBuilder result)
        {
            if (url != null)
            {
                url = protectEmphasis(url);
                result.append(" href=\"").append(url).append("\"");
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
        public String process(Matcher m)
        {
            String replacementText;
            String wholeMatch = m.group();
            String linkText = m.group("linkText");
            String linkId = m.group("linkId");
            String targetTag = m.group("target") != null ? TARGET : "";
            String classes = m.group("classes");

            linkId = processLinkId(linkId, linkText);
            LinkDefinition defn = linkDefinitions.get(linkId);

            if (defn != null)
            {
                replacementText = processLinkDefinition(defn, classes, targetTag, linkText);
            } else
            {
                replacementText = wholeMatch;
            }

            return replacementText;
        }

        private String processLinkId(String linkId, String linkText)
        {
            if (linkId == null || linkId.isBlank())
            {
                linkId = linkText;
            }

            return linkId;
        }

        private String processLinkDefinition(LinkDefinition defn, String classes,
                String targetTag, String linkText)
        {
            StringBuilder replacementText = new StringBuilder("<a");
            String classAtrib = addClass(defn.classes, classes);
            String titleTag = processTitle(defn);

            processUrl(defn, replacementText);
            finalizeReplacementText(replacementText, "", classAtrib, titleTag, targetTag, linkText);

            return replacementText.toString();
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
        public String process(Matcher m)
        {
            String replacementText;
            String wholeMatch = m.group();
            String footnote = m.group("footnote");
            String linkText = m.group("linkText");
            String targetTag = m.group("target") != null ? TARGET : "";
            String id = m.group("id");
            String linkId = linkText; // link id is now case sensitive
            linkId = linkId.replaceAll("[ ]?\\n", " "); // change embedded newlines into spaces
            String classes = m.group("classes");

            LinkDefinition defn = linkDefinitions.get(linkId);

            if (defn != null)
            {
                replacementText = processLinkDefinition(defn, classes, footnote,
                        linkText, id, targetTag);
            } else
            {
                replacementText = wholeMatch;
            }

            return replacementText;
        }

        private String processLinkDefinition(LinkDefinition defn, String classes,
                String footnote, final String linkText, String id, String targetTag)
        {
            StringBuilder replacementText = new StringBuilder("<a");
            String url = processUrl(defn);
            String classAtrib = addClass(defn.classes, classes);
            String titleTag = processTitle(defn);
            String idAttrib = "";
            Ref<String> linkTextRef = Ref.val(linkText);

            idAttrib = processFootnote(footnote, linkTextRef, url, idAttrib, id);
            appendUrl(url, replacementText);
            finalizeReplacementText(replacementText, idAttrib, classAtrib, titleTag,
                    targetTag, linkTextRef.val);

            return replacementText.toString();
        }

        private String processFootnote(String footnote, Ref<String> linkTextRef,
                String url, String idAttrib, String id)
        {
            if (footnote != null)
            {
                linkTextRef.val = processFootnoteLink(linkTextRef.val, url);
                idAttrib = processId(id, idAttrib);
            }

            return idAttrib;
        }

        private void appendUrl(String url, StringBuilder replacementText)
        {
            if (url != null)
            {
                replacementText.append(" href=\"").append(url).append("\"");
            }
        }

        private String processUrl(LinkDefinition defn)
        {
            String url = defn.url;

            if (url != null)
            {
                url = protectEmphasis(url);
            }

            return url;
        }

        private String processId(String id, String idAttrib)
        {
            if (id != null)
            {
                idAttrib = addId(id);
            }

            return idAttrib;
        }

        private String processFootnoteLink(String linkText, String url)
        {
            Matcher footnote = compile("\\A(\\d+)\\z").matcher(linkText);

            if (footnote.find())
            {
                linkText = doSubSup(new TextEditor("++" + CHAR_PROTECTOR.encode("[")
                        + (url != null ? linkText : "*") + "]++"))
                        .toString();
            }

            return linkText;
        }
    }
}
