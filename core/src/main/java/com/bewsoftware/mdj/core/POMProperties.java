/*
 * Copyright (c) 2020, Bradley Willcott.
 * <http://www.bewsoftware.com>
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
package com.bewsoftware.mdj.core;

import java.io.IOException;
import java.util.Properties;

/**
 * Provides access to some of the project's pom.properties.
 * <p>
 * To setup in a new project:</p>
 * <ol>
 * <li>Create a new file: "pom.properties" <br>
 * Location: "src/main/resources"
 * <p>
 * </li>
 * <li><p>
 * Place the following text into that file, and save it:</p>
 * <pre>
 *<code>
 *title=${project.name}
 *description=${project.description}
 *artifactId=${project.artifactId}
 *groupId=${project.groupId}
 *version=${project.version}
 *filename=${project.build.finalName}.jar
 *</code>
 * </pre>
 * </li><li><p>
 * Add the following to your projects <b>pom.xml</b> file:</p>
 * <pre>
 *<code>
 *&lt;build&gt;
 *    &lt;resources&gt;
 *        ...
 *        &lt;resource&gt;
 *            &lt;directory&gt;src/main/resources&lt;/directory&gt;
 *            &lt;filtering&gt;true&lt;/filtering&gt;
 *            &lt;includes&gt;
 *                &lt;include&gt;&#42;&#42;/pom.properties&lt;/include&gt;
 *            &lt;/includes&gt;
 *        &lt;/resource&gt;
 *        &lt;resource&gt;
 *            &lt;directory&gt;src/main/resources&lt;/directory&gt;
 *            &lt;filtering&gt;false&lt;/filtering&gt;
 *            &lt;excludes&gt;
 *                &lt;exclude&gt;&#42;&#42;/pom.properties&lt;/exclude&gt;
 *            &lt;/excludes&gt;
 *        &lt;/resource&gt;
 *    &lt;/resources&gt;
 *        ...
 *&lt;/build&gt;
 *</code>
 * </pre>
 * </li>
 * </ol>
 * <p>
 * To access the properties:
 * </p>
 * <pre><code>
 * POMProperties pom = POMProperties.INSTANCE;
 * System.out.println(pom.title):
 * </code></pre>
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6
 * @version 0.6.1
 */
public final class POMProperties {

    /**
     * Provides single instance of this class.
     */
    public static final POMProperties INSTANCE = new POMProperties();

    public static void main(String[] args) {
        System.out.println(POMProperties.INSTANCE);
    }
    /**
     * The identifier for this artifact that is unique within
     * the group given by the group ID.
     */
    public final String artifactId;

    /**
     * Project Description
     */
    public final String description;
    /**
     * The filename of the binary output file.
     * <p>
     * This is usually a '.jar' file.
     * </p>
     */
    public final String filename;

    /**
     * Project GroupId
     */
    public final String groupId;

    /**
     * Project Name
     */
    public final String title;

    /**
     * The version of the artifact.
     */
    public final String version;

    private POMProperties() {
        Properties properties = new Properties();
        try
        {
            properties.load(POMProperties.class.getResourceAsStream("/pom.properties"));
        } catch (IOException ex)
        {
            throw new RuntimeException("FileIOError", ex);
        }

        title = properties.getProperty("title");
        description = properties.getProperty("description");
        version = properties.getProperty("version");
        artifactId = properties.getProperty("artifactId");
        groupId = properties.getProperty("groupId");
        filename = properties.getProperty("filename");
    }

    @Override
    public String toString() {
        return new StringBuilder(POMProperties.class.getName()).append(":\n")
                .append("  title: ").append(title).append("\n")
                .append("  description: ").append(description).append("\n")
                .append("  groupId: ").append(groupId).append("\n")
                .append("  artifactId: ").append(artifactId).append("\n")
                .append("  version: ").append(version).append("\n")
                .append("  filename: ").append(filename).append("\n").toString();
    }
}
