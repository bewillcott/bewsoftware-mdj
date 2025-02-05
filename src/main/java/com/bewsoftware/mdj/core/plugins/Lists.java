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
import com.bewsoftware.utils.Ref;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.doLists;
import static com.bewsoftware.mdj.core.plugins.PluginInterlink.runBlockGamut;
import static com.bewsoftware.mdj.core.plugins.PluginInterlink.runSpanGamut;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.hasParagraphBreak;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.isEmptyString;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.replaceAll;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addId;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CLASS_REGEX_OPT;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.ID_REGEX_OPT;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.TAB_WIDTH;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

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
 * Both the `[#&lt;id&gt;]` and `[@&lt;classname&gt;]` attributes are
 * optional,
 * but if both are supplied they <i>must</i> be in the order shown above.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public class Lists implements TextConvertor
{
    private static final int LESS_THAN_TAB = TAB_WIDTH - 1;

    private static final String LIST_TYPE = "[*+-]";

    private static final String WHOLE_LIST
            = "(?<list>(?:[ ]{0," + LESS_THAN_TAB + "}"
            + "(?<listType>" + LIST_TYPE + "|\\d+[.])"
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
            + "(?![ ]*(?:" + LIST_TYPE + "|\\d+[.])[ ]+)"
            + "))";

    private static final Ref<Integer> listLevel = Ref.val(0);

    public Lists()
    {
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
     * @return {@code true} if any Checkboxes were found, {@code false}
     *         otherwise.
     *
     * @since 10/01/2020, last updated: 14/12/2020.
     */
    private static boolean doExtendedListOptions(final TextEditor item, final Ref<String> classString)
    {

        boolean rtn = processCheckBoxes(item, classString);

        if (!rtn)
        {
            processListItemsWithAClass(item, classString);
        }

        return rtn;
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
     * Bradley Willcott (20/04/2020)
     *
     * @param item        to be processed.
     * @param classString returns the value of the 'class' text, if any.
     *
     * @return {@code true} if check box found, {@code false} otherwise.
     */
    private static boolean processCheckBoxes(final TextEditor item, final Ref<String> classString)
    {
        final String regex = "^\\[(?<checked>[ xX])\\](?<disabled>[!])?" + CLASS_REGEX_OPT + "[ ]+(?<text>[^ ]+.*)\\n";
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
     * <b>Changes:</b>
     * <ul>
     * <li>Includes 'Check Boxes' and items with a 'class' attribute.</li>
     * </ul>
     * Bradley Willcott (20/04/2020)
     *
     * @param list Text to be processed and then returns the modified text.
     *
     * @return {@code true} if any check boxes found, {@code false} otherwise.
     */
    private static boolean processListItems(final Ref<String> list)
    {
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

        // Trim trailing blank lines:
        list.val = replaceAll(list.val, "\\n{2,}\\z", "\n");
        ListItem listItem = new ListItem();
        list.val = replaceAll(list.val, ListItem.PATTERN, listItem);

        listLevel.val--;
        return listItem.checkBoxesFound;
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
     * <p>
     * Bradley Willcott (20/04/2020)
     *
     * @param item        to be processed.
     * @param classString returns the value of the 'class' text, if any.
     *
     * @since 14/12/2020.
     */
    private static void processListItemsWithAClass(final TextEditor item, final Ref<String> classString)
    {
        final String regex = "^" + CLASS_REGEX_OPT + "[ ]+(?<text>[^ ]+.*)\\n";
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

    @Override
    public TextEditor execute(TextEditor text)
    {

        if (listLevel.val == 0)
        {
            text.replaceAll(ListStarter.PATTERN, new ListStarter());
        } else
        {
            text.replaceAll(List.PATTERN, new List());
        }

        return text;
    }

    private String getListType(Matcher m)
    {
        return m.group("listType").matches(LIST_TYPE) ? "ul" : "ol";
    }

    private String processCheckBoxes(boolean checkboxes, String classAttrib)
    {
        if (checkboxes)
        {
            classAttrib = "checkbox";
        }

        return classAttrib;
    }

    private String processClasses(String classes, String classAttrib)
    {
        if (classes != null)
        {
            classAttrib = !classAttrib.isBlank() ? classAttrib + " " + classes : classes;
        }

        return classAttrib;
    }

    private String processFinalString(String listType, String id, String classAttrib, Ref<String> list)
    {
        //
        // Added 'class=' attribute for when list contains checkboxes.
        //
        // Bradley Willcott (13/12/2020)
        //
        return "<" + listType + addId(id) + addClass(classAttrib) + ">\n" + list + "</" + listType + ">\n";
    }

    private void turnDoubleReturnsIntoTripleReturns(Ref<String> list)
    {
        // Turn double returns into triple returns, so that we can make a
        // paragraph for the last item in a list, if necessary:
        list.val = replaceAll(list.val, "\\n{2,}", "\n\n\n");
    }

    private static class ListItem implements Replacement
    {
        public static final Pattern PATTERN = compile("(\\n)?"
                + "^([ ]*)([-+*]|\\d+[.])[ ]+"
                + "((?s:.+?)(\\n{1,2}))"
                + "(?=\\n*(\\z|\\2([-+*]|\\d+[.])[ ]+))",
                MULTILINE);

        public boolean checkBoxesFound = false;

        private ListItem()
        {
        }

        @Override
        public String process(Matcher m)
        {
            Ref<String> classRtn = Ref.val();

            String leadingLine = m.group(1);
            TextEditor item = new TextEditor(m.group(4));

            if (!isEmptyString(leadingLine) || hasParagraphBreak(item))
            {
                item = runBlockGamut(item.outdent());
            } else
            {
                checkBoxesFound |= doExtendedListOptions(item, classRtn);
                // Recurse sub-lists
                item = doLists(item.outdent());
                item = runSpanGamut(item);
            }

            return processFinalString(classRtn, item);
        }

        private String processFinalString(Ref<String> classRtn, TextEditor item)
        {
            return "<li"
                    + (classRtn.val != null && !classRtn.val.isBlank()
                    ? addClass(classRtn.val) : "")
                    + ">" + item.trim().toString() + "</li>\n";
        }
    }

    private class List implements Replacement
    {
        public static final Pattern PATTERN = compile(
                "^(?:" + CLASS_REGEX_OPT + "[ ]*\\n)?" + WHOLE_LIST, MULTILINE);

        private List()
        {
        }

        @Override
        public String process(Matcher m)
        {
            Ref<String> list = Ref.val(m.group("list"));
            String classes = m.group("classes");
            String listType = getListType(m);

            turnDoubleReturnsIntoTripleReturns(list);

            // Check boxes are processed in here...
            boolean checkboxes = processListItems(list);
            String classAttrib = "";

            classAttrib = processCheckBoxes(checkboxes, classAttrib);
            classAttrib = processClasses(classes, classAttrib);

            // Trim any trailing whitespace, to put the closing `</ol>` or `</ul>`
            // up on the preceding line, to get it past the current stupid
            // HTML block parser. This is a hack to work around the terrible
            // hack that is the HTML block parser.
            list.val = list.val.stripTrailing();

            return processFinalString(listType, "", classAttrib, list);
        }
    }

    private class ListStarter implements Replacement
    {
        public static final Pattern PATTERN = compile(
                "(?:(?<=^\\n)|\\A\\n?)(?:" + ID_REGEX_OPT + CLASS_REGEX_OPT
                + "[ ]*\\n)?" + WHOLE_LIST, MULTILINE);

        private ListStarter()
        {
        }

        @Override
        public String process(Matcher m)
        {
            Ref<String> list = Ref.val(m.group("list"));
            String id = m.group("id");
            String classes = m.group("classes");
            String listType = getListType(m);

            turnDoubleReturnsIntoTripleReturns(list);

            // Check boxes are processed in here...
            boolean checkboxes = processListItems(list);
            String classAttrib = "";

            classAttrib = processCheckBoxes(checkboxes, classAttrib);
            classAttrib = processClasses(classes, classAttrib);
            return processFinalString(listType, id, classAttrib, list);
        }
    }
}
