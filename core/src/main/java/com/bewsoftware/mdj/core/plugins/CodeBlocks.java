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

import com.bewsoftware.mdj.core.TextEditor;
import com.bewsoftware.mdj.core.plugins.replacements.CodeBlock;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.MarkdownProcessor.LANG_IDENTIFIER;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * This plugin class builds spaced or tabbed Code Blocks.
 * <p>
 * The plugin class: {@link FencedCodeBlocks} must be executed <em>before</em>
 * this one to prevent mashing of the Fenced Code Block text.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
public class CodeBlocks implements TextConvertor
{
    public CodeBlocks()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        /*
         * If there are any Fenced Code Blocks, they will have been protected
         * from 'pattern' by HTML_PROTECTOR.encode(..). This prevents 'pattern'
         * from removing 4 spaces from the beginning of each line of the
         * Fenced Code Blocks.
         *
         * Any tabbed or space type block will still be processed by 'pattern'.
         *
         * Bradley Willcott (08/01/2020)
         */
        Pattern pattern = compile("(?:(?<id>)?(?<classes>)?(?<=\\n\\n)|\\A)"
                + "(?:^(?:> )*?[ ]{4})?(?<class>" + LANG_IDENTIFIER + ".+)?(?:\\n)?"
                + "(?<body>(?:"
                + "(?:^(?:> )*?[ ]{4}).*\\n+)+)"
                + "(?:(?=^\\n+)|\\Z)", MULTILINE);
        return text.replaceAll(pattern, new CodeBlock(false));
    }
}
