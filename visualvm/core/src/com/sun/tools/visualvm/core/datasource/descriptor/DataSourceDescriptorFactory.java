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

package com.sun.tools.visualvm.core.datasource.descriptor;

import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.model.ModelFactory;
import com.sun.tools.visualvm.core.model.ModelProvider;
import com.sun.tools.visualvm.core.properties.PropertiesSupport;

/**
 * ModelFactory for DataSourceDescriptors.
 *
 * @author Tomas Hurka
 */
public final class DataSourceDescriptorFactory extends ModelFactory<DataSourceDescriptor,DataSource> implements ModelProvider<DataSourceDescriptor,DataSource> {
    
    private static DataSourceDescriptorFactory dsDescFactory;
    
    private DataSourceDescriptorFactory() {
    }
    
    /**
     * Returns the singleton instance of DataSourceDescriptorFactory.
     * 
     * @return singleton instance of DataSourceDescriptorFactory.
     */
    public static synchronized DataSourceDescriptorFactory getDefault() {
        if (dsDescFactory == null) {
            dsDescFactory = new DataSourceDescriptorFactory();
            dsDescFactory.registerProvider(dsDescFactory);
            // Register General properties tab support
            PropertiesSupport.sharedInstance().registerPropertiesProvider(
                new GeneralPropertiesProvider(), DataSource.class);
        }
        return dsDescFactory;
    }
    
    /**
     * Returns DataSourceDescriptor for given DataSource.
     * Use this method to get for example a DataSource name or icon.
     * 
     * @param ds DataSource for which to get the descriptor.
     * @return DataSourceDescriptor for given DataSource.
     */
    public static DataSourceDescriptor getDescriptor(DataSource ds) {
        return getDefault().getModel(ds);
    }
    
    /**
     * Creates DataSourceDescriptor for given DataSource.
     * This method is used by the ModelFactory framework, typically you need
     * to use the DataSourceDescriptor.getDescriptor(DataSource) method to get
     * properties of a DataSource.
     * 
     * @param ds DataSource for which to create the descriptor.
     * @return new DataSourceDescriptor for given DataSource.
     */
    public DataSourceDescriptor createModelFor(DataSource ds) {
        return new DefaultDataSourceDescriptor(ds);
    }
    
    private static class DefaultDataSourceDescriptor extends DataSourceDescriptor {
        
        DefaultDataSourceDescriptor(DataSource ds) {
            super(ds);
        }
                
    }
}
