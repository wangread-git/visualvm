/*
 * Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.tools.visualvm.modules.saplugin;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.snapshot.Snapshot;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.DataSourceViewsManager;
import com.sun.tools.visualvm.core.ui.DataSourceViewProvider;
import com.sun.tools.visualvm.coredump.CoreDump;
import com.sun.tools.visualvm.host.Host;
import com.sun.tools.visualvm.tools.jvmstat.JvmJvmstatModel;
import com.sun.tools.visualvm.tools.jvmstat.JvmJvmstatModelFactory;
import java.io.File;

/**
 *
 * @author poonam
 */
class SAViewProvider extends DataSourceViewProvider<DataSource> {

    private static DataSourceViewProvider<DataSource> instance =  new SAViewProvider();

    private SAView saview = null;
    
    @Override
    public boolean supportsViewFor(DataSource ds) {
        if (ds == Application.CURRENT_APPLICATION)
            return false;
        
        if (ds instanceof Application) {
            if (Host.LOCALHOST.equals(((Application) ds).getHost())) {
                JvmJvmstatModel jvmstat = JvmJvmstatModelFactory.getJvmstatModelFor((Application) ds);
                if (jvmstat == null) {
                    return false;
                }
                File jdkHome = new File(jvmstat.getJavaHome());
                if ("jre".equals(jdkHome.getName())) {
                    jdkHome = jdkHome.getParentFile();
                }
                File saJar = SAModelProvider.getSaJar(jdkHome);
                if (saJar == null) {
                    return false;
                } else {
                    return true;
                }
            }
        } else if (ds instanceof CoreDump) {
            CoreDump coredump = (CoreDump) ds;
            File executable = new File(coredump.getExecutable());
            File coreFile = coredump.getFile();
            if (executable.exists() && coreFile.exists()) {
                File jdkHome = executable.getParentFile().getParentFile();
                File saJar = SAModelProvider.getSaJar(jdkHome);

                if (saJar == null) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public synchronized DataSourceView createView(final DataSource ds) {
        saview = new SAView(ds);
        return saview;

    }
     
    //@Override
    public boolean supportsSaveViewsFor(DataSource arg0) {
        return false;
    }    

  //  @Override
    public void saveViews(DataSource arg0, Snapshot arg1) {
    }
    
    void initialize() {        
        DataSourceViewsManager.sharedInstance().addViewProvider(instance, DataSource.class);
    }
    
    static void unregister() {
        DataSourceViewsManager.sharedInstance().removeViewProvider(instance);
    }

}
