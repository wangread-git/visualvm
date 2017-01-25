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

package com.sun.tools.visualvm.application.type;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.model.AbstractModelProvider;
import com.sun.tools.visualvm.application.jvm.Jvm;
import com.sun.tools.visualvm.application.jvm.JvmFactory;

/**
 * Factory which recognizes Java Web Start Application
 * @author Luis-Miguel Alventosa
 */
public class JavaWebStartApplicationTypeFactory
        extends AbstractModelProvider<ApplicationType, Application> {

    private static final String JWS = "-Djnlpx.home=";  // NOI18N

    /**
     * Detects Java Web Start application.
     * @return {@link JavaWebStartApplicationType} instance or <code>null</code>
     * if application is not Java Web Start application
     * @param application Application
     */
    @Override
    public ApplicationType createModelFor(Application application) {
        Jvm jvm = JvmFactory.getJVMFor(application);
        if (jvm.isBasicInfoSupported()) {
            String args = jvm.getJvmArgs();
            int jws_index = args.indexOf(JWS);
            if (jws_index != -1) {
                return new JavaWebStartApplicationType("");
            }
        }
        return null;
    }
}
