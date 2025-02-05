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

/**
 * CleanupMarkdownText class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public class CleanupMarkdownText implements TextConvertor
{
    public CleanupMarkdownText()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        // Standardize line endings:
        text.replaceAll("\\r\\n", "\n"); 	// DOS to Unix
        text.replaceAll("\\r", "\n");    	// Mac to Unix

        // Strip any lines consisting only of spaces and tabs.
        // This makes subsequent regexes easier to write, because we can
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

        return text;
    }

}
