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
import com.bewsoftware.mdj.core.plugins.replacements.Table;

import static com.bewsoftware.mdj.core.plugins.utils.Constants.CHAR_PROTECTOR;

/**
 * Build tables.
 * <p>
 * The syntax for tables looks like this:
 * <pre><code>
 * This would be the table's caption.
 * | Col1 Header | Col2 Header |Col3 Header|Col 4|[].
 * | :---- | -:- | ---:| :---: |[#Id][].
 * | Left | Center |Right|Justified|[].
 * | Row 2 | More text || |[].
 * </code></pre>
 * A table begins and ends with a blank line.
 * If present, the first row above the table becomes the table's "caption".
 * Each line begins and ends with a pipe character '|' with additional such
 * to separate columns.
 * <p>
 * Each line can have an optional "[]" bracketed parameter.
 * This when empty "[]" adds a border around that row.<br>
 * If the parameter contains text, it is processed as follows:
 * <ul>
 * <li>The delimiter row sets the parameters for the '&lt;table&gt;'
 * tag.</li>
 * <li>Data rows:
 * <ul>
 * <li>If just the first one is set, then this will be used for all
 * data rows.</li>
 * <li>Subsequent rows can be set to either the same class(es) as the
 * first row or to different class(es).</li>
 * <li>Any following rows not set, will be configured using the row
 * sequencing of the first set of rows, in rotation.</li>
 * </ul></li></ul>
 * The following can now have a class attribute: [@class name(s)]
 * <ul>
 * <li>table - via the delimiter row.</li>
 * <li>caption</li>
 * <li>header</li>
 * <li>data rows</li>
 * </ul>
 * If there is an 'id', then the 'class' attribute follows immediately after
 * it.
 * Also, you can't have both a border setting and class attribute. It would
 * not
 * be sensible as the class should have <i>all</i> the settings.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class Tables implements TextConvertor
{
    public Tables()
    {
    }

    @Override
    public TextEditor execute(final TextEditor text)
    {
        // Escaped pipes need to be handled
        text.replaceAll("\\x5C\\x7C", CHAR_PROTECTOR.encode("|"));
        return text.replaceAll(Table.PATTERN, new Table());
    }
}
