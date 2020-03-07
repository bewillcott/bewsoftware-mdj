/*
 * Copyright (c) 2005, Martian Software
 * Authors: Pete Bevin, John Mutchek
 * http://www.martiansoftware.com/markdownj
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
package org.markdownj;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;
import static org.markdownj.Attributes.addClass;
import static org.markdownj.Attributes.addId;
import static org.markdownj.Attributes.addStyle;

/**
 * Convert Markdown text into HTML, as per
 * http://daringfireball.net/projects/markdown/ . Usage:
 * <pre><code>
 *     MarkdownProcessor markdown = new MarkdownProcessor();
 *     String html = markdown.markdown("*italic*   **bold**\n_italic_   __bold__");
 * </code></pre>
 */
public class MarkdownProcessor {

    private static final CharacterProtector CHAR_PROTECTOR = new CharacterProtector();
    private static final CharacterProtector HTML_PROTECTOR = new CharacterProtector();
    private static final String LANG_IDENTIFIER = "lang:";
    private static final String TARGET = " target=\"" + CHAR_PROTECTOR.encode("_") + "blank\"";
    private static final String CODE_BLOCK_BEGIN = "-=: ";
    private static final String CODE_BLOCK_END = " :=-";
//    private static final String ID_REGEX = "(?:\\[#(?<id>[^\\s\\]]*)?\\])?";
    private static final String ID_REGEX = "(?:\\[#(?<id>\\w[\\w.]*)\\])?";
    private final Map<String, LinkDefinition> linkDefinitions = new TreeMap<>();
    private int listLevel;
    private final Random rnd = new Random();
    private final int tabWidth = 4;
    private final String[] radioGroupNames = new String[100];

    /**
     * Creates a new Markdown processor.
     */
    public MarkdownProcessor() {
        listLevel = 0;
    }

    /**
     * Perform the conversion from Markdown to HTML.
     *
     * @param txt - input in markdown format
     *
     * @return HTML block corresponding to txt passed in.
     */
    public String markdown(String txt) {
        if (txt == null) {
            txt = "";
        }

        TextEditor text = new TextEditor(txt);

        // Standardize line endings:
        text.replaceAll("\\r\\n", "\n"); 	// DOS to Unix
        text.replaceAll("\\r", "\n");    	// Mac to Unix

        // Strip any lines consisting only of spaces and tabs.
        // This makes subsequent regexen easier to write, because we can
        // match consecutive blank lines with /\n+/ instead of something
        // contorted like /[ \t]*\n+/ .
        text.replaceAll("^[ \\t]+$", "");

        // Make sure $text ends with a couple of newlines:
        text.append("\n\n");

        text.detabify();
        text.deleteAll("^[ ]+$");

        // Turn block-level HTML blocks into hash entries
        hashHTMLBlocks(text);
        // Strip link definitions, store in hashes.
        stripLinkDefinitions(text);

        text = runBlockGamut(text);

        unEscapeSpecialChars(text);

        text.append("\n");
        return text.toString();
    }

    @Override
    public String toString() {
        return "Markdown Processor for Java 0.4.0 (compatible with Markdown 1.0.2b2)";
    }

    private String deleteAll(String text, String regex) {
        return replaceAll(text, regex, "");
    }

    private TextEditor doAnchors(TextEditor markup) {
        // Internal references: [link text][id]!
        Pattern internalLink = compile(""
                                       + "\\[(?<linkText>[^\\[\\]]*?)\\]"
                                       // Link text = $1
                                       // + "[ ]?(?:\\n[ ]*)?"  // No whitespace between the brackets (GFM)
                                       + "\\[(?<linkId>[^\\]]*?)\\]"
                                       // ID = $2
                                       + "(?<target>!)?");
        markup.replaceAll(internalLink, (Matcher m) -> {
                      String replacementText;
                      String wholeMatch = m.group();
                      String linkText = m.group("linkText");
//                      String id = m.group(3).toLowerCase();
                      String linkId = m.group("linkId");
                      String targetTag = m.group("target") != null ? TARGET : "";

                      // [linkId] is now case sensitive
                      if (linkId == null || "".equals(linkId)) { // for shortcut links like [this][]
//                          id = linkText.toLowerCase();
                          linkId = linkText;
                      }

                      LinkDefinition defn = linkDefinitions.get(linkId);

                      if (defn != null) {
                          String url = defn.getUrl();
                          // protect emphasis (* and _) within urls
                          url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                          url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                          String title = defn.getTitle();
                          String titleTag = "";

                          if (title != null && !title.equals("")) {
                              // protect emphasis (* and _) within urls
                              title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                              title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                              titleTag = " title=\"" + title + "\"";
                          }

                          replacementText = "<a href=\"" + url + "\"" + titleTag + targetTag + ">" + linkText + "</a>";
                      } else {
                          replacementText = wholeMatch;
                      }

                      return replacementText;
                  });

        // Inline-style links: [link text]!(url "optional title")
        Pattern inlineLink = compile(""
                                     // Whole match = $1
                                     + "\\[(?<linkText>[^\\[\\]]*?)\\]"
                                     // Link text = $2
                                     + "(?<target>!)?"
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
        markup.replaceAll(inlineLink, (Matcher m) -> {
                      String linkText = m.group("linkText");
                      String url = m.group("url");
                      String title = m.group("title");
                      String targetTag = m.group("target") != null ? TARGET : "";

                      // protect emphasis (* and _) within urls
                      url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                      url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                      StringBuilder result = new StringBuilder();
                      result.append("<a href=\"").append(url).append("\"");

                      if (title != null) {
                          // protect emphasis (* and _) within urls
                          title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                          title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                          title = replaceAll(title, "\"", "&quot;");
                          result.append(" title=\"");
                          result.append(title);
                          result.append("\"");
                      }

                      if (!targetTag.isEmpty()) {
                          result.append(TARGET);
                      }

                      result.append(">").append(linkText);
                      result.append("</a>");

                      return result.toString();
                  });

        // Last, handle reference-style shortcuts: [link text]!
        // These must come last in case you've also got [link test][1]
        // or [link test](/foo)
        Pattern referenceShortcut = compile(""
                                            // wrap whole match in $1
                                            + "\\["
                                            + "(?<linkText>[^\\[\\]]+?)"
                                            // link text = $2; can't contain '[' or ']'
                                            + "\\]"
                                            + "(?<target>!)?", DOTALL);
        markup.replaceAll(referenceShortcut, (Matcher m) -> {
                      String replacementText;
                      String wholeMatch = m.group();
                      String linkText = m.group("linkText");
//                      String id = m.group(2).toLowerCase(); // link id should be lowercase
                      String targetTag = m.group("target") != null ? TARGET : "";
                      String id = linkText; // link id is now case sensitive
                      id = id.replaceAll("[ ]?\\n", " "); // change embedded newlines into spaces

                      LinkDefinition defn = linkDefinitions.get(id);

                      if (defn != null) {
                          String url = defn.getUrl();
                          // protect emphasis (* and _) within urls
                          url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                          url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                          String title = defn.getTitle();
                          String titleTag = "";

                          if (title != null && !title.equals("")) {
                              // protect emphasis (* and _) within urls
                              title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                              title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                              titleTag = " title=\"" + title + "\"";
                          }
                          replacementText = "<a href=\"" + url + "\"" + titleTag + targetTag + ">" + linkText + "</a>";
                      } else {
                          replacementText = wholeMatch;
                      }

                      return replacementText;
                  });

        return markup;
    }

    private TextEditor doAutoLinks(TextEditor markup) {
//        markup.replaceAll("<(?<(?:https?|ftp):[^'\">\\s]+)>", "<a href=\"$1\">$1</a>");
        Pattern link = Pattern.compile("<"
                                       + "(?<url>(?:https?|ftp):[^'\">\\s]+)"
                                       + ">"
                                       + "(?<target>!)?");

        markup.replaceAll(link, (Matcher m) -> {
                      String url = m.group("url");
                      String targetTag = m.group("target") != null ? TARGET : "";

                      return "<a href=\"" + url + "\"" + targetTag + ">" + url + "</a>";
                  });

        Pattern email = compile("<([-.\\w]+\\@[-a-z0-9]+(?:\\.[-a-z0-9]+)*\\.[a-z]+)>");
        markup.replaceAll(email, (Matcher m) -> {
                      String address = m.group(1);
                      TextEditor ed = new TextEditor(address);
                      unEscapeSpecialChars(ed);
                      String addr = encodeEmail(ed.toString());
                      String url = encodeEmail("mailto:" + ed.toString());
                      return "<a href=\"" + url + "\">" + addr + "</a>";
                  });

        return markup;
    }

    private TextEditor doBlockQuotes(TextEditor markup) {
        Pattern p = compile("("
                            + "("
                            + "^[ \t]*>[ \t]?"
                            // > at the start of a line
                            + ".+\\n"
                            // rest of the first line
                            + "(.+\\n)*"
                            // subsequent consecutive lines
                            + "\\n*"
                            // blanks
                            + ")+"
                            + ")", MULTILINE);
        return markup.replaceAll(p, (Matcher m) -> {
                             TextEditor blockQuote = new TextEditor(m.group(1));
                             blockQuote.deleteAll("^[ \t]*>[ \t]?");
                             blockQuote.deleteAll("^[ \t]+$");
                             blockQuote = runBlockGamut(blockQuote);
                             blockQuote.replaceAll("^", "  ");

                             Pattern p1 = compile("(\\s*<pre>.*?</pre>)", DOTALL);
                             blockQuote = blockQuote.replaceAll(p1, (Matcher m1) -> {
                                                            String pre = m1.group(1);
                                                            return deleteAll(pre, "^  ");
                                                        });

                             return "<blockquote>\n" + blockQuote + "\n</blockquote>\n\n";
                         });
    }

    private TextEditor doCodeBlocks(TextEditor markup) {
        /*
         * If there are any Fenced Code Blocks, they will have been protected
         * from 'p2' by HTML_PROTECTOR.encode(..). This prevents 'p2' from
         * removing 4 spaces from the beginning of each line of the Fenced Code
         * Blocks.
         *
         * Any tabbed or space type block will still be processed by 'p2'.
         *
         * Bradley Willcott (8/1/2020)
         */
        Pattern p2 = compile("(?:(?<id>)?(?<classes>)?(?<=\\n\\n)|\\A)"
                             + "(?:^(?:> )*?[ ]{4})?(?<class>" + LANG_IDENTIFIER + ".+)?(?:\\n)?"
                             + "(?<body>(?:"
                             + "(?:^(?:> )*?[ ]{4}).*\\n+)+)"
                             //                                     + "(?:(?=^[ ]{0,4}\\S)|\\Z)", MULTILINE);
                             + "(?:(?=^\\n+)|\\Z)", MULTILINE);
        return markup.replaceAll(p2, new CodeBlockReplacement(false));
    }

    private TextEditor doFencedCodeBlocks(TextEditor markup) {
        /*
         * Fenced Code Blocks: Using either ``` triple back-ticks, or ~~~ triple
         * tildes for beginning and end of code block.
         *
         * Can add classes to both the <pre> and <code> tags:
         *
         * ```[pre tag classes][code tag classes]
         *
         * Example:
         *
         * ~~~[prettyprint][language-java]
         *
         * Result:
         *
         * <pre class="prettyprint"><code class="language-java">
         *
         * Bradley Willcott (7/1/2020)
         */
        Pattern p1 = compile("(?<frontFence>^(?:[~]{3}|[`]{3}))"
                             //                             + "(?:\\[#(?<id>[^\\s\\]]*)?\\])?"
                             + ID_REGEX
                             + "(?:[ ]*\\n"
                             + "|(?<classes>\\[[^#]?[^\\]]*\\]\\[[^#]?[^\\]]*\\])?[ ]*\\n"
                             + "|(?<class>" + LANG_IDENTIFIER + ".+)?\\n)?"
                             + "(?<body>(?:.*?\\n+)+?)"
                             + "(?:\\k<frontFence>)[ ]*\\n", MULTILINE);
        return markup.replaceAll(p1, new CodeBlockReplacement(true));
    }

    private TextEditor doCodeSpans(TextEditor markup) {
//        return markup.replaceAll(compile("(?<!\\\\)(`+)(.+?)(?<!`)\\1(?!`)"), (Matcher m) -> {
        return markup.replaceAll(compile("(?<!\\\\)(`+)(.+?)(?<!`)\\1(?!`)"), (Matcher m) -> {
                             String code = m.group(2);
                             TextEditor subEditor = new TextEditor(code);
                             subEditor.deleteAll("^[ \\t]+").deleteAll("[ \\t]+$");
                             encodeCode(subEditor);
                             return "<code>" + subEditor.toString() + "</code>";
                         });
    }

    private TextEditor doHeaders(TextEditor markup) {
        // setext-style headers
        markup.replaceAll("^(.*)\n[=]{4,}$", "<h1>$1</h1>");
        markup.replaceAll("^(.*)\n[-]{4,}$", "<h2>$1</h2>");

        // atx-style headers - e.g., "#### heading 4 ####"
//        Pattern p = compile("^(?<marker>#{1,6})(?:\\[#(?<id>[^\\s\\]]*)?\\])?[ ]*(?<heading>.*?)[ ]*(?:\\k<marker>[ ]*(?<tail>.*)?)?$", MULTILINE);
        Pattern p = compile("^(?<marker>#{1,6})" + ID_REGEX + "[ ]*(?<heading>.*?)[ ]*(?:\\k<marker>[ ]*(?<tail>.*)?)?$", MULTILINE);
        markup.replaceAll(p, (Matcher m) -> {
                      String marker = m.group("marker");
                      String id = m.group("id");
                      String heading = m.group("heading");
                      String tail = m.group("tail");

                      if (id != null) {
                          id = addId(id);
                      } else {
                          id = "";
                      }

                      if (tail != null && !tail.isBlank()) {
                          tail = " " + doAnchors(new TextEditor(tail)).toString();
                      } else {
                          tail = "";
                      }

                      int level = marker.length();
                      String tag = "h" + level;
                      return "<" + tag + id + ">" + heading + tail + "</" + tag + ">\n";
                  });

        return markup;
    }

    private void doHorizontalRules(TextEditor text) {
        String regex = "^[ ]{0,3}(?:([*][ ]*){3,}|([-][ ]*){3,}|([_][ ]*){3,})[ ]*$";

        text.replaceAll(regex, "<hr>");
    }

    private void doImages(TextEditor text) {
        // Inline image syntax
        text.replaceAll("!\\[([^\\]]*)\\]\\(([^\"]*) \"([^\"]*)\"\\)", "<img src=\"$2\" alt=\"$1\" title=\"$3\">");
        text.replaceAll("!\\[([^\\]]*)\\]\\(([^\\)]*)\\)", "<img src=\"$2\" alt=\"$1\">");

        // Reference-style image syntax
        Pattern imageLink = compile("("
                                    + "!\\[([^\\]]*)\\]"
                                    // alt text = $2
                                    // + "[ ]?(?:\\n[ ]*)?" // No whitepsace between the brackets (GFM)
                                    + "(?:\\[([^\\]]*)\\])?"
                                    // ID = $3
                                    + ")");
        text.replaceAll(imageLink, (Matcher m) -> {
                    String replacementText;
                    String wholeMatch = m.group(1);
                    String altText = m.group(2);
                    String id = m.group(3);

                    // [id] is now case sensitive
                    if (id == null || "".equals(id)) {
//                        id = altText.toLowerCase();
                        id = altText;
                    }
//                    } else {
//                        id = id.toLowerCase();
//                    }

                    // imageDefinition is the same as linkDefinition
                    LinkDefinition defn = linkDefinitions.get(id);

                    if (defn != null) {
                        String url = defn.getUrl();
                        url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                        url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                        String title = defn.getTitle();
                        StringBuilder titleTag = new StringBuilder(" alt=\"").append(altText).append("\"");

                        if (title != null && !title.equals("")) {
                            title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                            title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                            titleTag.append(" title=\"").append(title).append("\"");
                        }

                        replacementText = "<img src=\"" + url + "\"" + titleTag + ">";
                    } else {
                        replacementText = wholeMatch;
                    }

                    return replacementText;
                });
    }

    // **Strong**, *Emphasise*, __Bold__, _Italics_
    private TextEditor doStrongEmAndBoldItalics(TextEditor markup) {
//        markup.replaceAll("(\\*\\*|__)(?=\\S)(.+?[*_]*)(?<=\\S)\\1", "<strong>$2</strong>");
//        markup.replaceAll("(\\*|_)(?=\\S)(.+?)(?<=\\S)\\1", "<em>$2</em>");
        markup.replaceAll("(\\*\\*)(?=\\S)(.+?[*]?)(?<=\\S)\\1", "<strong>$2</strong>");
        markup.replaceAll("(\\*)(?=\\S)(.+?)(?<=\\S)\\1", "<em>$2</em>");
        markup.replaceAll("(__)(?=\\S)(.+?[_]?)(?<=\\S)\\1", "<b>$2</b>");
        markup.replaceAll("(_)(?=\\S)(.+?)(?<=\\S)\\1", "<i>$2</i>");

        return markup;
    }

    private TextEditor doLists(TextEditor text) {
        int lessThanTab = tabWidth - 1;

        String wholeList
               = "(?<list>(?:[ ]{0," + lessThanTab + "}"
                 + "(?<listType>[-+*]|\\d+[.])"
                 // $1 is first list item marker
                 + "[ ]+"
                 + ")"
                 + "(?s:.+?)"
                 + "(?:"
                 + "\\z"
                 // End of input is OK
                 + "|\\n{2,}"
                 // If not end of input, then a new para
                 + "(?=\\S)"
                 // negative lookahead for another list marker
                 + "(?![ ]*(?:[-+*]|\\d+[.])[ ]+)"
                 + "))";

        if (listLevel > 0) {
            Replacement replacer = (Matcher m) -> {
                String list = m.group("list");
                String listType = (m.group("listType").matches("[*+-]") ? "ul" : "ol");
//
                // Turn double returns into triple returns, so that we can make a
                // paragraph for the last item in a list, if necessary:
                list = replaceAll(list, "\\n{2,}", "\n\n\n");
                String result = processListItems(list);

                // Trim any trailing whitespace, to put the closing `</ol>` or `</ul>`
                // up on the preceding line, to get it past the current stupid
                // HTML block parser. This is a hack to work around the terrible
                // hack that is the HTML block parser.
                result = result.replaceAll("\\s+$", "");
                String html = "<" + listType + ">\n" + result + "</" + listType + ">\n";

                return html;
            };

            Pattern matchStartOfLine = compile("^" + wholeList, MULTILINE);
            text.replaceAll(matchStartOfLine, replacer);
        } else {
            Replacement replacer = (Matcher m) -> {
                String id = m.group("id");
                String list = m.group("list");
                String listType = (m.group("listType").matches("[*+-]") ? "ul" : "ol");

                // Turn double returns into triple returns, so that we can make a
                // paragraph for the last item in a list, if necessary:
                list = replaceAll(list, "\n{2,}", "\n\n\n");

                String result = processListItems(list);

                if (id == null) {
                    id = "";
                } else {
                    id = addId(id);
                }

                String html = "<" + listType + id + ">\n" + result + "</" + listType + ">\n";

                return html;
            };

            Pattern matchStartOfLine = compile("(?:(?<=\\n\\n)|\\A\\n?)(?:" + ID_REGEX + "[ ]*\\n)?" + wholeList, MULTILINE);
            text.replaceAll(matchStartOfLine, replacer);
        }

        return text;
    }

    private TextEditor doTables(TextEditor text) {
        //
        // This would be the table's caption.
        // | Col1 Header | Col2 Header |Col3 Header|Col 4|[]
        // | :---- | -:- | ---:|  :---: |[#Id][]
        // | Left | Center |Right|Justified|[]
        // | Row 2 | More text || |[]
        //
        // Table begins and ends with a blank line.
        // If present, the first row above the table becomes to table's "caption".
        // Each line begins and ends with a pipe character '|'.
        //
        // Each line can have an optional "[]" bracketed parameter.
        // This when empty "[]" adds a border around that row.
        // If contains text, the text is inserted into a 'class=' attribute
        // for that row.
        //
        // Special cases:
        // - The delimitor row sets the parameters for the '<table>' tag.
        // - Data rows:
        //   - If just the first one is set, then this will be used for all
        //     data rows.
        //   - Subseqient rows can be set to either the same class(es) as the
        //     first row or to different class(es).
        //   - Any follwing rows not set, will be configured using the row
        //     seqencing of the first set of rows, in rotation.
        //
        // "caption" is the Title for the table
        // "header" is the first row of the table
        // "delrow" is the delimitor row
        // "datarows" contains the body of the table
        // "tail" contains staggler rows just following the table
        // The table should be followed by a blank line
        //
//        Pattern p = compile("(?<header>^\\|[^\\|]+?\\|.*)\\n"    \\[\\w+[[^\\]]*?\\]|
//        Pattern p = compile("(?<header>^\\|(?:[^\\|]+?\\|)+?)(?:\\[[^\\]]*?\\])?[ ]*\\n"
        Pattern p = compile("(?<=^\\n+)"
                            //                            + "(?<caption>^[ ]*(?:\\w+.*?)[ ]*\\n)?"
                            + "(?<caption>^(?:[ ]*\\[[^\\]]*?\\][ ]*|.*?)\\n)?"
                            + "(?<header>^\\|(?:.+?\\|)+?(?:\\[#[^\\]]*?\\])?(?:\\[[^\\]]*?\\])?)[ ]*\\n"
                            //                                    + "(?<delrow>\\|(?:[ ]*([:]?[-]+?[:]?|[-]+?[:][-]+?)[ ]*\\|)+?(?:[ ]*[:]?[-]+?[:]?[ ]*)?)\\n"
                            + "(?<delrow>\\|(?:[ ]*([:-]{1}[-]+?[:-]{1}|[-]+?[:][-]+?)[ ]*\\|)+?(?:\\[#[^\\]]*?\\])?(?:\\[[^\\]]*?\\])?)[ ]*\\n"
                            //                                    + "(?<datarows>(?:\\|(?:[^\\|\\n]*\\|)+.*\\n)*?)?"
                            + "(?<datarows>(?:\\|(?:[^\\|\\n]*\\|)+(?:\\[#[^\\]]*?\\])?(?:\\[[^\\]]*?\\])?[ ]*\\n)*?)?"
                            //                                    + "(?<datarows>^(\\|?(?:[^\\|\\n]+\\|)+\\n))?"
                            //                                    + "(?<tail>(?:[^\\|\\n]+?\\n)*?)?"
                            // Find end of table
                            + "(?="
                            // Blank line
                            + "^\\n"
                            //                                    // Block quotes
                            //                                    + "|^> "
                            //                                    // <hr>
                            //                                    + "|^[ ]{0,3}(?:([*][ ]*){3,}|([-][ ]*){3,}|([_][ ]*){3,})[ ]*$"
                            //                                    // Indented Code Blocks
                            //                                    + "|^[ ]{4,}.*"
                            //                                    // Fenced Code Blocks
                            //                                    + "|^(?:[~]{3}|[`]{3})(?:[ ]*\\n"
                            //                                    + "| (?:\\[[^\\]]*\\][ ]?\\[[^\\]]*\\])?[ ]*\\n"
                            //                                    + "| (?:" + LANG_IDENTIFIER + ".+)?\\n)?"
                            // End of data
                            + "|\\Z)"
                            + "", MULTILINE);

        Replacement makeTable = new TableReplacement();

        // Escaped pipes need to be handled
        text = text.replaceAll("\\x5C\\x7C", CHAR_PROTECTOR.encode("|"));

        text.replaceAll(p, makeTable);

        return text;
    }

    private TextEditor encodeAmpsAndAngles(TextEditor markup) {
        // Ampersand-encoding based entirely on Nat Irons's Amputator MT plugin:
        // http://bumppo.net/projects/amputator/
        // Added CHAR_PROTECTOR to overcome replacement of the '&' in '&amp;'
        // by 'encodeCode()'
        // Brad Willcott (26/2/2020)
        markup.replaceAll("&(?!#?[xX]?(?:[0-9a-fA-F]+|\\w+);)", "&amp;");
        markup.replaceAll("<(?![a-zA-Z/?\\$!])", "&lt;");

        return markup;
    }

    private TextEditor encodeBackslashEscapes(TextEditor text) {
        char[] normalChars = "`_>!".toCharArray();
        char[] escapedChars = "*{}[]()#+-.".toCharArray();

        // Two backslashes in a row
        text.replaceAllLiteral("\\\\\\\\", CHAR_PROTECTOR.encode("\\"));

        // Normal characters don't require a backslash in the regular expression
        encodeEscapes(text, normalChars, "\\\\");
        encodeEscapes(text, escapedChars, "\\\\\\");

        return text;
    }

    private TextEditor encodeCode(TextEditor ed) {
        ed.replaceAll("\\\\&", CHAR_PROTECTOR.encode("&"));
        ed.replaceAll("&", "&amp;");
        ed.replaceAll("<", "&lt;");
        ed.replaceAll(">", "&gt;");
        ed.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
        ed.replaceAll("_", CHAR_PROTECTOR.encode("_"));
        ed.replaceAll("\\{", CHAR_PROTECTOR.encode("{"));
        ed.replaceAll("\\}", CHAR_PROTECTOR.encode("}"));
        ed.replaceAll("\\x5C\\x5C\\x5B", CHAR_PROTECTOR.encode("["));
        ed.replaceAll("\\[", CHAR_PROTECTOR.encode("["));
        ed.replaceAll("\\]", CHAR_PROTECTOR.encode("]"));
        ed.replaceAll("\\x5C\\x7C", CHAR_PROTECTOR.encode("|"));
        ed.replaceAll("\\\\", CHAR_PROTECTOR.encode("\\"));
        return ed;
    }

    private String encodeEmail(String s) {
        StringBuilder sb = new StringBuilder();
        char[] email = s.toCharArray();

        for (char ch : email) {
            double r = rnd.nextDouble();

            if (r < 0.45) {      // Decimal
                sb.append("&#");
                sb.append((int) ch);
                sb.append(';');
            } else if (r < 0.9) {  // Hex
                sb.append("&#x");
                sb.append(Integer.toString((int) ch, 16));
                sb.append(';');
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    private TextEditor encodeEscapes(TextEditor text, char[] chars, String slashes) {
        for (char ch : chars) {
            String regex = slashes + ch;
            text.replaceAllLiteral(regex, CHAR_PROTECTOR.encode(String.valueOf(ch)));
        }

        return text;
    }

    /**
     * escape special characters
     * <p>
     * Within tags -- meaning between < and > -- encode [\ ` * _] so they don't
     * conflict with their use in Markdown for code, italics and strong. We're
     * replacing each such character with its corresponding random string value;
     * this is likely overkill, but it should prevent us from colliding with the
     * escape values by accident.
     *
     * @param text
     *
     * @return
     */
    private TextEditor escapeSpecialCharsWithinTagAttributes(TextEditor text) {
        Collection<HTMLToken> tokens = text.tokenizeHTML();
        TextEditor newText = new TextEditor("");

        tokens.stream().map((token) -> {
            String value = token.getText();

            if (token.isTag()) {
                value = value.replaceAll("\\\\", CHAR_PROTECTOR.encode("\\"));
                value = value.replaceAll("`", CHAR_PROTECTOR.encode("`"));
                value = value.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                value = value.replaceAll("_", CHAR_PROTECTOR.encode("_"));
            }

            return value;
        }).forEachOrdered((value) -> {
            newText.append(value);
        });

        return newText;
    }

    private TextEditor formParagraphs(TextEditor markup) {
        markup.deleteAll("\\A\\n+");
        markup.deleteAll("\\n+\\z");
        markup.replaceAll("(?:\\A|\\n)" + CODE_BLOCK_BEGIN + "(\\w+?)" + CODE_BLOCK_END + "(?:\\n|\\Z)", "\n\n$1\n\n");

        String[] paragraphs;

        if (markup.isEmpty()) {
            paragraphs = new String[0];
        } else {

            paragraphs = compile("\\n{2,}").split(markup.toString());
        }

        for (int i = 0; i < paragraphs.length; i++) {
            String paragraph = paragraphs[i];
            String decoded = HTML_PROTECTOR.decode(paragraph);
            if (decoded != null) {
                paragraphs[i] = decoded;
            } else {
                paragraph = runSpanGamut(new TextEditor(paragraph)).toString();
                paragraphs[i] = "<p>" + paragraph + "</p>";
            }
        }

        return new TextEditor(String.join("\n\n", paragraphs));
    }

    private boolean hasParagraphBreak(TextEditor item) {
        return item.toString().contains("\n\n");
    }

    private void hashHTMLBlocks(TextEditor text) {
        // Hashify HTML blocks:
        // We only want to do this for block-level HTML tags, such as headers,
        // lists, and tables. That's because we still want to wrap <p>s around
        // "paragraphs" that are wrapped in non-block-level tags, such as anchors,
        // phrase emphasis, and spans. The list of tags we're looking for is
        // hard-coded:

        String[] tagsA = {
            "head",
            "p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "pre", "table",
            "dl", "ol", "ul", "script", "noscript", "form", "fieldset", "iframe", "math"
        };

        String[] tagsB = {"ins", "del"};
        String alternationA = String.join("|", tagsA);
        String alternationB = alternationA + "|" + String.join("|", tagsB);

        int less_than_tab = tabWidth - 1;

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
        Pattern p1 = compile("(?:^<(" + alternationB + ")\\b[^>]*>\n"
                             + "(?:.*\\n)*?"
                             + "^</\\1>[ ]*"
                             + "(?=\\n+|\\Z))", MULTILINE | CASE_INSENSITIVE);

        Replacement protectHTML = (Matcher m) -> {
            String literal = m.group();
            return "\n\n" + HTML_PROTECTOR.encode(literal) + "\n\n";
        };

        text.replaceAll(p1, protectHTML);

        // Now match more liberally, simply from `\n<tag>` to `</tag>\n`
//        Pattern p2 = compile("(?:^<(" + alternationA + ")\\b[^>]*>"
//                                     + "(?:.*\\n)*?"
//                                     + ".*</\\1>[ ]*"
//                                     + "(?=\\n+|\\Z))", MULTILINE | CASE_INSENSITIVE);
        Pattern p2 = compile("(?:^<(" + alternationA + ")\\b[^>]*>"
                             + "(((?!(<\\1|</\\1)).)*\\n)*?"
                             + ".*</\\1\\b[^>]*>[ ]*"
                             + "(?=\\n+|\\Z))", MULTILINE | CASE_INSENSITIVE);
        do {
            text.replaceAll(p2, protectHTML);
        } while (text.wasFound());

//        text.replaceAll(p2, protectHTML);
        // Special case for <hr>
        Pattern p3 = compile("(?:"
                             + "(?<=\\n\\n)"
                             + "|"
                             + "\\A\\n?"
                             + ")"
                             + "("
                             + "[ ]{0," + less_than_tab + "}"
                             + "<(hr)"
                             + "\\b"
                             + "([^<>])*?"
                             + "/?>"
                             + "[ ]*"
                             + "(?=\\n{2,}|\\Z))", CASE_INSENSITIVE);
        text.replaceAll(p3, protectHTML);

        // Special case for standalone HTML comments:
        Pattern p4 = compile("(?:"
                             + "(?<=\\n\\n)"
                             + "|"
                             + "\\A\\n?"
                             + ")"
                             + "("
                             + "[ ]{0," + less_than_tab + "}"
                             + "(?s:"
                             + "<!"
                             + "(--.*?--\\s*)+"
                             + ">"
                             + ")"
                             + "[ ]*"
                             + "(?=\\n{2,}|\\Z)"
                             + ")");
        text.replaceAll(p4, protectHTML);
    }

    private boolean isEmptyString(String leadingLine) {
        return leadingLine == null || leadingLine.isEmpty();
    }

    private String processGroupText(String text) {
        if (text != null && !text.isBlank()) {
            // Escaped pipes need to be handled
            return runSpanGamut(new TextEditor(text)).toString(); //.replaceAll("\\x5C\\x7C", "&#124;");
        } else {
            return "";
        }
    }

    private String processListItems(String list) {
        // The listLevel variable keeps track of when we're inside a list.
        // Each time we enter a list, we increment it; when we leave a list,
        // we decrement. If it's zero, we're not in a list anymore.
        //
        // We do this because when we're not inside a list, we want to treat
        // something like this:
        //
        //       I recommend upgrading to version
        //       8. Oops, now this line is treated
        //       as a sub-list.
        //
        // As a single paragraph, despite the fact that the second line starts
        // with a digit-period-space sequence.
        //
        // Whereas when we're inside a list (or sub-list), that line will be
        // treated as the start of a sub-list. What a kludge, huh? This is
        // an aspect of Markdown's syntax that's hard to parse perfectly
        // without resorting to mind-reading. Perhaps the solution is to
        // change the syntax rules such that sub-lists must start with a
        // starting cardinal number; e.g. "1." or "a.".
        listLevel++;

        // Trim trailing blank lines:
        list = replaceAll(list, "\\n{2,}\\z", "\n");

        Pattern p = compile("(\\n)?"
                            + "^([ ]*)([-+*]|\\d+[.])[ ]+"
                            + "((?s:.+?)(\\n{1,2}))"
                            + "(?=\\n*(\\z|\\2([-+*]|\\d+[.])[ ]+))",
                            MULTILINE);
        list = replaceAll(list, p, (Matcher m) -> {
                      String text = m.group(4);
                      TextEditor item = new TextEditor(text);
                      String leadingLine = m.group(1);

                      if (!isEmptyString(leadingLine) || hasParagraphBreak(item)) {
                          item = runBlockGamut(item.outdent());
//                          item = runBlockGamut(item);
                      } else {
                          doExtendedListOptions(item);
                          // Recurse sub-lists
                          item = doLists(item.outdent());
                          item = runSpanGamut(item);
                      }

                      return "<li>" + item.trim().toString() + "</li>\n";
                  });

        listLevel--;
        return list;
    }

    private TextEditor doExtendedListOptions(TextEditor item) {
        processCheckBoxes(item);
        return null;
    }

    private void processCheckBoxes(TextEditor item) {
        String regex = "^\\[(?<checked>[ xX])\\](?<disabled>[!])?(?<text>[ ]+[^ ]+.*)\\n";
        Pattern p = compile(regex);

        Replacement processCheckBox = (Matcher m) -> {
            StringBuilder sb = new StringBuilder("<input type=\"checkbox\"");
            String checked = m.group("checked");
            String disabled = m.group("disabled");
            String text = m.group("text");

            if (!checked.isBlank()) {
                sb.append(" checked");
            }

            if (disabled != null && !disabled.isBlank()) {
                sb.append(" disabled");
            }

            sb.append(">").append(text).append("\n");

            return sb.toString();
        };

        item.replaceAll(p, processCheckBox);
    }

    private String replaceAll(String text, String regex, String replacement) {
        TextEditor ed = new TextEditor(text);
        ed.replaceAll(regex, replacement);

        return ed.toString();
    }

    private String replaceAll(String text, Pattern pattern, Replacement replacement) {
        TextEditor ed = new TextEditor(text);
        ed.replaceAll(pattern, replacement);

        return ed.toString();
    }

    private TextEditor runBlockGamut(TextEditor text) {
        doFencedCodeBlocks(text);
        doHeaders(text);
        doTables(text);
        doHorizontalRules(text);
        doLists(text);
        doCodeBlocks(text);
        doBlockQuotes(text);

        // We already ran hashHTMLBlocks() before, in markdown(), but that
        // was to escape raw HTML in the original Markdown source. This time,
        // we're escaping the markup we've just created, so that we don't wrap
        // <p> tags around block-level tags.
        hashHTMLBlocks(text);

        return formParagraphs(text);
    }

    private TextEditor runSpanGamut(TextEditor text) {
        text = escapeSpecialCharsWithinTagAttributes(text);
        text = doCodeSpans(text);
        text = encodeBackslashEscapes(text);

        doImages(text);
        doAnchors(text);
        doAutoLinks(text);

        // Fix for BUG #1357582
        // We must call escapeSpecialCharsWithinTagAttributes() a second time to
        // escape the contents of any attributes generated by the prior methods.
        // - Nathan Winant, nw@exegetic.net, 8/29/2006
        text = escapeSpecialCharsWithinTagAttributes(text);

        encodeAmpsAndAngles(text);
        doStrongEmAndBoldItalics(text);

        // Manual line breaks
        text.replaceAll(" {2,}\n", " <br>\n");
        return text;
    }

    // [id] is now case sensitive
    private void stripLinkDefinitions(TextEditor text) {
        Pattern p = compile("^[ ]{0,3}\\[(?<id>.+)\\]:"
                            // ID = $1
                            + "[ ]*\\n?[ ]*"
                            // Space
                            //                            + "<?(?<url>\\S+?)>?"
                            + "<?(?<url>.+?)>?"
                            // URL = $2
                            + "[ ]*\\n?[ ]*"
                            // Space
                            + "(?:(?<quote>['\"(])(?<title>.+?)\\k<quote>[ ]*)?"
                            // Optional title = $3
                            + "(?:\\n+|\\Z)",
                            MULTILINE);

        text.replaceAll(p, (Matcher m) -> {
//                    String id = m.group(1).toLowerCase();
                    String id = m.group("id");
                    String url = encodeAmpsAndAngles(new TextEditor(m.group("url"))).toString();
                    String title = m.group("title");

                    if (title == null) {
                        title = "";
                    }

                    title = replaceAll(title, "\"", "&quot;");
                    linkDefinitions.put(id, new LinkDefinition(url, title));
                    return "";
                });
    }

    private void unEscapeSpecialChars(TextEditor ed) {
        CHAR_PROTECTOR.getAllEncodedTokens().forEach((hash) -> {
            String plaintext = CHAR_PROTECTOR.decode(hash);
            ed.replaceAllLiteral(hash, plaintext);
        });

    }

    public static void main(String[] args) {

        final String CtlX = Character.toString(24);

        StringBuilder buf = new StringBuilder();
        BufferedReader in = null;
        Reader reader = null;

        if (args.length > 0) {
            try {
                reader = new FileReader(args[0]);
            } catch (FileNotFoundException ex) {
                System.err.println("Error opening file: " + args[0] + "\n" + ex.getMessage());
                System.exit(1);
            }
        } else {
            reader = new InputStreamReader(System.in);
        }

        in = new BufferedReader(reader);
        String input = "";

        try {
            while (input != null) {
                while ((input = in.readLine()) != null && !input.equals(CtlX)) {
                    buf.append(input).append("\n");
                }

                if (buf.length() == 0) {
                    input = null;
                } else {
//                    System.out.println(new MarkdownProcessor().markdown(buf.toString()));
                    String regex = "^ *-=: .*\\n";
                    String regex1 = "^" + CODE_BLOCK_BEGIN + "(\\w+?)" + CODE_BLOCK_END + ".*\\n";
                    System.out.println("regex1: \"" + regex1 + "\"");

                    Pattern p = Pattern.compile(regex1, MULTILINE);
                    Matcher m = p.matcher(buf.toString());

                    if (m.find()) {
                        System.out.println("Found: |" + m.group() + "|");
                        System.out.println("$1: |" + m.group(1) + "|");
                    } else {
                        System.err.println("Not Found");
                    }

                    buf.setLength(0);
                }
            }
        } catch (java.io.IOException ex) {
            System.err.println("Error reading input: " + ex.getMessage());
            System.exit(1);
        }
    }

    private class CodeBlockReplacement implements Replacement {

        private final boolean fencedCode;
        private Matcher m;

        public CodeBlockReplacement(boolean fencedCode) {
            this.fencedCode = fencedCode;
        }

        @Override
        public String replacement(Matcher m) {
            this.m = m;
            TextEditor ed = new TextEditor(m.group("body"));

            if (!fencedCode) {
                ed.outdent();
            }

            unHashBlocks(ed);
            encodeCode(ed);
            ed.detabify().deleteAll("\\A\\n+").deleteAll("\\s+\\z");

            String text = ed.toString();
            String out = "";

            if (m.group("class") != null) {
                out = languageBlock(m.group("class"), text);
            } else if (m.group("classes") != null) {
                out = classesBlock(m.group("classes"), text);
            } else {
                out = genericCodeBlock(text);
            }

            String rtn = "\n" + CODE_BLOCK_BEGIN + HTML_PROTECTOR.encode(out) + CODE_BLOCK_END + "\n";
//            System.out.println("fencedCode: " + fencedCode + "\n");
//            System.out.println("Encoded: " + rtn);
            return rtn;
//            return out;
        }

        private String classesBlock(String classes, String text) {
            Pattern p = compile("\\[(?<preClasses>[^\\]]*)?\\][ ]?\\[(?<codeClasses>[^\\]]*)?\\]");
            Matcher m2 = p.matcher(classes);

            if (m2.find()) {
                String pre = "<pre" + addId(m.group("id")) + addClass(m2.group("preClasses")) + ">\n";
                String code = "    <code" + addClass(m2.group("codeClasses")) + ">\n";

                return pre + code + text + "\n    </code>\n</pre>";
            } else {
                return genericCodeBlock(text);
            }
        }

        private String genericCodeBlock(String text) {
            // dont'use %n in format string (markdown aspect every new line char as "\n")
//            String codeBlockTemplate = "<pre>\n    <code>\n%s\n    </code>\n</pre>";
//
//            return String.format(codeBlockTemplate, text);
            return "<pre" + addId(m.group("id")) + ">\n    <code>\n" + text + "\n    </code>\n</pre>";
        }

        private String languageBlock(String clazz, String text) {
//            String codeBlockTemplate = "<pre class=\"%s\">\n    <code>\n%s\n    </code>\n</pre>"; // http://shjs.sourceforge.net/doc/documentation.html
            String lang = clazz.replaceFirst(LANG_IDENTIFIER, "").trim();
//            String block = text.replaceFirst(firstLine + "\n", "");

//            return String.format(codeBlockTemplate, text);
            return "<pre" + addId(m.group("id")) + addClass(lang) + ">\n    <code>\n" + text + "\n    </code>\n</pre>";
        }

        private void unHashBlocks(TextEditor ed) {
            Matcher m = Pattern.compile(CharacterProtector.REGEX, MULTILINE).matcher(ed.toString());

            while (m.find()) {
                String encoded = m.group("encoded");
                String decoded = HTML_PROTECTOR.decode(encoded);

                if (decoded != null) {
                    ed.replaceAllLiteral(encoded, decoded);
                }
            }
        }
    }

    protected enum Fenced {
        YES, NO, UNFENCED
    }

    private class TableReplacement implements Replacement {

        private final String CAPTION_BORDER = "border-left: %1$dpx solid black;border-top: %1$dpx solid black;border-right: %1$dpx solid black;padding: %2$dpx";
        private final String[] INDENT = {"", "  ", "    ", "      "};
        private final String ROW_BORDER = "border: %1$dpx solid black;padding: %2$dpx;";
        private final String TABLE_BORDER = "border: %1$dpx solid black;border-collapse: collapse;padding: %2$dpx";

        public TableReplacement() {
        }

        @Override
        public String replacement(Matcher m) {

//                Pattern attPattern = compile("^.*?\\|(?<attrib>\\[(?<classes>[^\\]]*?)?\\])[ ]*$", MULTILINE);
            String rtn = m.group();

            String caption = m.group("caption");
            String header = processGroupText(m.group("header").trim());
            String delrow = m.group("delrow").trim();
            String data = processGroupText(m.group("datarows").trim());
//            String tail = processGroupText(m.group("tail").trim());

            // Process <thead>
            TableRow hRow = TableRow.parse(header);
            hRow.setReadOnly();
            TableRow delRow = TableRow.parse(delrow);

            int align = 0;
            String tmp = "";

            if (hRow.length() == delRow.length()) {
                StringBuilder sb = new StringBuilder();

                sb.append("<table");

                if (delRow.hasAttrib()) {
                    if (delRow.hasId()) {
                        sb.append(addId(delRow.getId()));
                    }

                    if (delRow.hasBorder()) {
                        tmp = String.format(TABLE_BORDER, delRow.getBorderWidth(), delRow.getCellPadding());
                        sb.append(addStyle(tmp));
                    } else {
                        sb.append(addClass(delRow.getClasses()));
                    }
                }

                sb.append(">\n");

                if (caption != null && !caption.isEmpty()) {
                    boolean captionBorders = false;
                    sb.append(INDENT[1]).append("<caption");
                    caption = processGroupText(caption.trim());

                    if (caption.startsWith("[") && caption.endsWith("]")) {
                        captionBorders = true;
                        caption = caption.substring(1, caption.length() - 1).trim();
                    }

                    if (captionBorders && delRow.hasAttrib()) {
                        if (delRow.hasBorder()) {
                            tmp = String.format(CAPTION_BORDER, delRow.getBorderWidth(), delRow.getCellPadding());
                            sb.append(addStyle(tmp));
                        }
                    }

                    sb.append(">\n").append(caption).append("\n")
                            .append(INDENT[1]).append("</caption>\n");
                }

                // Process column formatting
//                    boolean colGroupRequired = false;
//                    StringBuilder cols = new StringBuilder();
                for (int i = 0; i < delRow.length(); i++) {
                    String delCol = delRow.getCell(i).trim();
                    align = (delCol.startsWith(":") ? 1 : 0);
                    align = align + (delCol.endsWith(":") ? 2 : 0);

                    switch (align) {
                        case 0:
                            if (delCol.matches("[-]+?[:][-]+?")) {
                                delRow.setCell(i, "text-align: center");
                            } else {
                                delRow.setCell(i, "");
                            }
                            break;

                        case 1:
                            delRow.setCell(i, "text-align: left");
                            break;

                        case 2:
                            delRow.setCell(i, "text-align: right");
                            break;

                        case 3:
                            delRow.setCell(i, "text-align: justify");
                            break;
                    }

//                        if (!delRow.getCell(i).isEmpty()) {
//                            cols.append(INDENT[2]).append("<col");
//                            cols.append(String.format(STYLE, delRow.getCell(i)));
//                            cols.append(">\n");
//                            colGroupRequired = true;
//                        }
                }

                delRow.setReadOnly();

//                    if (cols.length() > 0) {
//                        sb.append(INDENT[1]).append("<colgroup>\n")
//                                .append(cols)
//                                .append(INDENT[1]).append("</colgroup>\n");
//                    }
                sb.append(INDENT[1]).append("<thead>\n")
                        .append(INDENT[2]).append("<tr");

                if (hRow.hasId()) {
                    sb.append(addId(hRow.getId()));
                }

                sb.append(">\n");

                // Process <th> attributes
                for (int i = 0; i < delRow.length(); i++) {

                    sb.append(INDENT[3]).append("<th");

                    if (!hRow.hasAttrib()) {
                        if (!delRow.getCell(i).isEmpty()) {
                            sb.append(addStyle(delRow.getCell(i)));
                        }
                    } else {
                        if (hRow.hasBorder()) {
                            tmp = String.format(ROW_BORDER, hRow.getBorderWidth(), hRow.getCellPadding());
                            sb.append(addStyle(tmp + delRow.getCell(i)));
//                                sb.append(String.format(STYLE, tmp));
                        } else {
                            if (hRow.hasClasses()) {
                                sb.append(addClass(hRow.getClasses()));
                            }

                            if (!delRow.getCell(i).isEmpty()) {
                                sb.append(addStyle(delRow.getCell(i)));
                            }
                        }
                    }

                    sb.append(">\n").append(hRow.getCell(i).trim()).append("\n")
                            .append(INDENT[3]).append("</th>\n");
                }

                sb.append(INDENT[2]).append("</tr>\n")
                        .append(INDENT[1]).append("</thead>\n");

                if (!data.trim().isEmpty()) {
                    String[] dataRows = data.split("\n");
                    TableRowList rowList = new TableRowList(dataRows.length);

                    sb.append(INDENT[1]).append("<tbody>\n");

                    for (int i = 0; i < dataRows.length; i++) {
                        TableRow dataRow = TableRow.parse(dataRows[i]);
                        rowList.add(dataRow);

                        sb.append(INDENT[2]).append("<tr");

                        if (dataRow.hasId()) {
                            sb.append(addId(dataRow.getId()));
                        }

                        sb.append(">\n");

                        for (int j = 0; j < dataRow.length() && j < delRow.length(); j++) {
                            // Process <td> attributes
                            sb.append(INDENT[3]).append("<td");

                            if (!dataRow.hasAttrib()) {
                                if (rowList.hasNext()) {
                                    TableRow attrib = rowList.getNext();

                                    if (attrib.hasBorder()) {
                                        tmp = String.format(ROW_BORDER, attrib.getBorderWidth(), attrib.getCellPadding());
                                        sb.append(addStyle(tmp + delRow.getCell(j)));
//                                            sb.append(String.format(STYLE, tmp));
                                    } else {
//                                        sb.append(addClass(attrib.getClasses()));
                                        if (attrib.hasClasses()) {
                                            sb.append(addClass(attrib.getClasses()));
                                        }

                                        if (!delRow.getCell(j).isEmpty()) {
                                            sb.append(addStyle(delRow.getCell(j)));
                                        }
                                    }
                                } else if (!delRow.getCell(j).isEmpty()) {
                                    sb.append(addStyle(delRow.getCell(j)));
                                }
                            } else {
                                if (dataRow.hasBorder()) {
                                    tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(), dataRow.getCellPadding());
                                    sb.append(addStyle(tmp + delRow.getCell(j)));
//                                        sb.append(String.format(STYLE, tmp));
                                } else {
//                                    sb.append(addClass(dataRow.getClasses()));
                                    if (dataRow.hasClasses()) {
                                        sb.append(addClass(dataRow.getClasses()));
                                    }

                                    if (!delRow.getCell(j).isEmpty()) {
                                        sb.append(addStyle(delRow.getCell(j)));
                                    }
                                }
                            }

                            sb.append(">\n").append(dataRow.getCell(j).trim()).append("\n")
                                    .append(INDENT[3]).append("</td>\n");
                        }

                        if (dataRow.length() < hRow.length()) {
                            sb.append(INDENT[3]).append("<td");

                            for (int k = dataRow.length(); k < hRow.length(); k++) {
                                if (!dataRow.hasAttrib()) {
                                    if (rowList.hasNext()) {
                                        TableRow attrib = rowList.getNext();

                                        if (attrib.hasBorder()) {
                                            tmp = String.format(ROW_BORDER, attrib.getBorderWidth(), attrib.getCellPadding());
                                            sb.append(addStyle(tmp + delRow.getCell(k)));
//                                                sb.append(String.format(STYLE, tmp));
                                        } else {
//                                            sb.append(addClass(attrib.getClasses()));
                                            if (attrib.hasClasses()) {
                                                sb.append(addClass(attrib.getClasses()));
                                            }

                                            if (!delRow.getCell(k).isEmpty()) {
                                                sb.append(addStyle(delRow.getCell(k)));
                                            }
                                        }
                                    } else if (!delRow.getCell(k).isEmpty()) {
                                        sb.append(addStyle(delRow.getCell(k)));
                                    }
                                } else {
                                    if (dataRow.hasBorder()) {
                                        tmp = String.format(ROW_BORDER, dataRow.getBorderWidth(), dataRow.getCellPadding());
                                        sb.append(addStyle(tmp + delRow.getCell(k)));
//                                            sb.append(String.format(STYLE, tmp));
                                    } else {
//                                        sb.append(addClass(dataRow.getClasses()));
                                        if (dataRow.hasClasses()) {
                                            sb.append(addClass(dataRow.getClasses()));
                                        }

                                        if (!delRow.getCell(k).isEmpty()) {
                                            sb.append(addStyle(delRow.getCell(k)));
                                        }
                                    }
                                }

                                sb.append(">\n&nbsp;\n").append(INDENT[3]).append("</td>\n");
                            }
                        }

                        sb.append(INDENT[2]).append("</tr>\n");
                    }

//                    if (!tail.trim().isEmpty()) {
//                        dataRows = tail.split("\n");
//
//                        for (int i = 0; i < dataRows.length; i++) {
//                            sb.append("<tr>\n<td>").append(dataRows[i].trim()).append("</td>\n");
//
//                            for (int j = 1; j < hCols.length; j++) {
//                                sb.append("<td></td>\n");
//                            }
//
//                            sb.append("</tr>\n");
//                        }
//
//                    }
                    sb.append(INDENT[1]).append("</tbody>\n");

//                } else if (!tail.trim().isEmpty()) {
//
//                    String[] dataRows = tail.split("\n");
//                    sb.append("<tbody>\n");
//
//                    for (int i = 0; i < dataRows.length; i++) {
//                        sb.append("<tr>\n<td>").append(dataRows[i].trim()).append("</td>\n");
//
//                        for (int j = 1; j < hCols.length; j++) {
//                            sb.append("<td></td>\n");
//                        }
//
//                        sb.append("</tr>\n");
//                    }
//
//                    sb.append("</tbody>\n");
                }
                String out = sb.append("</table>\n").toString();

                rtn = "\n\n" + HTML_PROTECTOR.encode(out) + "\n\n";
            }

            return rtn;
        }
    }
}
