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

package com.sun.tools.visualvm.charts.xy;

import org.netbeans.lib.profiler.charts.swing.LongRect;
import org.netbeans.lib.profiler.charts.xy.XYItemChange;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItem;

/**
 *
 * @author Jiri Sedlacek
 */
abstract class XYItem extends SynchronousXYItem {

    private int lastIndex;
    private int lastValuesCount;
    
    private final LongRect bounds;
    private long initialMinY;
    private long initialMaxY;

    private long minY;
    private long maxY;


    // --- Constructor ---------------------------------------------------------

    public XYItem(String name) {
        this(name, Long.MAX_VALUE);
    }

    public XYItem(String name, long initialMinY) {
        this(name, initialMinY, Long.MIN_VALUE);
    }

    public XYItem(String name, long initialMinY, long initialMaxY) {
        super(name, initialMinY, initialMaxY);
        this.initialMinY = initialMinY;
        this.initialMaxY = initialMaxY;
        minY = Long.MAX_VALUE;
        maxY = Long.MIN_VALUE;
        bounds = new LongRect();
        lastIndex = -1;
    }


    // --- Item telemetry ------------------------------------------------------

    public XYItemChange valuesChanged() {

        int valuesCount = getValuesCount();
        int index = valuesCount - 1;
        XYItemChange change = null;

        if (index > -1) { // New item(s)

            // Save oldBounds, setup dirtyBounds
            LongRect oldBounds = new LongRect(bounds);
            LongRect dirtyBounds = new LongRect();

            boolean initBounds = lastIndex == -1;
            int dirtyIndex = lastIndex == -1 ? 0 : lastIndex;

            // Process other values
            for (int i = dirtyIndex; i <= index; i++) {

                long timestamp = getXValue(i);
                long value = getYValue(i);

                // Update item minY/maxY
                minY = Math.min(value, minY);
                maxY = Math.max(value, maxY);

                // Process item bounds
                if (initBounds) {
                    // Initialize item bounds
                    bounds.x = timestamp;
                    bounds.y = Math.min(value, initialMinY);
                    bounds.width = 0;
                    bounds.height = Math.max(value, initialMaxY) - bounds.y;
                    initBounds = false;
                } else {
                    // Update item bounds
                    LongRect.add(bounds, timestamp, value);
                    if (valuesCount == lastValuesCount) {
                        bounds.x = getXValue(0);
                        bounds.width = getXValue(valuesCount - 1) - bounds.x;
                    }
                }

                // Process dirty bounds
                if (i == dirtyIndex) {
                    // Setup dirty bounds
                    dirtyBounds.x = timestamp;
                    dirtyBounds.y = value;
                    dirtyBounds.width = getXValue(index) - dirtyBounds.x;
                } else {
                    // Update dirty y/height
                    long dirtyY = dirtyBounds.y;
                    dirtyBounds.y = Math.min(dirtyY, value);
                    dirtyBounds.height = Math.max(dirtyY, value) - dirtyBounds.y;
                }

            }

            // Return ItemChange
            int indexesCount = index - lastIndex;
            int[] indexes = new int[indexesCount];
            for (int i = 0; i < indexesCount; i++) indexes[i] = lastIndex + 1 + i;
            change = new XYItemChange.Default(this, indexes, oldBounds,
                                              new LongRect(bounds), dirtyBounds);

        } else { // Reset

            minY = Long.MAX_VALUE;
            maxY = Long.MIN_VALUE;

            // Save oldBounds
            LongRect oldBounds = new LongRect(bounds);
            LongRect.set(bounds, 0, 0, 0, 0);

            // Return ItemChange
            change = new XYItemChange.Default(this, new int[] { -1 }, oldBounds,
                                            new LongRect(bounds), oldBounds);

        }
        
        lastIndex = index;
        lastValuesCount = valuesCount;
        return change;
        
    }

    public long getMinYValue() { return minY; }

    public long getMaxYValue() { return maxY; }
    
    public LongRect getBounds() {
        if (getValuesCount() > 0) return bounds;
        else return getInitialBounds();
    }

}
