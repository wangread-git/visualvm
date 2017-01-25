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

package org.visualvm.samples.filtersortsample;

import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.datasource.descriptor.DataSourceDescriptor;
import com.sun.tools.visualvm.core.datasource.descriptor.DataSourceDescriptorFactory;
import com.sun.tools.visualvm.core.datasupport.Positionable;
import com.sun.tools.visualvm.core.model.AbstractModelProvider;
import java.awt.Image;
import org.openide.util.Utilities;

/**
 * 
 * @author Jiri Sedlacek
 */
public class FilterSortRootNode extends DataSource {
    
    private static FilterSortRootNode sharedInstance;
    private static FilterSortRootDescriptorProvider descriptorProvider;
    
    
    public static synchronized FilterSortRootNode sharedInstance() {
        if (sharedInstance == null) sharedInstance = new FilterSortRootNode();
        return sharedInstance;
    }
    
    
    private FilterSortRootNode() {}
    
    
    static synchronized void initialize() {
        if (descriptorProvider != null) return;
        descriptorProvider = new FilterSortRootDescriptorProvider();
        DataSourceDescriptorFactory.getDefault().registerProvider(descriptorProvider);
    }
    
    static synchronized void uninitialize() {
        if (descriptorProvider == null) return;
        DataSourceDescriptorFactory.getDefault().unregisterProvider(descriptorProvider);
        descriptorProvider = null;
    }
    
    
    private static class FilterSortRootDescriptorProvider extends AbstractModelProvider<DataSourceDescriptor, DataSource> {
    
        public DataSourceDescriptor createModelFor(DataSource ds) {
            if (FilterSortRootNode.sharedInstance().equals(ds)) return new FilterSortRootDescriptor();
            else return null;
        }

        private static class FilterSortRootDescriptor extends DataSourceDescriptor {
            private static final Image NODE_ICON = Utilities.loadImage("com/sun/tools/visualvm/core/ui/resources/idle-icon.png", true);  // NOI18N

            FilterSortRootDescriptor() {
                super(FilterSortRootNode.sharedInstance(), "FilterSortDemo", null, NODE_ICON, Positionable.POSITION_LAST, EXPAND_ON_EACH_NEW_CHILD); // NOI18N
            }

        }

    }
    
}
