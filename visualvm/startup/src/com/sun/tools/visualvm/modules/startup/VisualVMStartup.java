/*
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.tools.visualvm.modules.startup;

import com.sun.tools.visualvm.modules.startup.dialogs.StartupDialog;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.openide.LifecycleManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;

/**
 *
 * @author Jiri Sedlacek
 */
final class VisualVMStartup extends ModuleInstall {
    
    private static final boolean DISABLE_STARTUP_CHECK = Boolean.getBoolean("com.sun.tools.visualvm.modules.startup.DisableStartupCheck"); // NOI18N
    
    private static final ResourceBundle bundle = NbBundle.getBundle(VisualVMStartup.class);
    private static final String ERROR_STARTUP_CAPTION = bundle.getString("VisualVMStartup_ErrorStartupCaption"); // NOI18N
    private static final String INCORRECT_VERSION_MSG = bundle.getString("VisualVMStartup_IncorrectVersionMsg"); // NOI18N
    private static final String JRE_MSG = bundle.getString("VisualVMStartup_JreMsg"); // NOI18N

    private static boolean envChecked = false;
    
    
    public void validate() {
        if (!checkEnv()) LifecycleManager.getDefault().exit();
    }

    static synchronized boolean checkEnv() {
        if (envChecked) return true;
        envChecked = true;
        
        if (DISABLE_STARTUP_CHECK) {
            System.err.println("Starting with com.sun.tools.visualvm.modules.startup.DisableStartupCheck=true"); // NOI18N
        } else {
            if (!isJava7or8or9()) {
                displayError7or8or9();
                return false;
            } else if (!isJDK()) {
                displayErrorJRE();
                return false;
            }
        }

        return true;
    }
    
    private static void displayError7or8or9() {
        Utils.setSystemLaF();
        JDialog d = StartupDialog.create(ERROR_STARTUP_CAPTION, MessageFormat.format(INCORRECT_VERSION_MSG,
                new Object[] { System.getProperty("java.specification.version"), getJavaInfo(), // NOI18N
                getJvmInfo(), System.getProperties().getProperty("java.home", "unknown location") }),
                JOptionPane.ERROR_MESSAGE); // NOI18N
        d.setVisible(true);
    }
    
    private static void displayErrorJRE() {
        Utils.setSystemLaF();
        JDialog d = StartupDialog.create(ERROR_STARTUP_CAPTION, MessageFormat.format(JRE_MSG,
                new Object[] { getJavaInfo(), getJvmInfo(), System.getProperties().getProperty(
                "java.home", "unknown location") }), JOptionPane.ERROR_MESSAGE); // NOI18N
        d.setVisible(true);
    }
    
    private static boolean isJava7or8or9() {
        String javaVersion = System.getProperty("java.specification.version"); // NOI18N
        if (javaVersion == null) return false;
        return javaVersion.startsWith("1.7") || javaVersion.startsWith("1.8")  // NOI18N
            || javaVersion.startsWith("1.9") || javaVersion.startsWith("9"); // NOI18N
    }
    
    private static boolean isJDK() {
        Class vmClass = null;
        try { vmClass = Class.forName("com.sun.tools.attach.VirtualMachine"); } catch (ClassNotFoundException ex) {} // NOI18N
        return vmClass != null;
    }
    
    private static String getJavaInfo() {
        Properties systemProperties = System.getProperties();
        String javaVersion = systemProperties.getProperty("java.version", "unknown version"); // NOI18N
        String vendorInfo = systemProperties.getProperty("java.vendor", "unknown vendor"); // NOI18N
        return javaVersion + "; " + vendorInfo; // NOI18N
    }
    
    private static String getJvmInfo() {
        Properties systemProperties = System.getProperties();
        String vmName = systemProperties.getProperty("java.vm.name", "unknown name"); // NOI18N
        String vmVerison = systemProperties.getProperty("java.vm.version", ""); // NOI18N
        String vmInfo = systemProperties.getProperty("java.vm.info", ""); // NOI18N
        return vmName + " (" + vmVerison + ", " + vmInfo + ")"; // NOI18N
    }
    
}
