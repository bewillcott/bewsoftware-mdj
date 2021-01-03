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
 *
 */
package com.bewsoftware.mdj.core;

import com.bewsoftware.utils.struct.BooleanReturn;
import com.bewsoftware.utils.struct.IntegerReturn;
import com.bewsoftware.utils.struct.StringReturn;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;
import static com.bewsoftware.mdj.core.Attributes.addClass;
import static com.bewsoftware.mdj.core.Attributes.addId;

/**
 * Convert Markdown text into HTML, as per
 * http://daringfireball.net/projects/markdown/ . Usage:
 * <pre><code>
 *     MarkdownProcessor markdown = new MarkdownProcessor();
 *     String html = markdown.markdown("*italic*   **bold**\n_italic_   __bold__");
 * </code></pre>
 * <p>
 * <b>Changes:</b>
 * <ul>
 * <li>Set all methods to be {@code static}.</li>
 * <li>Set all method parameters to be {@code final}.</li>
 * <li>Converted all instance variables to be class variables.</li>
 * <li>MarkdownProcessor class is now a {@code static} class.</li>
 * </ul>
 * Bradley Willcott (02/01/2021)
 */
public class MarkdownProcessor {

    private static final CharacterProtector CHAR_PROTECTOR = new CharacterProtector();
    private static final String CLASS_REGEX = "(?:\\[@(?<classes>\\p{Alpha}[^\\]]+)\\])?";
    private static final String ID_REGEX = "(?:\\[#(?<id>\\w+)\\])";
    private static final String ID_REGEX_OPT = "(?:\\[#(?<id>\\w+)\\])?";
    private static final int TAB_WIDTH = 4;
    private static final String TAG_ID = "\\[#\\w+\\]";
    private static final String TARGET = " target=\"" + CHAR_PROTECTOR.encode("_") + "blank\"";
    private static final Map<String, LinkDefinition> linkDefinitions = new TreeMap<>();
    private static final IntegerReturn listLevel = new IntegerReturn(0);

    static final String CODE_BLOCK_BEGIN = "-=: ";
    static final String CODE_BLOCK_END = " :=-";
    static final CharacterProtector HTML_PROTECTOR = new CharacterProtector();
    static final String LANG_IDENTIFIER = "lang:";

    /**
     * For testing purposes ONLY.
     *
     * @param args not used.
     */
    public static void main(String[] args) {

        final String CtlX = Character.toString(24);

        StringBuilder buf = new StringBuilder();
        BufferedReader in = null;
        Reader reader = null;

        if (args.length > 0)
        {
            try
            {
                reader = new FileReader(args[0]);
            } catch (FileNotFoundException ex)
            {
                System.err.println("Error opening file: " + args[0] + "\n" + ex.getMessage());
                System.exit(1);
            }
        } else
        {
            reader = new InputStreamReader(System.in);
        }

        in = new BufferedReader(reader);
        String input = "";

        try
        {
            while (input != null)
            {
                while ((input = in.readLine()) != null && !input.equals(CtlX))
                {
                    buf.append(input).append("\n");
                }

                if (buf.length() == 0)
                {
                    input = null;
                } else
                {
                    String regex = "^ *-=: .*\\n";
                    String regex1 = "^" + CODE_BLOCK_BEGIN + "(\\w+?)" + CODE_BLOCK_END + ".*\\n";
                    System.out.println("regex1: \"" + regex1 + "\"");

                    Pattern p = Pattern.compile(regex1, MULTILINE);
                    Matcher m = p.matcher(buf.toString());

                    if (m.find())
                    {
                        System.out.println("Found: |" + m.group() + "|");
                        System.out.println("$1: |" + m.group(1) + "|");
                    } else
                    {
                        System.err.println("Not Found");
                    }

                    buf.setLength(0);
                }
            }
        } catch (java.io.IOException ex)
        {
            System.err.println("Error reading input: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Perform the conversion from Markdown to HTML.
     *
     * @param txt - input in markdown format
     *
     * @return HTML block corresponding to txt passed in.
     */
    public static String markdown(String txt) {
        if (txt == null)
        {
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

        //
        // Convert line extensions to single spaces.
        //
        // Bradley Willcott (26/12/2020)
        //
        text.replaceAll(" \\\\\\n", " ");

        // Turn block-level HTML blocks into hash entries
        hashHTMLBlocks(text);
        // Strip link definitions, store in hashes.
        stripLinkDefinitions(text);

        text = runBlockGamut(text);

        unEscapeSpecialChars(text);

        text.append("\n");
        return text.toString();
    }

    private static String deleteAll(final String text, final String regex) {
        return replaceAll(text, regex, "");
    }

    private static TextEditor doAutoLinks(final TextEditor markup) {
        // Added class attribute to anchors.
        // <url>![@class]
        //
        // Bradley Willcott (15/12/2020)
        Pattern link = Pattern.compile("<"
                                       + "(?<url>(?:https?|ftp):[^'\">\\s]+)"
                                       + ">"
                                       + "(?<target>!)?"
                                       + CLASS_REGEX); // BW

        markup.replaceAll(link, (Matcher m) ->
                  {
                      String url = m.group("url");
                      String targetTag = m.group("target") != null ? TARGET : "";
                      // BW: classString
                      String classes = m.group("classes");
                      // BW: classAtrib
                      String classAtrib = classes != null && !classes.isBlank()
                                          ? addClass(classes) : "";

                      return "<a href=\"" + url + "\""
                             // BW: classAtrib
                             + classAtrib + targetTag + ">" + url + "</a>";
                  });

        Pattern email = compile("<([-.\\w]+\\@[-a-z0-9]+(?:\\.[-a-z0-9]+)*\\.[a-z]+)>");
        markup.replaceAll(email, (Matcher m) ->
                  {
                      String address = m.group(1);
                      TextEditor ed = new TextEditor(address);
                      unEscapeSpecialChars(ed);
                      String addr = encodeEmail(ed.toString());
                      String url = encodeEmail("mailto:" + ed.toString());
                      return "<a href=\"" + url + "\">" + addr + "</a>";
                  });

        return markup;
    }

    private static TextEditor doBlockQuotes(final TextEditor markup) {
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
        return markup.replaceAll(p, (Matcher m) ->
                         {
                             TextEditor blockQuote = new TextEditor(m.group(1));
                             blockQuote.deleteAll("^[ \t]*>[ \t]?");
                             blockQuote.deleteAll("^[ \t]+$");
                             blockQuote = runBlockGamut(blockQuote);
                             blockQuote.replaceAll("^", "  ");

                             Pattern p1 = compile("(\\s*<pre>.*?</pre>)", DOTALL);
                             blockQuote = blockQuote.replaceAll(p1, (Matcher m1) ->
                                                        {
                                                            String pre = m1.group(1);
                                                            return deleteAll(pre, "^  ");
                                                        });

                             return "<blockquote>\n" + blockQuote + "\n</blockquote>\n\n";
                         });
    }

    private static TextEditor doCodeBlocks(final TextEditor markup) {
        /*
         * If there are any Fenced Code Blocks, they will have been protected
         * from 'p2' by HTML_PROTECTOR.encode(..). This prevents 'p2' from
         * removing 4 spaces from the beginning of each line of the Fenced Code
         * Blocks.
         *
         * Any tabbed or space type block will still be processed by 'p2'.
         *
         * Bradley Willcott (08/01/2020)
         */
        Pattern p2 = compile("(?:(?<id>)?(?<classes>)?(?<=\\n\\n)|\\A)"
                             + "(?:^(?:> )*?[ ]{4})?(?<class>" + LANG_IDENTIFIER + ".+)?(?:\\n)?"
                             + "(?<body>(?:"
                             + "(?:^(?:> )*?[ ]{4}).*\\n+)+)"
                             + "(?:(?=^\\n+)|\\Z)", MULTILINE);
        return markup.replaceAll(p2, new CodeBlockReplacement(false));
    }

    private static TextEditor doCodeSpans(final TextEditor markup) {
        return markup.replaceAll(compile("(?<!\\\\)(`+)(.+?)(?<!`)\\1(?!`)"), (Matcher m) ->
                         {
                             String code = m.group(2);
                             TextEditor subEditor = new TextEditor(code);
                             subEditor.deleteAll("^[ \\t]+").deleteAll("[ \\t]+$");
                             encodeCode(subEditor);
                             return "<code>" + subEditor.toString() + "</code>";
                         });
    }

    private static TextEditor doDelIns(final TextEditor text) {
        text.replaceAll("(-!)(?=\\S)(.+?)(?<=\\S)!", "<del>$2</del>");
        text.replaceAll("(\\+!)(?=\\S)(.+?)(?<=\\S)!", "<ins>$2</ins>");

        return text;
    }

    /**
     * Process additional types of lists.
     * <p>
     * Lists with line items containing any or all of the following:<br>
     * <ul>
     * <li>Checkbox.<br>
     * - [ ] This is a checkbox item.<br>
     * - [ ][@classname] This is a checkbox item with a class attribute.<br>
     * see: {@link #processCheckBoxes(com.bewsoftware.mdj.core.TextEditor,
     * com.bewsoftware.utils.struct.StringReturn)
     * processCheckBoxes()}
     * </li>
     * <li>Class attribute.<br>
     * - [@classname] This is a list item with a class attribute.<br>
     * see: {@link #processListItemsWithAClass(com.bewsoftware.mdj.core.TextEditor,
     * com.bewsoftware.utils.struct.StringReturn)
     * processListItemsWithAClass()}
     * </li>
     * </ul>
     * Bradley Willcott
     *
     * @param item        The text to process.
     * @param classString returns the value of the 'class' text, if any.
     *
     * @return {@code true} if any Checkboxes were found, {@code false} otherwise.
     *
     * @since 10/01/2020, last updated: 14/12/2020.
     */
    private static boolean doExtendedListOptions(final TextEditor item, final StringReturn classString) {

        boolean rtn = processCheckBoxes(item, classString);

        if (!rtn)
        {
            processListItemsWithAClass(item, classString);
        }

        return rtn;
    }

    /**
     * Fenced Code Blocks:<br>
     * Use either triple back-ticks: {@code ```}, or triple tildes: {@code ~~~}
     * for the beginning and end of the code block.
     * <dl>
     * <dt><b>NOTE:</b></dt>
     * <dd>You MUST use the same characters for the end of the block, as you used
     * for the beginning of the block.</dd>
     * </dl>
     * <hr>
     * <pre><code>
     * Example:
     *
     * ~~~
     *    &#64;Override
     *    public static String toString(){
     *        return "This is the text";
     *    }
     * ~~~
     *
     * &lt;pre&gt;
     *     &lt;code&gt;
     *     &#64;Override
     *     public static String toString(){
     *         return "This is the text";
     *     }
     *     &lt;/code&gt;
     * &lt;/pre&gt;
     * </code></pre>
     * <hr>
     * <dl>
     * <dt><b>New: classes</b></dt>
     * <dd>Can now add classes to both the &lt;pre&gt; and &lt;code&gt; tags:</dd>
     * </dl>
     * <hr>
     * <pre><code>
     * ```[@&lt;pre tag classes&gt;][@&lt;code tag classes&gt;]
     *
     * Example:
     *
     * ~~~[@prettyprint][@language-java]
     *
     * Result:
     *
     * &lt;pre class="prettyprint"&gt;&lt;code class="language-java"&gt;
     *</code></pre>
     * <hr>
     * <p>
     * Though both class tags are optional, you must have at least the empty
     * <i>pre</i> tag class brackets, if you want to supply the <i>code</i>
     * tag class:
     * <hr>
     * <pre><code>
     * ```[][@&lt;code tag classes&gt;]
     *
     * Example:
     *
     * ~~~[][@language-java]
     *
     * Result:
     *
     * &lt;pre&gt;&lt;code class="language-java"&gt;
     *
     * ------------------------------------------
     *
     * ```[@&lt;pre tag classes&gt;]
     *
     * Example:
     *
     * ~~~[@prettyprint]
     *
     * Result:
     *
     * &lt;pre class="prettyprint"&gt;&lt;code&gt;
     *</code></pre>
     * <hr>
     * <p>
     * Bradley Willcott
     *
     * @param markup The text to process.
     *
     * @return the parameter {@code markup} for chaining.
     *
     * @since 07/01/2020
     */
    private static TextEditor doFencedCodeBlocks(final TextEditor markup) {
        Pattern p1 = compile("(?<frontFence>^(?:[~]{3}|[`]{3}))"
                             + ID_REGEX_OPT
                             + "(?:[ ]*\\n"
                             + "|(?<classes>\\[(?:@\\p{Alpha}[^\\]]*)?\\](?:\\[(?:@\\p{Alpha}[^\\]]*)?\\])?)?[ ]*\\n"
                             + "|(?<class>" + LANG_IDENTIFIER + ".+)?\\n)?"
                             + "(?<body>(?:.*?\\n+)+?)"
                             + "(?:\\k<frontFence>)[ ]*\\n", MULTILINE);
        return markup.replaceAll(p1, new CodeBlockReplacement(true));
    }

    /**
     * Build headers: &lt;h1&gt; - &lt;h6&gt;.
     *
     * @param markup to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor doHeaders(final TextEditor markup) {
        // setext-style headers
        markup.replaceAll("^(.*)\n[=]{4,}$", "<h1>$1</h1>");
        markup.replaceAll("^(.*)\n[-]{4,}$", "<h2>$1</h2>");

        // atx-style headers - e.g., "#### heading 4 ####"
        // Added Anchor option to header text
        // Bradley Willcott (11/03/2020)
        Pattern p = compile("^(?<marker>[#]{1,6})" + ID_REGEX_OPT
                            + "[ ]*(?<heading>(?<brkt1>\\[)?[^\\]]*?(?<brkt2>(\\]|\\]\\[[^\\]]*?\\])!?)?)"
                            + "[ ]*(?:\\k<marker>[ ]*(?<tail>.*)?)?$", MULTILINE);
        markup.replaceAll(p, (Matcher m) ->
                  {
                      String marker = m.group("marker");
                      String id = m.group("id");
                      String heading = m.group("heading");
                      String brkt1 = m.group("brkt1");
                      String brkt2 = m.group("brkt2");
                      String tail = m.group("tail");

                      if (id != null)
                      {
                          id = addId(id);
                      } else
                      {
                          id = "";
                      }

                      // Process header text, looking for anchor option (BW)
                      if (brkt1 != null && brkt2 != null)
                      {
                          heading = doAnchors(new TextEditor(heading), false).toString();
                      }

                      if (tail != null && !tail.isBlank())
                      {
                          tail = " " + doAnchors(new TextEditor(tail), false).toString();
                      } else
                      {
                          tail = "";
                      }

                      int level = marker.length();
                      String tag = "h" + level;
                      return "<" + tag + id + ">" + heading + tail + "</" + tag + ">\n";
                  });

        return markup;
    }

    /**
     * Build horizontal rules.
     *
     * @param text to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor doHorizontalRules(final TextEditor text) {
        String regex = "^[ ]{0,3}(?:([*][ ]*){3,}|([-][ ]*){3,}|([_][ ]*){3,})[ ]*$";

        text.replaceAll(regex, "<hr>");

        return text;
    }

    /**
     * Process all image references.
     * <p>
     * <b>Syntax:</b>
     * <ul>
     * <li>Inline image:
     * <ul>
     * <li>{@code ![<alt>](<src>) "<title>"}</li>
     * <li>{@code ![<alt>](<src>)}</li>
     * </ul></li>
     * <li>Reference-style image:
     * <ul>
     * <li>{@code ![<alt>][<id>]}</li>
     * </ul></li>
     * </ul>
     * Comments added by: Bradley Willcott (24/12/2020)
     *
     * @param text to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor doImages(final TextEditor text) {
        // Inline image syntax
        //
        // Added "(?!@)" to prevent picking up [link]![@class](...)
        //
        // Bradley Willcott (23/12/2020)
        text.replaceAll("!\\[(?!@)([^\\]]*)\\]\\(([^\"]*) \"([^\"]*)\"\\)", "<img src=\"$2\" alt=\"$1\" title=\"$3\">");
        text.replaceAll("!\\[(?!@)([^\\]]*)\\]\\(([^\\)]*)\\)", "<img src=\"$2\" alt=\"$1\">");

        // Reference-style image syntax
        //
        // Added "(?!@)" to prevent picking up [link]![@class](...)
        //
        // Bradley Willcott (23/12/2020)
        Pattern imageLink = compile("("
                                    + "!\\[(?!@)([^\\]]*)\\]"
                                    // alt text = $2
                                    + "(?:\\[([^\\]]*)\\])?"
                                    // ID = $3
                                    + ")");
        text.replaceAll(imageLink, (Matcher m) ->
                {
                    String replacementText;
                    String wholeMatch = m.group(1);
                    String altText = m.group(2);
                    String id = m.group(3);

                    // [id] is now case sensitive
                    if (id == null || "".equals(id))
                    {
                        id = altText;
                    }

                    // imageDefinition is the same as linkDefinition
                    LinkDefinition defn = linkDefinitions.get(id);

                    if (defn != null)
                    {
                        // BW:
                        String url = defn.url;

                        if (url != null)
                        {
                            url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                            url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                        }

                        // BW:
                        String title = defn.title;
                        StringBuilder titleTag = new StringBuilder(" alt=\"").append(altText).append("\"");

                        if (title != null && !title.equals(""))
                        {
                            title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                            title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                            titleTag.append(" title=\"").append(title).append("\"");
                        }

                        replacementText = "<img src=\"" + url + "\"" + titleTag + ">";
                    } else
                    {
                        replacementText = wholeMatch;
                    }

                    return replacementText;
                });

        return text;
    }

    /**
     * Lists are created as follows:
     * <hr><pre><code>
     *
     *[#&lt;id&gt;][@&lt;classname&gt;]
     *- Item one
     *- Item two
     *[@&lt;classname&gt;]
     *    - Sub-item A
     *    - Sub-item B
     *- Item three
     *    - Sub-item A
     * </code></pre><hr>
     * Both the `[#&lt;id&gt;]` and `[@&lt;classname&gt;]` attributes are optional,
     * but if both are supplied they <i>must</i> be in the order shown above.
     *
     *
     * @param text to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor doLists(final TextEditor text) {
        int lessThanTab = TAB_WIDTH - 1;

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

        if (listLevel.val > 0)
        {
            Replacement replacer = (Matcher m) ->
            {
                //
                // Changed 'list' from String to StringReturn class for use
                // with: processListItems(list).
                //
                // Bradley Willcott (13/12/2020)
                //
                StringReturn list = new StringReturn(m.group("list"));
                String classes = m.group("classes");
                String listType = (m.group("listType").matches("[*+-]") ? "ul" : "ol");
                //
                // Turn double returns into triple returns, so that we can make a
                // paragraph for the last item in a list, if necessary:
                list.val = replaceAll(list.val, "\\n{2,}", "\n\n\n");

                // Check boxes are processed in here...
                boolean checkboxes = processListItems(list);
                String classAttrib = "";

                if (checkboxes)
                {
                    classAttrib = "checkbox";
                }

                if (classes != null)
                {
                    classAttrib = !classAttrib.isBlank() ? classAttrib + " " + classes : classes;
                }

                // Trim any trailing whitespace, to put the closing `</ol>` or `</ul>`
                // up on the preceding line, to get it past the current stupid
                // HTML block parser. This is a hack to work around the terrible
                // hack that is the HTML block parser.
                list.val = list.val.replaceAll("\\s+$", "");

                //
                // Added 'class=' attribute for when list contains checkboxes.
                //
                // Bradley Willcott (13/12/2020)
                //
                return "<" + listType + addClass(classAttrib) + ">\n" + list + "</" + listType + ">\n";
            };

            Pattern matchStartOfLine = compile("^(?:" + CLASS_REGEX + "[ ]*\\n)?" + wholeList, MULTILINE);
            text.replaceAll(matchStartOfLine, replacer);
        } else
        {
            Replacement replacer = (Matcher m) ->
            {
                //
                // Changed 'list' from String to StringReturn class for use
                // with: processListItems(list).
                //
                // Bradley Willcott (13/12/2020)
                //
                StringReturn list = new StringReturn(m.group("list"));
                String id = m.group("id");
                String classes = m.group("classes");
                String listType = (m.group("listType").matches("[*+-]") ? "ul" : "ol");

                // Turn double returns into triple returns, so that we can make a
                // paragraph for the last item in a list, if necessary:
                list.val = replaceAll(list.val, "\n{2,}", "\n\n\n");

                // Check boxes are processed in here...
                boolean checkboxes = processListItems(list);
                String classAttrib = "";

                if (checkboxes)
                {
                    classAttrib = "checkbox";
                }

                if (classes != null)
                {
                    classAttrib = !classAttrib.isBlank() ? classAttrib + " " + classes : classes;
                }

                if (id == null)
                {
                    id = "";
                } else
                {
                    id = addId(id);
                }

                //
                // Added 'class=' attribute for when list contains checkboxes.
                //
                // Bradley Willcott (13/12/2020)
                //
                return "<" + listType + id + addClass(classAttrib) + ">\n" + list + "</" + listType + ">\n";
            };

            Pattern matchStartOfLine = compile("(?:(?<=^\\n)|\\A\\n?)(?:" + ID_REGEX_OPT + CLASS_REGEX + "[ ]*\\n)?" + wholeList, MULTILINE);
            text.replaceAll(matchStartOfLine, replacer);
        }

        return text;
    }

    /**
     * Build **Strong**, *Emphasise*, __Bold__, _Italics_
     *
     * @param markup to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor doStrongEmAndBoldItalics(final TextEditor markup) {
        markup.replaceAll("(\\*\\*)(?=\\S)(.+?[*]?)(?<=\\S)\\1", "<strong>$2</strong>");
        markup.replaceAll("(\\*)(?=\\S)(.+?)(?<=\\S)\\1", "<em>$2</em>");
        markup.replaceAll("(__)(?=\\S)(.+?[_]?)(?<=\\S)\\1", "<b>$2</b>");
        markup.replaceAll("(_)(?=\\S)(.+?)(?<=\\S)\\1", "<i>$2</i>");

        return markup;
    }

    /**
     * Build subscripts and superscripts.
     *
     * @param text to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor doSubSup(final TextEditor text) {
        text.replaceAll("(?<![-:])(--)(?![-:])(?=\\S)(.+?)(?<=\\S)\\1", "<sub>$2</sub>");
        text.replaceAll("(\\+\\+)(?=\\S)(.+?)(?<=\\S)\\1", "<sup>$2</sup>");

        return text;
    }

    /**
     * Build tables.
     *
     * @param text to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor doTables(final TextEditor text) {
        //
        // This would be the table's caption.
        // | Col1 Header | Col2 Header |Col3 Header|Col 4|[]
        // | :---- | -:- | ---:|  :---: |[#Id][]
        // | Left | Center |Right|Justified|[]
        // | Row 2 | More text || |[]
        //
        // Table begins and ends with a blank line.
        // If present, the first row above the table becomes to table's "caption".
        // Each line begins and ends with a pipe character '|' with additional such
        // to separate columns.
        //
        // Each line can have an optional "[]" bracketed parameter.
        // This when empty "[]" adds a border around that row.
        // If contains text, the text is inserted into a 'class=' attribute
        // for that row.
        //
        // Special cases:
        // - The delimiter row sets the parameters for the '<table>' tag.
        // - Data rows:
        //   - If just the first one is set, then this will be used for all
        //     data rows.
        //   - Subsequent rows can be set to either the same class(es) as the
        //     first row or to different class(es).
        //   - Any follwing rows not set, will be configured using the row
        //     sequencing of the first set of rows, in rotation.
        //
        // "caption" is the Title for the table
        // "header" is the first row of the table
        // "delrow" is the delimitor row
        // "datarows" contains the body of the table
        // "tail" contains staggler rows just following the table
        // The table should be followed by a blank line
        //
        Pattern p = compile("(?<=^\\n+)"
                            + "(?<caption>^(?:[ ]*\\[\\w+[^\\]]*?\\][ ]*|[ ]*.*?\\w+.*?[ ]*)\\n)?"
                            + "(?<header>^\\|(?:.+?\\|)+?(?:\\[#[^\\]]*?\\])?(?:\\[[^\\]]*?\\])?)[ ]*\\n"
                            + "(?<delrow>\\|(?:[ ]*([:-]{1}[-]+?[:-]{1}|[-]+?[:][-]+?)[ ]*\\|)+?(?:" + TAG_ID + ")?(?:\\[[^\\]]*?\\])?)[ ]*\\n"
                            + "(?<datarows>(?:\\|(?:[^\\|\\n]*\\|)+(?:\\[#[^\\]]*?\\])?(?:\\[[^\\]]*?\\])?[ ]*\\n)*?)?"
                            // Find end of table
                            + "(?="
                            // Blank line
                            + "^\\n"
                            // End of data
                            + "|\\Z)"
                            + "", MULTILINE);

        Replacement makeTable = new TableReplacement();

        // Escaped pipes need to be handled
        text.replaceAll("\\x5C\\x7C", CHAR_PROTECTOR.encode("|"));

        text.replaceAll(p, makeTable);

        return text;
    }

    /**
     * Encode Amps and Angles.
     *
     * @param markup text to process.
     *
     * @return TextEditor for chaining.
     */
    private static TextEditor encodeAmpsAndAngles(final TextEditor markup) {
        // Ampersand-encoding based entirely on Nat Irons's Amputator MT plugin:
        // http://bumppo.net/projects/amputator/
        // Added CHAR_PROTECTOR to overcome replacement of the '&' in '&amp;'
        // by 'encodeCode()'
        // Brad Willcott (26/2/2020)
        markup.replaceAll("&(?!#?[xX]?(?:[0-9a-fA-F]+|\\w+);)", "&amp;");
        markup.replaceAll("<(?![a-zA-Z/?\\$!])", "&lt;");

        return markup;
    }

    /**
     * Encode backslash escapes.
     *
     * @param text to process.
     *
     * @return TextEditor for chaining;
     */
    private static TextEditor encodeBackslashEscapes(final TextEditor text) {
        char[] normalChars = "`_>!".toCharArray();
        char[] escapedChars = "*{}[]()#+-.".toCharArray();

        // Two backslashes in a row
        text.replaceAllLiteral("\\\\\\\\", CHAR_PROTECTOR.encode("\\"));

        // Normal characters don't require a backslash in the regular expression
        encodeEscapes(text, normalChars, "\\\\");
        encodeEscapes(text, escapedChars, "\\\\\\");

        return text;
    }

    private static String encodeEmail(final String s) {
        StringBuilder sb = new StringBuilder();
        char[] email = s.toCharArray();

        for (char ch : email)
        {
            double r = new Random().nextDouble();

            if (r < 0.45)
            {      // Decimal
                sb.append("&#");
                sb.append((int) ch);
                sb.append(';');
            } else if (r < 0.9)
            {  // Hex
                sb.append("&#x");
                sb.append(Integer.toString((int) ch, 16));
                sb.append(';');
            } else
            {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    private static TextEditor encodeEscapes(final TextEditor text,
                                            final char[] chars, final String slashes) {
        for (char ch : chars)
        {
            String regex = slashes + ch;
            text.replaceAllLiteral(regex, CHAR_PROTECTOR.encode(String.valueOf(ch)));
        }

        return text;
    }

    /**
     * escape special characters
     * <p>
     * Within tags -- meaning between &lt; and &gt; -- encode [\ ` * _] so they don't
     * conflict with their use in Markdown for code, italics and strong. We're
     * replacing each such character with its corresponding random string value;
     * this is likely overkill, but it should prevent us from colliding with the
     * escape values by accident.
     *
     * @param text to process.
     *
     * @return new TextEditor with changes.
     */
    private static TextEditor escapeSpecialCharsWithinTagAttributes(TextEditor text) {
        Collection<HTMLToken> tokens = text.tokenizeHTML();
        TextEditor newText = new TextEditor("");

        tokens.stream().map(token ->
        {
            String value = token.getText();

            if (token.isTag())
            {
                value = value.replaceAll("\\\\", CHAR_PROTECTOR.encode("\\"));
                value = value.replaceAll("`", CHAR_PROTECTOR.encode("`"));
                value = value.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                value = value.replaceAll("_", CHAR_PROTECTOR.encode("_"));
            }

            return value;
        }).forEachOrdered(value -> newText.append(value));

        return newText;
    }

    /**
     * Form paragraphs.
     *
     * @param markup to process.
     *
     * @return new TextEditor with modified text.
     */
    private static TextEditor formParagraphs(TextEditor markup) {
        markup.deleteAll("\\A\\n+");
        markup.deleteAll("\\n+\\z");
        markup.replaceAll("(?:\\A|\\n)" + CODE_BLOCK_BEGIN + "(\\w+?)" + CODE_BLOCK_END + "(?:\\n|\\Z)", "\n\n$1\n\n");

        String[] paragraphs;

        if (markup.isEmpty())
        {
            paragraphs = new String[0];
        } else
        {

            paragraphs = compile("\\n{2,}").split(markup.toString());
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

    private static boolean hasParagraphBreak(final TextEditor item) {
        return item.toString().contains("\n\n");
    }

    private static void hashHTMLBlocks(final TextEditor text) {
        // Hashify HTML blocks:
        // We only want to do this for block-level HTML tags, such as headers,
        // lists, and tables. That's because we still want to wrap <p>s around
        // "paragraphs" that are wrapped in non-block-level tags, such as anchors,
        // phrase emphasis, and spans. The list of tags we're looking for is
        // hard-coded:

        String[] tagsA =
        {
            "head",
            "p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "pre", "table",
            "dl", "ol", "ul", "script", "noscript", "form", "fieldset", "iframe", "math"
        };

        String[] tagsB =
        {
            "ins", "del"
        };
        String alternationA = String.join("|", tagsA);
        String alternationB = alternationA + "|" + String.join("|", tagsB);

        int lessThanTab = TAB_WIDTH - 1;

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

        Replacement protectHTML = (Matcher m) ->
        {
            String literal = m.group();
            return "\n\n" + HTML_PROTECTOR.encode(literal) + "\n\n";
        };

        text.replaceAll(p1, protectHTML);

        // Now match more liberally, simply from `\n<tag>` to `</tag>\n`
        Pattern p2 = compile("(?:^<(" + alternationA + ")\\b[^>]*>"
                             + "(((?!(<\\1|</\\1)).)*\\n)*?"
                             + ".*</\\1\\b[^>]*>[ ]*"
                             + "(?=\\n+|\\Z))", MULTILINE | CASE_INSENSITIVE);
        do
        {
            text.replaceAll(p2, protectHTML);
        } while (text.wasFound());

        // Special case for <hr>
        Pattern p3 = compile("(?:"
                             + "(?<=\\n\\n)"
                             + "|"
                             + "\\A\\n?"
                             + ")"
                             + "("
                             + "[ ]{0," + lessThanTab + "}"
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
                             + "[ ]{0," + lessThanTab + "}"
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

    private static boolean isEmptyString(final String leadingLine) {
        return leadingLine == null || leadingLine.isEmpty();
    }

    /**
     * Process list items with checkboxes.
     * <p>
     * <b>The basic checkbox:</b><br>
     * The syntax has a number of settings:
     * <pre><code>
     *- [ ] An unchecked mutable checkbox.
     *- [ ]! An unchecked immutable checkbox.
     *- [x] A checked mutable checkbox.
     *- [X]! A checked immutable checkbox.
     * </code></pre>
     * will produce html like this:
     * <pre><code>
     * &lt;ul class="checkbox"&gt;
     * &lt;li&gt;&lt;input type="checkbox"&gt;An unchecked mutable checkbox.&lt;/li&gt;
     * &lt;li&gt;&lt;input type="checkbox" disabled&gt;An unchecked immutable checkbox.&lt;/li&gt;
     * &lt;li&gt;&lt;input type="checkbox" checked&gt;A checked mutable checkbox.&lt;/li&gt;
     * &lt;li&gt;&lt;input type="checkbox" checked disabled&gt;A checked immutable checkbox.&lt;/li&gt;
     * &lt;/ul&gt;
     * </code></pre>
     * Both 'x' and 'X' are useable.
     * <p>
     * <b>The class attribute:</b><br>
     * Each check box can have a class attribute:
     * <pre><code>
     *- [ ][@myClass] An unchecked mutable checkbox with the class attribute: myClass.
     *- [ ]![@myClass2] An unchecked immutable checkbox with the class attribute: myClass2.
     *- [x][@yourClass] A checked mutable checkbox with the class attribute: yourClass.
     *- [X]![@bestClass] A checked immutable checkbox with the class attribute: bestClass.
     * </code></pre>
     * will produce html like this:
     * <pre><code>
     * &lt;ul class="checkbox"&gt;
     * &lt;li class="myClass"&gt;&lt;input type="checkbox"&gt;An unchecked mutable checkbox with the class attribute: &lt;code&gt;myClass&lt;/code&gt;.&lt;/li&gt;
     * &lt;li class="myClass2"&gt;&lt;input type="checkbox" disabled&gt;An unchecked immutable checkbox with the class attribute: &lt;code&gt;myClass2&lt;/code&gt;.&lt;/li&gt;
     * &lt;li class="yourClass"&gt;&lt;input type="checkbox" checked&gt;A checked mutable checkbox with the class attribute: &lt;code&gt;yourClass&lt;/code&gt;.&lt;/li&gt;
     * &lt;li class="bestClass"&gt;&lt;input type="checkbox" checked disabled&gt;A checked immutable checkbox with the class attribute: &lt;code&gt;bestClass&lt;/code&gt;.&lt;/li&gt;
     * &lt;/ul&gt;
     * </code></pre>
     *
     * @param item        to be processed.
     * @param classString returns the value of the 'class' text, if any.
     *
     * @return {@code true} if check box found, {@code false} otherwise.
     */
    private static boolean processCheckBoxes(final TextEditor item, final StringReturn classString) {
        final String regex = "^\\[(?<checked>[ xX])\\](?<disabled>[!])?" + CLASS_REGEX + "[ ]+(?<text>[^ ]+.*)\\n";
        final Pattern p = compile(regex);

        Replacement processCheckBox = (Matcher m) ->
        {
            StringBuilder sb = new StringBuilder("<input type=\"checkbox\"");
            String checked = m.group("checked");
            String disabled = m.group("disabled");
            classString.val = m.group("classes");
            String text = m.group("text");

            if (!checked.isBlank())
            {
                sb.append(" checked");
            }

            if (disabled != null && !disabled.isBlank())
            {
                sb.append(" disabled");
            }

            sb.append(">").append(text).append("\n");

            return sb.toString();
        };

        item.replaceAll(p, processCheckBox);
        return item.wasFound();
    }

    /**
     * Process list items.
     * <p>
     * Includes 'Check Boxes' and items with a 'class' attribute.
     *
     * @param list Text to be processed and then returns the modified text.
     *
     * @return {@code true} if any check boxes found, {@code false} otherwise.
     */
    private static boolean processListItems(final StringReturn list) {
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
        listLevel.val++;
        final BooleanReturn rtn = new BooleanReturn();

        // Trim trailing blank lines:
        list.val = replaceAll(list.val, "\\n{2,}\\z", "\n");

        Pattern p = compile("(\\n)?"
                            + "^([ ]*)([-+*]|\\d+[.])[ ]+"
                            + "((?s:.+?)(\\n{1,2}))"
                            + "(?=\\n*(\\z|\\2([-+*]|\\d+[.])[ ]+))",
                            MULTILINE);
        list.val = replaceAll(list.val, p, (Matcher m) ->
                      {
                          String text = m.group(4);
                          TextEditor item = new TextEditor(text);
                          String leadingLine = m.group(1);
                          StringReturn classRtn = new StringReturn();

                          if (!isEmptyString(leadingLine) || hasParagraphBreak(item))
                          {
                              item = runBlockGamut(item.outdent());
                          } else
                          {
                              rtn.val |= doExtendedListOptions(item, classRtn);
                              // Recurse sub-lists
                              item = doLists(item.outdent());
                              item = runSpanGamut(item);
                          }

                          return "<li"
                                 + (classRtn.val != null && !classRtn.val.isBlank() ? addClass(classRtn.val) : "")
                                 + ">" + item.trim().toString() + "</li>\n";
                      });

        listLevel.val--;
        return rtn.val;
    }

    /**
     * Process list items with a class attribute.
     * <p>
     * Each line item can have its own class attribute:
     * <pre><code>
     * - [myClass] This is my line.
     * - [yourClass] This is your line.
     * </code></pre>
     * will produce html like this:
     * <pre><code>
     * &lt;li class="myClass"&gt;This is my line.&lt;/li&gt;
     * &lt;li class="yourClass"&gt;This is your line.&lt;/li&gt;
     * </code></pre>
     *
     * @param item        to be processed.
     * @param classString returns the value of the 'class' text, if any.
     *
     * @since 14/12/2020.
     */
    private static void processListItemsWithAClass(final TextEditor item, final StringReturn classString) {
        final String regex = "^" + CLASS_REGEX + "[ ]+(?<text>[^ ]+.*)\\n";
        final Pattern p = compile(regex);

        Replacement processClass = (Matcher m) ->
        {
            StringBuilder sb = new StringBuilder();
            classString.val = m.group("classes");
            String text = m.group("text");

            sb.append(text).append("\n");

            return sb.toString();
        };

        item.replaceAll(p, processClass);
    }

    private static String replaceAll(final String text, final String regex, final String replacement) {
        return new TextEditor(text)
                .replaceAll(regex, replacement)
                .toString();
    }

    private static String replaceAll(final String text, final Pattern pattern, final Replacement replacement) {
        return new TextEditor(text)
                .replaceAll(pattern, replacement)
                .toString();
    }

    /**
     * Run all block level processing.
     *
     * @param text to process.
     *
     * @return new TextEditor with modified text.
     */
    private static TextEditor runBlockGamut(final TextEditor text) {
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

    /**
     * Run &lt;span&gt; level processing.
     *
     * @param text to process.
     *
     * @return new TextEditor with modified text.
     */
    private static TextEditor runSpanGamut(TextEditor text) {
        text = escapeSpecialCharsWithinTagAttributes(text);
        doCodeSpans(text);
        encodeBackslashEscapes(text);

        doSubSup(text);
        doDelIns(text);
        doImages(text);
        doAnchors(text, false);
        doAutoLinks(text);

        // Fix for BUG #1357582
        // We must call escapeSpecialCharsWithinTagAttributes() a second time to
        // escape the contents of any attributes generated by the prior methods.
        // - Nathan Winant, nw@exegetic.net, 8/29/2006
        text = escapeSpecialCharsWithinTagAttributes(text);

        encodeAmpsAndAngles(text);
        doStrongEmAndBoldItalics(text);

        // Manual line breaks
        return text.replaceAll(" {2,}\n", " <br>\n");
    }

    // [id] is now case sensitive
    // Added class attribute:
    // [<id>]:{@<classname>]<url> "title"
    //
    // Bradley Willcott (24/12/2020)
    //
    private static void stripLinkDefinitions(TextEditor text) {
        Pattern p = compile("^[ ]{0,3}\\[(?<id>.+)\\]:"
                            // ID = $1
                            // BW:
                            + CLASS_REGEX
                            + "[ ]*\\n?[ ]*"
                            // Space
                            + "<?(?<url>[^'\"]+?)?>?"
                            // URL = $2
                            + "[ ]*\\n?[ ]*"
                            // Space
                            + "(?:(?<quote>['\"])(?<title>.+?)\\k<quote>[ ]*)?"
                            // Optional title = $3
                            + "(?:\\n+|\\Z)",
                            MULTILINE);

        text.replaceAll(p, (Matcher m) ->
                {
                    String id = m.group("id");
                    String classes = m.group("classes");
                    String url = m.group("url");
                    String title = m.group("title");

                    // BW:
                    if (classes == null)
                    {
                        classes = "";
                    }

                    if (url != null)
                    {
                        url = encodeAmpsAndAngles(new TextEditor(url)).toString();
                    }

                    if (title == null)
                    {
                        title = "";
                    }

                    title = replaceAll(title, "\"", "&quot;");
                    // BW:
                    linkDefinitions.put(id, new LinkDefinition(classes, url, title));
                    return "";
                });
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
    private static Tag tag(String text) {
        Pattern pId = compile("(?<!\\\\)" + ID_REGEX);
        Pattern pClasses = compile("^(?<!\\\\)" + CLASS_REGEX);
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

    private static void unEscapeSpecialChars(TextEditor ed) {
        CHAR_PROTECTOR.getAllEncodedTokens().forEach(hash ->
        {
            String plaintext = CHAR_PROTECTOR.decode(hash);
            ed.replaceAllLiteral(hash, plaintext);
        });
    }

    static TextEditor doAnchors(final TextEditor markup, final boolean doLimited) {
        if (!doLimited)
        {

            // Internal references: [link text][id]!
            //
            // Added class attribute to anchors.
            // [link text][id]![@class]
            //
            // Bradley Willcott (15/12/2020)
            Pattern internalLink = compile(""
                                           + "\\[(?<linkText>[^\\[\\]]*?)\\]"
                                           // Link text = $1
                                           // + "[ ]?(?:\\n[ ]*)?"  // No whitespace between the brackets (GFM)
                                           + "\\[(?<linkId>[^\\]]*?)\\]"
                                           // ID = $2
                                           + "(?<target>!)?"
                                           + CLASS_REGEX); // BW
            markup.replaceAll(internalLink, (Matcher m) ->
                      {
                          String replacementText;
                          String wholeMatch = m.group();
                          String linkText = m.group("linkText");
                          String linkId = m.group("linkId");
                          String targetTag = m.group("target") != null ? TARGET : "";
                          // BW: classString
                          String classes = m.group("classes");

                          // [linkId] is now case sensitive
                          if (linkId == null || "".equals(linkId))
                          { // for shortcut links like [this][]
                              linkId = linkText;
                          }

                          LinkDefinition defn = linkDefinitions.get(linkId);

                          if (defn != null)
                          {
                              // BW:
                              String url = defn.url;

                              if (url != null)
                              {
                                  // protect emphasis (* and _) within urls
                                  url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                                  url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                              }

                              // BW: classAtrib
                              String classAtrib = addClass(defn.classes, classes);

                              // BW:
                              String title = defn.title;
                              String titleTag = "";

                              if (title != null && !title.equals(""))
                              {
                                  // protect emphasis (* and _) within urls
                                  title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                                  title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                                  titleTag = " title=\"" + title + "\"";
                              }

                              if (url != null)
                              {
                                  replacementText = "<a href=\"" + url + "\""
                                                    // BW: classAtrib
                                                    + classAtrib + titleTag + targetTag + ">"
                                                    + linkText + "</a>";
                              } else
                              {
                                  replacementText = "<a"
                                                    // BW: classAtrib
                                                    + classAtrib + titleTag + targetTag + ">"
                                                    + linkText + "</a>";
                              }
                          } else
                          {
                              replacementText = wholeMatch;
                          }

                          return replacementText;
                      });

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
            Pattern inlineLink = compile(""
                                         // Whole match = $1
                                         + "\\[(?<linkText>[^\\[\\]]*?)\\]"
                                         // Link text = $2
                                         + "(?<target>!)?"
                                         + CLASS_REGEX // BW
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
            markup.replaceAll(inlineLink, (Matcher m) ->
                      {
                          String linkText = m.group("linkText");
                          String url = m.group("url");
                          String title = m.group("title");
                          String targetTag = m.group("target") != null ? TARGET : "";
                          // BW: classString
                          String classes = m.group("classes");

                          // protect emphasis (* and _) within urls
                          url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                          url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                          StringBuilder result = new StringBuilder();
                          result.append("<a href=\"").append(url).append("\"");

                          // BW: classAtrib
                          String classAtrib = classes != null && !classes.isBlank()
                                              ? addClass(classes) : "";
                          result.append(classAtrib);

                          if (title != null)
                          {
                              // protect emphasis (* and _) within urls
                              title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                              title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                              title = replaceAll(title, "\"", "&quot;");
                              result.append(" title=\"");
                              result.append(title);
                              result.append("\"");
                          }

                          if (!targetTag.isEmpty())
                          {
                              result.append(TARGET);
                          }

                          result.append(">").append(linkText);
                          result.append("</a>");

                          return result.toString();
                      });
        }

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
        Pattern referenceShortcut = compile("(?!\\[#|\\[\\d)" // BW:
                                            + "\\["
                                            + "\\^?" // BW:
                                            + "(?<linkText>[^\\[\\]]+?)"
                                            // link text can't contain ']'
                                            + "\\]"
                                            + "(?<target>!)?"
                                            + CLASS_REGEX, // BW
                                            DOTALL);
        markup.replaceAll(referenceShortcut, (Matcher m) ->
                  {
                      String replacementText;
                      String wholeMatch = m.group();
                      String linkText = m.group("linkText");
                      String targetTag = m.group("target") != null ? TARGET : "";
                      String id = linkText; // link id is now case sensitive
                      id = id.replaceAll("[ ]?\\n", " "); // change embedded newlines into spaces
                      // BW: classString
                      String classes = m.group("classes");

                      LinkDefinition defn = linkDefinitions.get(id);

                      if (defn != null)
                      {
                          // BW:
                          String url = defn.url;

                          if (url != null)
                          {
                              // protect emphasis (* and _) within urls
                              url = url.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                              url = url.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                          }

                          // BW: classAtrib
                          String classAtrib = addClass(defn.classes, classes);

                          // BW:
                          String title = defn.title;
                          String titleTag = "";

                          if (title != null && !title.equals(""))
                          {
                              // protect emphasis (* and _) within urls
                              title = title.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                              title = title.replaceAll("_", CHAR_PROTECTOR.encode("_"));
                              titleTag = " title=\"" + title + "\"";
                          }

                          // Added superscripted footnote reference
                          // (14/03/2020) Bradley Willcott
                          Matcher footnote = compile("\\A(\\d+)\\z").matcher(linkText);

                          if (footnote.find())
                          {
                              linkText = doSubSup(new TextEditor("++" + CHAR_PROTECTOR.encode("[") + (url != null ? linkText : "*") + "]++")).toString();
                          }

                          if (url != null)
                          {
                              replacementText = "<a href=\"" + url + "\""
                                                // BW: classAtrib
                                                + classAtrib + titleTag + targetTag + ">"
                                                + linkText + "</a>";
                          } else
                          {
                              replacementText = "<a"
                                                // BW: classAtrib
                                                + classAtrib + titleTag + targetTag + ">"
                                                + linkText + "</a>";
                          }
                      } else
                      {
                          replacementText = wholeMatch;
                      }

                      return replacementText;
                  });

        return markup;
    }

    static TextEditor encodeCode(TextEditor ed) {
        ed.replaceAll("\\\\&", CHAR_PROTECTOR.encode("&"));
        ed.replaceAll("--", CHAR_PROTECTOR.encode("--"));
        ed.replaceAll("\\+\\+", CHAR_PROTECTOR.encode("++"));
        ed.replaceAll("\\-!", CHAR_PROTECTOR.encode("-!"));
        ed.replaceAll("\\+!", CHAR_PROTECTOR.encode("+!"));
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

    static String processGroupText(String text) {
        if (text != null && !text.isBlank())
        {
            // Escaped pipes need to be handled
            return runSpanGamut(new TextEditor(text)).toString();
        } else
        {
            return "";
        }
    }

    /**
     * <del>Creates a new Markdown processor.</del>
     *
     * @deprecated No longer required to be instantiated, as everything is now
     * <i>static</i>.
     */
    @Deprecated
    public MarkdownProcessor() {
    }

    @Override
    public String toString() {
        return "Markdown Processor for Java 0.6.3 (should still be compatible with Markdown 1.0.2b2)";
    }

    /**
     * Used by {@link #tag(java.lang.String) tag()}.
     *
     * @author Bradley Willcott
     *
     * @since 0.6
     * @version 0.6.8
     */
    public static class Tag {

        public final String text;
        public final String classes;
        public final String id;

        public Tag(String text, String classes, String id) {
            this.text = text;
            this.classes = addClass(classes);
            this.id = addId(id);
        }
    }

    protected enum Fenced {
        YES, NO, UNFENCED
    }
}
