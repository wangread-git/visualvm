/*
 * Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.tools.visualvm.application.type;

import com.sun.tools.visualvm.application.jvm.Jvm;
import com.sun.tools.visualvm.application.Application;
import java.util.Properties;

/**
 * Factory which recognizes Eclipse application.
 * @author Tomas Hurka
 * 
 * @since VisualVM 1.3.4
 */
public class EclipseApplicationTypeFactory extends MainClassApplicationTypeFactory {
    
    private static final String MAIN_CLASS = "org.eclipse.equinox.launcher.Main"; // NOI18N
    private static final String ECLIPSE_ID = "-Dosgi.requiredJavaVersion="; // NOI18N
    private static final String ECLIPSE_ID2 = "-XstartOnFirstThread"; // NOI18N
    private static final String ECLIPSE_SYSPROP_ID = "eclipse.buildId"; // NOI18N
    private static final String ECLIPSE_NAME = "Eclipse"; // NOI18N
    
    /**
     * Detects Eclipse application. It returns
     * {@link EclipseApplicationType} for Eclipse application.
     *
     * @return {@link ApplicationType} subclass or <code>null</code> if
     * this application is not Eclipse application
     */
    public ApplicationType createApplicationTypeFor(Application app, Jvm jvm, String mainClass) {
        if (MAIN_CLASS.equals(mainClass)) {
            String name = getName(jvm);
            if (name == null) {
                name = ECLIPSE_NAME;
            }
            return new EclipseApplicationType(app, name);
        }
        if (mainClass == null || mainClass.length() == 0) {    // there is no main class - detect native Windows launcher
            String args = jvm.getJvmArgs();
            if (args != null && (args.contains(ECLIPSE_ID) || args.contains(ECLIPSE_ID2))) {
                String name = getName(jvm);
                if (name != null) {
                    return new EclipseApplicationType(app, name);
                }
            }
        }
        return null;
    }
    
    private String getName(Jvm jvm) {
        if (jvm.isGetSystemPropertiesSupported()) {
            Properties p = jvm.getSystemProperties();
            if (p != null && p.containsKey(ECLIPSE_SYSPROP_ID)) {
                return ECLIPSE_NAME;
            }
        }
        return null;
    }
}
