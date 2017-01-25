/*
 *  Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Oracle designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Oracle in the LICENSE file that accompanied this code.
 *
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 *
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 *  or visit www.oracle.com if you need additional information or have any
 *  questions.
 */

package com.sun.tools.visualvm.coredump;

import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.datasource.descriptor.DataSourceDescriptor;
import java.awt.Image;
import java.util.Comparator;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * DataSourceDescriptor for VM Coredumps node in Applications window.
 *
 * @author Jiri Sedlacek
 *
 * @since VisualVM 1.3
 */
public final class CoreDumpsContainerDescriptor extends DataSourceDescriptor<CoreDumpsContainer> {

    private static final Image NODE_ICON = ImageUtilities.loadImage(
                "com/sun/tools/visualvm/coredump/resources/coredumps.png", true);  // NOI18N

    CoreDumpsContainerDescriptor() {
        super(CoreDumpsContainer.sharedInstance(), NbBundle.getMessage(
              CoreDumpsContainerDescriptor.class, "LBL_VM_Coredumps"), null, // NOI18N
              NODE_ICON, 20, EXPAND_ON_EACH_NEW_CHILD);
        
        // Initialize sorting
        setChildrenComparator(CoreDumpsSorting.instance().getInitialSorting());
    }

    /**
     * Sets a custom comparator for sorting DataSources within the CoreDumpsContainer.
     * Use setChildrenComparator(null) to restore the default sorting.
     *
     * @param newComparator comparator for sorting DataSources within the CoreDumpsContainer
     */
    public void setChildrenComparator(Comparator<DataSource> newComparator) {
        super.setChildrenComparator(newComparator);
    }

}
