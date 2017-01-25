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

package com.sun.tools.visualvm.modules.threadinspect;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.application.views.ApplicationViewsSupport;
import com.sun.tools.visualvm.core.ui.DataSourceViewPlugin;
import com.sun.tools.visualvm.core.ui.DataSourceViewPluginProvider;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent.DetailsView;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author Jiri Sedlacek
 */
final class Installer extends ModuleInstall {

    public void restored() {
        ApplicationViewsSupport.sharedInstance().getThreadsView().
                registerPluginProvider(new ThreadsViewPluginProvider());
    }


    private static class ThreadsViewPluginProvider extends DataSourceViewPluginProvider<Application> {

        protected boolean supportsPluginFor(Application application) {
            return true;
        }

        protected DataSourceViewPlugin createPlugin(Application application) {
            return new ThreadInspectorViewPlugin(application);
        }

    }

    private static class ThreadInspectorViewPlugin extends DataSourceViewPlugin {

        public DetailsView createView(int location) {
            switch (location) {
                case DataViewComponent.BOTTOM_LEFT:
                    return new DataViewComponent.DetailsView("Threads inspector", null,
                                                 10, new ThreadsInspector((Application)
                                                 getDataSource()), null);
                default: return null;
            }
        }


        ThreadInspectorViewPlugin(Application application) {
            super(application);
        }

    }

}
