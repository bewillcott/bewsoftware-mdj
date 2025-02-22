/*
 * Copyright (c) 2005, Pete Bevin.
 * <http://markdownj.petebevin.com>
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
 *
 */
package com.bewsoftware.mdj.core;

import com.bewsoftware.mdj.core.HTMLToken;
import com.bewsoftware.mdj.core.Replacement;
import com.bewsoftware.mdj.core.plugins.utils.Constants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.utils.Constants.TAB_WIDTH;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;

/**
 * Mutable String with common operations used in Markdown processing.
 * <p>
 * <b>Changes:</b>
 * <ul>
 * <li>Set all method parameters to be {@code final}.</li>
 * </ul>
 * Bradley Willcott (02/01/2021)
 *
 * @since 0.1
 * @version 0.8.0
 */
public class TextEditor
{
    private boolean found;

    private StringBuilder text;

    /**
     * Create a new TextEditor based on the contents of a String or
     * StringBuffer.
     *
     * @param text to be processed.
     */
    public TextEditor(final CharSequence text)
    {
        this.text = new StringBuilder(text);
    }

    /**
     * Add a string to the end of the buffer.
     *
     * @param s text to be processed.
     */
    public void append(final CharSequence s)
    {
        text.append(s);
    }

    /**
     * Remove all occurrences of the given regex pattern, replacing them with
     * the empty string.
     *
     * @param pattern Regular expression
     *
     * @return this object for chaining purposes.
     *
     * @see java.util.regex.Pattern
     */
    public TextEditor deleteAll(final String pattern)
    {
        return replaceAll(pattern, "");
    }

    /**
     * Convert tabs to spaces given the default tab width.
     *
     * @return this object for chaining purposes.
     *
     * @see Constants#TAB_WIDTH
     */
    public TextEditor detabify()
    {
        return detabify(TAB_WIDTH);
    }

    /**
     * Convert tabs to spaces.
     *
     * @param tabWidth Number of spaces per tab.
     *
     * @return this object for chaining purposes.
     */
    public TextEditor detabify(final int tabWidth)
    {
        replaceAll(Pattern.compile("(.*?)\\t"), (Matcher m) ->
        {
            final String lineSoFar = m.group(1);
            int width = lineSoFar.length();
            final StringBuilder replacement = new StringBuilder(lineSoFar);

            do
            {
                replacement.append(' ');
                ++width;
            } while (width % tabWidth != 0);

            return replacement.toString();
        });

        return this;
    }

    /**
     * Introduce a number of spaces at the start of each line.
     *
     * @param spaces The number of spaces to use.
     *
     * @return this object for chaining purposes.
     */
    public TextEditor indent(final int spaces)
    {
        return replaceAll("^", " ".repeat(spaces));
    }

    /**
     * Find out whether the buffer is empty.
     *
     * @return status
     */
    public boolean isEmpty()
    {
        return text.length() == 0;
    }

    /**
     * Remove a number of spaces at the start of each line.
     *
     * @param spaces The number of spaces to use.
     *
     * @return this object for chaining purposes.
     */
    public TextEditor outdent(final int spaces)
    {
        return deleteAll("^(\\t|[ ]{1," + spaces + "})");
    }

    /**
     * Remove one tab width from the start of each line.
     *
     * @return this object for chaining purposes.
     *
     * @see Constants#TAB_WIDTH
     */
    public TextEditor outdent()
    {
        return outdent(TAB_WIDTH);
    }

    /**
     * Add a string to the start of the first line of the buffer.
     *
     * @param s text to be processed.
     */
    public void prepend(final CharSequence s)
    {
        text.insert(0, s);
    }

    /**
     * Prepend a char to each line of the text block.<br>
     * This is primarily used to protect a Code Block from being reformatted a
     * second time.
     *
     * @param c ASCII character (\x02 recommended)
     *
     * @return this object for chaining purposes.
     */
    public TextEditor prependCharToLines(final Character c)
    {
        return replaceAll("^", c.toString());
    }

    /**
     * The reverse of
     * {@link #prependCharToLines(java.lang.Character) prependCharToLines(c)}
     *
     * @param c ASCII character (\x02 recommended).  <b>MUST</b> be the same
     *          character used with {@code prependCharToLines(c)}
     *
     * @return this object for chaining purposes.
     */
    public TextEditor removePrependedCharFromLines(final Character c)
    {
        return replaceAll("^" + c.toString(), "");
    }

    /**
     * Replace all occurrences of the regular expression with the replacement.
     * The replacement string can contain $1, $2 etc. referring to matched
     * groups in the regular expression.
     *
     * @param regex       The pattern to use.
     * @param replacement The replacement text.
     *
     * @return this object for chaining purposes.
     */
    public TextEditor replaceAll(final String regex, final String replacement)
    {
        if (text.length() > 0)
        {
            final String r = replacement;
            final Pattern p = Pattern.compile(regex, MULTILINE);
            final Matcher m = p.matcher(text);
            final StringBuffer sb = new StringBuffer();

            found = false;

            while (m.find())
            {
                found = true;
                m.appendReplacement(sb, r);
            }

            m.appendTail(sb);
            text = new StringBuilder(sb.toString());
        }

        return this;
    }

    /**
     * Replace all occurrences of the Pattern. The Replacement object's
     * replace() method is called on each match, and it provides a replacement,
     * which is placed literally (i.e., without interpreting $1, $2 etc.)
     *
     * @param pattern     The pattern to use.
     * @param replacement The replacement to use.
     *
     * @return this object for chaining purposes.
     */
    public TextEditor replaceAll(final Pattern pattern, final Replacement replacement)
    {
        final Matcher m = pattern.matcher(text);
        int lastIndex = 0;
        final StringBuilder sb = new StringBuilder();

        found = false;

        while (m.find())
        {
            found = true;
            sb.append(text.subSequence(lastIndex, m.start()));
            sb.append(replacement.process(m));
            lastIndex = m.end();
        }

        sb.append(text.subSequence(lastIndex, text.length()));
        text = sb;

        return this;
    }

    /**
     * Same as replaceAll(String, String), but does not interpret $1, $2 etc. in
     * the replacement string.
     *
     * @param regex       The pattern to use.
     * @param replacement The replacement text.
     *
     * @return this object for chaining purposes.
     */
    public TextEditor replaceAllLiteral(final String regex, final String replacement)
    {
        return replaceAll(Pattern.compile(regex, MULTILINE), (Matcher m) -> replacement);
    }

    /**
     * Give up the contents of the TextEditor.
     *
     * @return this object for chaining purposes.
     */
    @Override
    public String toString()
    {
        return text.toString();
    }

    /**
     * Parse HTML tags, returning a Collection of HTMLToken objects.
     *
     * @return tokens collection.
     */
    public Collection<HTMLToken> tokenizeHTML()
    {
        final List<HTMLToken> tokens = new ArrayList<>();
        final String nestedTags = nestedTagsRegex(6);

        final Pattern p = Pattern.compile(""
                + "(?s:<!(--.*?--\\s*)+>)"
                + "|"
                + "(?s:<\\?.*?\\?>)"
                + "|"
                + nestedTags
                + "", CASE_INSENSITIVE);

        final Matcher m = p.matcher(text);
        int lastPos = 0;

        while (m.find())
        {
            if (lastPos < m.start())
            {
                tokens.add(HTMLToken.text(text.substring(lastPos, m.start())));
            }

            tokens.add(HTMLToken.tag(text.substring(m.start(), m.end())));
            lastPos = m.end();
        }

        if (lastPos < text.length())
        {
            tokens.add(HTMLToken.text(text.substring(lastPos, text.length())));
        }

        return tokens;
    }

    /**
     * Remove leading and trailing space from the start and end of the buffer.
     * Intermediate lines are not affected.
     *
     * @return this object for chaining purposes.
     */
    public TextEditor trim()
    {
        text = new StringBuilder(text.toString().trim());

        return this;
    }

    /**
     * Result of last regex search performed by replaceAll*() methods.
     *
     * @see TextEditor#replaceAll(Pattern, Replacement)
     * @see TextEditor#replaceAll(String, String)
     * @see TextEditor#replaceAllLiteral(String, String)
     *
     * @return {@code True} if found, {@code false} otherwise.
     */
    public boolean wasFound()
    {
        return found;
    }

    /**
     * Regex to match a tag, possibly with nested tags such as <a
     * href="<MTFoo>">.
     *
     * @param depth - How many levels of tags-within-tags to allow. The example
     *              <a href="<MTFoo>"> has depth 2.
     */
    private String nestedTagsRegex(final int depth)
    {
        if (depth == 0)
        {
            return "";
        } else
        {
            return "(?:<[a-z/!$](?:[^<>]|" + nestedTagsRegex(depth - 1) + ")*>)";
        }
    }
}
