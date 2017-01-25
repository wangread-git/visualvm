/*
 * Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.tools.visualvm.profiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.lib.profiler.global.Platform;

/**
 * NOTE: Must be built with Source Level 1.5 to work for getting info from Java 5!
 *
 * @author Jiri Sedlacek
 */
final class JavaInfo {
    
    public static void main(String[] args) {
        for (String arg : args) System.out.println(System.getProperty(arg));
    }
    
    static String getCurrentJDKExecutable() {
        return getJDKExecutable(System.getProperty("java.home")); // NOI18N
    }
    
    static String getJDKExecutable(String jdkHome) {
        if (jdkHome == null || jdkHome.trim().length() == 0) return null;
        String jreSuffix = File.separator + "jre"; // NOI18N
        if (jdkHome.endsWith(jreSuffix)) jdkHome = jdkHome.substring(0, jdkHome.length() - jreSuffix.length());
        String jdkExe = jdkHome + File.separator + "bin" + File.separator + "java" + (Platform.isWindows() ? ".exe" : ""); // NOI18N
        return jdkExe;
    }    
    
    static String[] getSystemProperties(File java, String... keys) {
        if (keys.length == 0) return new String[0];
        
        try {
            List<String> list = new ArrayList();
            list.add(java.getAbsolutePath());
            list.add("-cp"); // NOI18N
            list.add(getCPJar());
            list.add(JavaInfo.class.getName());
            list.addAll(Arrays.asList(keys));

            Process p = Runtime.getRuntime().exec(list.toArray(new String[list.size()]));
            
            list.clear();
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                list.add(line);
                line = br.readLine();
            }
            br.close();
            
            return list.toArray(new String[list.size()]);
        } catch (Throwable t) {
            System.err.println("Error getting system properties from " + java.toString() + ": " + t.getMessage()); // NOI18N
            t.printStackTrace(System.err);
            return null;
        }
    }
    
    
    private static String CP_JAR;
    private static synchronized String getCPJar() throws MalformedURLException, URISyntaxException {
        if (CP_JAR == null) {
            String name = JavaInfo.class.getSimpleName() + ".class"; // NOI18N
            URL url = JavaInfo.class.getResource(name);
            String path = url.getFile();
            String jar = path.substring(0, path.indexOf("!/")); // NOI18N
            URI uri = new URL(jar).toURI();
            CP_JAR = new File(uri).getAbsolutePath();
        }
        return CP_JAR;
    }
    
    private JavaInfo() {}
    
}
