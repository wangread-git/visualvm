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

package com.sun.tools.visualvm.sampler.cpu;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.application.jvm.JvmFactory;
import com.sun.tools.visualvm.core.datasupport.Stateful;
import com.sun.tools.visualvm.tools.jmx.JmxModel;
import com.sun.tools.visualvm.tools.jmx.JmxModelFactory;
import com.sun.tools.visualvm.tools.jmx.JvmMXBeans;
import com.sun.tools.visualvm.tools.jmx.JvmMXBeansFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Hurka
 */
public final class ThreadInfoProvider {

    private static final Logger LOGGER = Logger.getLogger(ThreadInfoProvider.class.getName());
    
    final private String status;
    private boolean useGetThreadInfo;
    private ThreadMXBean threadBean;
    
    public ThreadInfoProvider(Application app) {
        status = initialize(app);
    }

    public String getStatus() {
        return status;
    }

    public ThreadMXBean getThreadMXBean() {
        return threadBean;
    }
    
    private String initialize(Application application) {
        if (application.getState() != Stateful.STATE_AVAILABLE) {
            return NbBundle.getMessage(ThreadInfoProvider.class, "MSG_unavailable"); // NOI18N
        }
        JmxModel jmxModel = JmxModelFactory.getJmxModelFor(application);
        if (jmxModel == null) {
            return NbBundle.getMessage(ThreadInfoProvider.class, "MSG_unavailable_init_jmx"); // NOI18N
        }
        if (jmxModel.getConnectionState() != JmxModel.ConnectionState.CONNECTED) {
           return NbBundle.getMessage(ThreadInfoProvider.class, "MSG_unavailable_create_jmx"); // NOI18N
        }
        JvmMXBeans mxbeans = JvmMXBeansFactory.getJvmMXBeans(jmxModel);
        if (mxbeans == null) {
            LOGGER.log(Level.INFO, "JvmMXBeansFactory.getJvmMXBeans(jmxModel) returns null for " + application); // NOI18N
            return NbBundle.getMessage(ThreadInfoProvider.class, "MSG_unavailable_threads"); // NOI18N
        }
        threadBean = mxbeans.getThreadMXBean();
        if (threadBean == null) {
            LOGGER.log(Level.INFO, "mxbeans.getThreadMXBean() returns null for " + application); // NOI18N
            return NbBundle.getMessage(ThreadInfoProvider.class, "MSG_unavailable_threads"); // NOI18N
        }
        useGetThreadInfo = JvmFactory.getJVMFor(application).is15();
        try {
            dumpAllThreads();
        } catch (SecurityException e) {
            LOGGER.log(Level.INFO, "threadBean.getThreadInfo(ids, maxDepth) throws SecurityException for " + application, e); // NOI18N
            return NbBundle.getMessage(ThreadInfoProvider.class, "MSG_unavailable_threads"); // NOI18N
        } catch (Throwable t) {
            LOGGER.log(Level.INFO, "threadBean.getThreadInfo(ids, maxDepth) throws Throwable for " + application, t); // NOI18N
            return NbBundle.getMessage(ThreadInfoProvider.class, "MSG_unavailable_threads"); // NOI18N
        }
        return null;
    }

    ThreadInfo[] dumpAllThreads() {
        if (useGetThreadInfo) {
            return threadBean.getThreadInfo(threadBean.getAllThreadIds(), Integer.MAX_VALUE);
        }
        return threadBean.dumpAllThreads(false,false);
    }

}
