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
package com.bewsoftware.mdj.core.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * CharacterProtector class.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.7
 * @version 0.8.0
 */
public class CharacterProtector
{
    private static final String GOOD_CHARS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    private static final int NUMOFCHARS = 20;

    /**
     * Use to find encoded strings.
     * <p>
     * Provides a named group: <code>encoded</code>.<br>
     * This gives you the encoded string to provide to
     * {@link #decode(java.lang.String)}.
     * </p>
     */
    public static final String FIND_ENCODED = "^(?<encoded>[0-9a-zA-Z]{" + NUMOFCHARS + "})$";

    private final ConcurrentMap<String, String> protectMap;

    private final Random rnd;

    private final ConcurrentMap<String, String> unprotectMap;

    public CharacterProtector()
    {
        this.rnd = new Random();
        this.protectMap = new ConcurrentHashMap<>();
        this.unprotectMap = new ConcurrentHashMap<>();
    }

    public String decode(final String coded)
    {
        return unprotectMap.get(coded);
    }

    public String encode(final String literal)
    {
        final String encoded = protectMap.get(literal);

        return encoded == null ? getNewEncodedAtomically(literal) : encoded;
    }

    public Collection<String> getAllEncodedTokens()
    {
        return Collections.unmodifiableSet(unprotectMap.keySet());
    }

    @Override
    public String toString()
    {
        return protectMap.toString();
    }

    private String addToken(final String literal)
    {
        final String encoded = longRandomString();

        protectMap.put(literal, encoded);
        unprotectMap.put(encoded, literal);

        return encoded;
    }

    private synchronized String getNewEncodedAtomically(final String literal)
    {
        String encoded = protectMap.get(literal);

        return encoded == null ? addToken(literal) : encoded;
    }

    private String longRandomString()
    {
        StringBuilder sb = new StringBuilder();
        final int CHAR_MAX = GOOD_CHARS.length();

        for (int i = 0; i < NUMOFCHARS; i++)
        {
            sb.append(GOOD_CHARS.charAt(rnd.nextInt(CHAR_MAX)));
        }

        return sb.toString();
    }
}
