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

package com.sun.tools.visualvm.host.views;

import com.sun.tools.visualvm.core.ui.DataSourceViewsManager;
import com.sun.tools.visualvm.core.ui.PluggableDataSourceViewProvider;
import com.sun.tools.visualvm.host.Host;
import com.sun.tools.visualvm.host.views.overview.HostOverviewViewProvider;

/**
 * Support for built-in host views in VisualVM.
 * Currently publishes Overview subtab for Host.
 *
 * @author Jiri Sedlacek
 */
public final class HostViewsSupport {
    
    private static HostViewsSupport sharedInstance;
    
    private HostOverviewViewProvider viewProvider = new HostOverviewViewProvider();
    
    
    /**
     * Returns singleton instance of HostViewsSupport.
     * 
     * @return singleton instance of HostViewsSupport.
     */
    public static synchronized HostViewsSupport sharedInstance() {
        if (sharedInstance == null) sharedInstance = new HostViewsSupport();
        return sharedInstance;
    }
    
    /**
     * Returns PluggableDataSourceViewProvider for Overview host subtab.
     * 
     * @return PluggableDataSourceViewProvider for Overview host subtab.
     */
    public PluggableDataSourceViewProvider<Host> getOverviewView() {
        return viewProvider;
    }
    
    
    private HostViewsSupport() {
        DataSourceViewsManager.sharedInstance().addViewProvider(viewProvider, Host.class);
    }
    
}
