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

package com.sun.tools.visualvm.modules.customtype;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.application.jvm.Jvm;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jaroslav Bachorik
 */
public class ApplicationTypeFactory extends com.sun.tools.visualvm.application.type.MainClassApplicationTypeFactory {
    final private Map<String, ApplicationType> typeMap = new HashMap<String, ApplicationType>();

    final private static class Singleton {
        final private static ApplicationTypeFactory INSTANCE = new ApplicationTypeFactory();
    }

    final public static ApplicationTypeFactory getDefault() {
        return Singleton.INSTANCE;
    }

    private ApplicationTypeManager manager;

    private ApplicationTypeFactory() {
        manager = ApplicationTypeManager.getDefault();
    }

    final public static void initialize() {
        com.sun.tools.visualvm.application.type.ApplicationTypeFactory.getDefault().registerProvider(getDefault());
    }
    
    final public static void shutdown() {
        com.sun.tools.visualvm.application.type.ApplicationTypeFactory.getDefault().unregisterProvider(getDefault());
    }

    @Override
    public ApplicationType createApplicationTypeFor(Application app, Jvm jvm, String mainClass) {
        synchronized(typeMap) {
            ApplicationType type = typeMap.get(mainClass);
            if (type == null) {
                type = findType(mainClass);
                if (type != null) {
                    typeMap.put(mainClass, type);
                }
            }
            return type;
        }
    }

    private ApplicationType findType(String mainClass) {
        ApplicationType type = manager.findType(mainClass);

        if (type != null) {
            type.loadIcon();
            synchronized(typeMap) {
                typeMap.put(mainClass, type);
            }
        }

        return type;
    }
}
