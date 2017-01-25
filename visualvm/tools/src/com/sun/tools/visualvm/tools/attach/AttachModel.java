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

package com.sun.tools.visualvm.tools.attach;

import com.sun.tools.visualvm.application.jvm.HeapHistogram;
import com.sun.tools.visualvm.core.model.Model;
import java.util.Properties;

/**
 * This class uses <a href=http://download.oracle.com/javase/6/docs/technotes/guides/attach/index.html>Attach API</a> 
 * to obtain various information from JVM. Note that
 * Attach API is available in JDK 6 and up and only for local processes running as the
 * same user. See Attach API documentation for mode details.
 * 
 * @author Tomas Hurka
 */
public abstract class AttachModel extends Model {

    /**
     * Returns the current system properties in the target Application.
     * 
     * <p> This method returns the system properties in the target virtual
     * machine. Properties whose key or value is not a <tt>String</tt> are 
     * omitted. The method is approximately equivalent to the invocation of the
     * method {@link java.lang.System#getProperties System.getProperties}
     * in the target virtual machine except that properties with a key or
     * value that is not a <tt>String</tt> are not included.
     * @return The system properties of target Application
     * @see java.lang.System#getProperties
     */
    public abstract Properties getSystemProperties();
    
    /**
     * Takes heap dump of target Application.
     * The heap is written to the <tt>fileName</tt> file in the same
     * format as the hprof heap dump.
     * @return returns <CODE>true</CODE> if operation was successful.
     * @param fileName {@link String} where heap dump will be stored.
     */
    public abstract boolean takeHeapDump(String fileName);
    
    /**
     * Takes thread dump of target Application.
     * @return Returns {@link String} of the thread dump from target Application.
     */
    public abstract String takeThreadDump();
    
    /**
     * Takes heap histogram of target Application.
     * @since VisualVM 1.2
     * @return Returns {@link HeapHistogram} of the heap from target Application.
     */
    public abstract HeapHistogram takeHeapHistogram();
    
    /**
     * print VM option.
     * Note that VM option is the one which starts with
     * <CODE>-XX:</CODE>
     * @param name name of VM option. For example <CODE>HeapDumpOnOutOfMemoryError</CODE>
     * @return Full text of VM option. For example <CODE>-XX:+HeapDumpOnOutOfMemoryError</CODE>
     */
    public abstract String printFlag(String name);
    
    /**
     * Sets a VM option of the given name to the specified value. 
     *
     * @param name Name of a VM option 
     * @param value New value of the VM option to be set 
     */
    public abstract void setFlag(String name,String value);
    
}
