package org.visualvm.demoapplicationtype.datasource;

import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.datasource.descriptor.DataSourceDescriptor;


public abstract class AnagramDataSource extends DataSource {

    public AnagramDataSource() {
        this(null);
    }

    public AnagramDataSource(DataSource master) {
        super(master);
    }

    abstract public DataSourceDescriptor getDescriptor();
}
