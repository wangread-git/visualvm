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
package net.java.visualvm.modules.glassfish.datasource;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.descriptor.DataSourceDescriptor;
import java.awt.Image;


/**
 *
 * @author Jaroslav Bachorik
 */
public class GlassFishModel extends GlassFishDataSource {

    private Application application;
    private final DataSourceDescriptor descriptor = new DataSourceDescriptor(this) {

        @Override
        public Image getIcon() {
            return null;
        }

        @Override
        public String getName() {
            return "Model";
        }

        @Override
        public String getDescription() {
            return "GlassFish/SJSAS Logical Model";
        }

        @Override
        public int getAutoExpansionPolicy() {
            return EXPAND_NEVER;
        }
    };

    public GlassFishModel(Application app) {
        super();
        application = app;
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public DataSourceDescriptor getDescriptor() {
        return descriptor;
    }
}

//            GlassFishWebModule module = new GlassFishWebModule(appName != null ? (moduleName + " (in " + appName + ")") : moduleName, objectName, virtMonitorEntry.getValue(), root);
//            registerDataSource(module);
//            root.getRepository().addDataSource(module);

