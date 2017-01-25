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

package com.sun.tools.visualvm.modules.tracer.impl.timeline;

import com.sun.tools.visualvm.modules.tracer.ProbeItemDescriptor;
import com.sun.tools.visualvm.modules.tracer.impl.timeline.items.ValueItemDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.lib.profiler.charts.Timeline;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItem;

/**
 * All methods must be invoked from the EDT.
 *
 * @author Jiri Sedlacek
 */
final class TimelineModel implements Timeline {

    private static final int STORAGE_BUFFER_STEP = 50;

    private int bufferStep;

    private int valuesCount;
    private long[] timestamps;
    private long[][] values;

    private final List<TimelineXYItem> items = new ArrayList();


    TimelineModel() {
        this(STORAGE_BUFFER_STEP);
    }
    
    private TimelineModel(int bufferStep) {
        this.bufferStep = bufferStep;
        reset();
    }


    int getItemsCount() {
        return items.size();
    }


    TimelineXYItem[] createItems(ProbeItemDescriptor[] itemDescriptors) {
        int itemsCount = values == null ? 0 : values.length;
        int addedItemsCount = itemDescriptors.length;
        TimelineXYItem[] itemsArr = new TimelineXYItem[addedItemsCount];

        for (int i = 0; i < addedItemsCount; i++) {
            if (itemDescriptors[i] instanceof ValueItemDescriptor) {
                ValueItemDescriptor d = (ValueItemDescriptor)itemDescriptors[i];
                itemsArr[i] = new TimelineXYItem(d.getName(), d.getMinValue(),
                                                 d.getMaxValue(), itemsCount + i) {
                    public long getYValue(int valueIndex) {
                        return values[getIndex()][valueIndex];
                    }
                };
            } else {
                // Reserved for non-value items
            }
            items.add(itemsArr[i]);
        }

        addItemsImpl(addedItemsCount);

        return itemsArr;
    }

    void removeItems(SynchronousXYItem[] removed) {
        removeItemsImpl(removed.length);

        int firstRemovedIndex = ((TimelineXYItem)removed[0]).getIndex();
        for (SynchronousXYItem item : removed)
            items.remove(firstRemovedIndex);
        for (int i = firstRemovedIndex; i < items.size(); i++)
            items.get(i).setIndex(i);
    }


    void addValues(long timestamp, long[] newValues) {
        updateStorage();

        // Check last timestamp whether greater than the new one
        long lastTimestamp = valuesCount == 0 ? -1 : timestamps[valuesCount - 1];
        // Silently increase timestamp, JVM was busy - timer out of sync
        if (lastTimestamp >= timestamp) timestamp = lastTimestamp + 1;

        timestamps[valuesCount] = timestamp;
        for (int i = 0; i < values.length; i++)
            values[i][valuesCount] = newValues[i];

        valuesCount++;
    }
    

    void reset() {
        valuesCount = 0;

        timestamps = null;
        if (values != null) {
            if (values.length == 0) {
                values = null;
            } else {
                for (int i = 0; i < values.length; i++)
                    values[i] = new long[0];
            }
        }
    }


    private void addItemsImpl(int addedItemsCount) {
        int newItemsCount = (values == null ? 0 : values.length) + addedItemsCount;
        values = new long[newItemsCount][];
        reset();
    }

    private void removeItemsImpl(int removedItemsCount) {
        values = new long[values.length - removedItemsCount][];
        reset();
    }


    public int getTimestampsCount() {
        return valuesCount;
    }

    public long getTimestamp(int index) {
        return timestamps[index];
    }


    private void updateStorage() {
        if (timestamps == null) {
            timestamps = new long[bufferStep];
            for (int i = 0; i < values.length; i++)
                values[i] = new long[bufferStep];
        } else if (valuesCount == timestamps.length) {
            timestamps = extendArray(timestamps, bufferStep);
            for (int i = 0; i < values.length; i++)
                values[i] = extendArray(values[i], bufferStep);
        }
    }

    private static long[] extendArray(long[] array, int extraLength) {
        int originalLength = array.length;
        long[] newArray = new long[originalLength + extraLength];
        System.arraycopy(array, 0, newArray, 0, originalLength);
        return newArray;
    }

}
