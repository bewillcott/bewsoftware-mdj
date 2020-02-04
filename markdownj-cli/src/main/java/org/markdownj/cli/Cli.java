/*
 * Copyright (C) 2020 Bradley Willcott
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.markdownj.cli;

import java.io.*;
import org.markdownj.MarkdownProcessor;

/**
 *
 * @author Bradley Willcott
 */
public class Cli {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final String CtlX = Character.toString(24);

        StringBuilder buf = new StringBuilder();
        BufferedReader in = null;
        Reader reader = null;

        if (args.length > 0) {
            try {
                reader = new FileReader(args[0]);
            } catch (FileNotFoundException ex) {
                System.err.println("Error opening file: " + args[0] + "\n" + ex.getMessage());
                System.exit(1);
            }
        } else {
            reader = new InputStreamReader(System.in);
        }

        in = new BufferedReader(reader);
        String input = "";

        try {
            while (input != null) {
                while ((input = in.readLine()) != null && !input.equals(CtlX)) {
                    buf.append(input).append("\n");
                }

                if (buf.length() == 0) {
                    input = null;
                } else {
                    System.out.println(new MarkdownProcessor().markdown(buf.toString()));
                    buf.setLength(0);
                }
            }
        } catch (java.io.IOException ex) {
            System.err.println("Error reading input: " + ex.getMessage());
            System.exit(1);
        }
    }

}
