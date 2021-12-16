/*
 * Copyright (c) 2020, 2021 Bradley Willcott
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

import com.bewsoftware.mdj.core.plugins.replacements.CodeBlock;
import com.bewsoftware.mdj.core.TextEditor;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.utils.Constants.ID_REGEX_OPT;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.LANG_IDENTIFIER;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Fenced Code Blocks:<br>
 * <ol>
 * <li>This plugin class must be executed <em>before</em> {@link CodeBlocks} to
 * prevent mashing of the Fenced Code Block text.
 * </li>
 * <li>
 * Use either triple back-ticks: {@code ```}, or triple tildes: {@code ~~~}
 * for the beginning and end of the code block.
 * </li>
 * </ol>
 * <dl>
 * <dt><b>NOTE:</b></dt>
 * <dd>You MUST use the same characters for the end of the block, as you
 * used
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
 * <dd>Can now add classes to both the &lt;pre&gt; and &lt;code&gt;
 * tags:</dd>
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
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public class FencedCodeBlocks implements TextConvertor
{
    public FencedCodeBlocks()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        Pattern pattern = compile("(?<frontFence>^(?:[~]{3}|[`]{3}))"
                + ID_REGEX_OPT
                + "(?:[ ]*\\n"
                + "|(?<classes>\\[(?:@\\p{Alpha}[^\\]]*)?\\](?:\\[(?:@\\p{Alpha}[^\\]]*)?\\])?)?[ ]*\\n"
                + "|(?<class>" + LANG_IDENTIFIER + ".+)?\\n)?"
                + "(?<body>(?:.*?\\n+)+?)"
                + "(?:\\k<frontFence>)[ ]*\\n", MULTILINE);
        return text.replaceAll(pattern, new CodeBlock(true));
    }
}
