/*
 * Copyright (c) 2005, Pete Bevin.
 * <http://markdownj.petebevin.com>
 *
 * Copyright (c) 2020, Bradley Willcott.
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
package com.bewsoftware.mdj.core.utils;

import com.bewsoftware.annotations.jcip.Immutable;

/**
 * Used to store the link definitions.
 * <p>
 * <b>Changes:</b>
 * <ul>
 * <li>Added classes field</li>
 * <li>Internal fields are now {@code public}</li>
 * <li>Removed getters</li>
 * <li>Updated {@link #toString()}</li>
 * <li>Added Javadoc comments</li>
 * </ul>
 * Bradley Willcott (24/12/2020)
 *
 * @since before v0.5
 * @version 0.8.0
 */
@Immutable
public class LinkDefinition
{
    /**
     * The link's classes: {@code class="<classes>"}.
     */
    public final String classes;

    /**
     * The link's title: {@code title="<title>"}.
     */
    public final String title;

    /**
     * The link's url: {@code href="<url>"}.
     */
    public final String url;

    /**
     * Instantiate the class.
     *
     * @param classes The link's classes.
     * @param url     The link's url.
     * @param title   The link's title.
     */
    public LinkDefinition(
            final String classes,
            final String url,
            final String title
    )
    {
        this.classes = classes;
        this.url = url;
        this.title = title;
    }

    @Override
    public String toString()
    {
        return "[@" + classes + "]" + url + " (" + title + ")";
    }
}
