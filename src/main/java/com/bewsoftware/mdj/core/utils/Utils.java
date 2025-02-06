/*
 *  File Name:    Utils.java
 *  Project Name: JCodes
 *
 *  Copyright (c) 2025 Bradley Willcott
 *
 *  TestCode is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TestCode is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.bewsoftware.mdj.core.utils;

/**
 * Provides various <i>println</i> methods, used for debugging/informational purposes.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public interface Utils
{
    /**
     * Print out the {@code text}.
     *
     * @param text to print.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    static void print(final String text)
    {
        System.out.print(text);
    }

    /**
     * Print out the {@code text}.
     *
     * @param text to print.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    static void println(final String text)
    {
        System.out.println(text);
    }

    /**
     * Print out the {@code obj}.
     *
     * @param obj to print.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    static void println(final Object obj)
    {
        System.out.println(obj);
    }

    /**
     * Print out the formatted data.
     *
     * @param format A format string as described in
     * <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/Formatter.html#syntax">Format
     * String Syntax</a>.
     * @param args   Arguments referenced by the format specifiers in the format string.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    static void println(final String format, final Object... args)
    {
        System.out.printf(format + '\n', args);
    }

    /**
     * Print a blank line.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    static void println()
    {
        System.out.println();
    }
}
