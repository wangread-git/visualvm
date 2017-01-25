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

package com.sun.tools.visualvm.modules.tracer.impl.options;

import com.sun.tools.visualvm.core.options.GlobalPreferences;
import java.util.prefs.Preferences;
import org.netbeans.lib.profiler.charts.swing.Utils;
import org.openide.util.NbPreferences;

/**
 *
 * @author Jiri Sedlacek
 */
public final class TracerOptions {

    private static final String PROP_PROBES_APP = "TracerOptions.probesApp"; // NOI18N
    public static final int KEY_PROBES_FIRSTEXP = 0;
    public static final int KEY_PROBES_ALLEXP = 1;
    public static final int KEY_PROBES_ALLCOLL = 2;
    private static final int PROBES_APP_DEFAULT = KEY_PROBES_FIRSTEXP;

    private static final String PROP_REFRESH_RATE = "TracerOptions.refreshRate"; // NOI18N
    private static final String PROP_REFRESH_CUSTOMIZABLE = "TracerOptions.refreshCustomizable"; // NOI18N

    private static final String PROP_SHOW_VALUES = "TracerOptions.showValues"; // NOI18N
    private static final String PROP_SHOW_LEGEND = "TracerOptions.showLegend"; // NOI18N
    private static final String PROP_ROWS_DECORATION = "TracerOptions.rowsDecoration"; // NOI18N
    private static final String PROP_ROWS_SELECTION = "TracerOptions.rowsSelection"; // NOI18N

    private static final String PROP_INITIALLY_OPEN = "TracerOptions.initiallyOpen"; // NOI18N
    private static final String PROP_PROBE_ADDED = "TracerOptions.probeAdded"; // NOI18N
    private static final String PROP_PROBE_ADDED2 = "TracerOptions.probeAdded2"; // NOI18N
    private static final String PROP_SESSION_STARTED = "TracerOptions.sessionStarted"; // NOI18N
    private static final String PROP_ROW_SELECTED = "TracerOptions.rowSelected"; // NOI18N
    private static final String PROP_ROW_SELECTED2 = "TracerOptions.rowSelected2"; // NOI18N
    public static final String VIEWS_UNCHANGED = ""; // NOI18N
    public static final String VIEW_PROBES = "KEY_probes"; // NOI18N
    public static final String VIEW_TIMELINE = "KEY_timeline"; // NOI18N
    public static final String VIEW_SETTINGS = "KEY_settings"; // NOI18N
    public static final String VIEW_DETAILS = "KEY_details"; // NOI18N
    static final String INITIALLY_OPEN_DEFAULT = VIEW_PROBES;
    static final String PROBE_ADDED_DEFAULT = INITIALLY_OPEN_DEFAULT + "," + VIEW_TIMELINE; // NOI18N
    static final String PROBE_ADDED_DEFAULT2 = INITIALLY_OPEN_DEFAULT;
    static final String SESSION_STARTED_DEFAULT = VIEW_TIMELINE;
    static final String ROW_SELECTED_DEFAULT = SESSION_STARTED_DEFAULT + "," + VIEW_DETAILS; // NOI18N
    static final String ROW_SELECTED_DEFAULT2 = SESSION_STARTED_DEFAULT;

    private static final String PROP_CLEAR_SELECTION = "TracerOptions.clearSelection"; // NOI18N

    private static final String PROP_ZOOM_MODE = "TracerOptions.zoomMode"; // NOI18N
    private static final String KEY_FIXED_SCALE = "KEY_fixedScale"; // NOI18N
    private static final String KEY_SCALE_TO_FIT = "KEY_scaleToFit"; // NOI18N
    public static final String FIXED_SCALE = "fixed scale";
    public static final String SCALE_TO_FIT = "scale to fit";

    private static final String PROP_MOUSE_WHEEL_ACTION = "TracerOptions.mouseWheelAction"; // NOI18N
    private static final String KEY_MOUSE_WHEEL_ZOOMS = "KEY_mouseWheelZooms"; // NOI18N
    private static final String KEY_MOUSE_WHEEL_HSCROLLS = "KEY_mouseWheelHScrolls"; // NOI18N
    private static final String KEY_MOUSE_WHEEL_VSCROLLS = "KEY_mouseWheelVScrolls"; // NOI18N
    public static final String MOUSE_WHEEL_ZOOMS = "zoom";
    public static final String MOUSE_WHEEL_HSCROLLS = "horizontal scroll";
    public static final String MOUSE_WHEEL_VSCROLLS = "vertical scroll";

    private static final String PROP_TIMELINE_TOOLBAR = "TracerOptions.timelineToolbar"; // NOI18N
    private static final String PROP_SELECTION_TOOLBAR = "TracerOptions.selectionToolbar"; // NOI18N
    private static final String PROP_EXTRA_TOOLBAR = "TracerOptions.extraToolbar"; // NOI18N
    public static final int SHOW_ALWAYS = 0;
    public static final int SHOW_AS_NEEDED = 1;
    public static final int SHOW_NEVER = 2;
    private static final int TIMELINE_TOOLBAR_DEFAULT = SHOW_AS_NEEDED;
    private static final int SELECTION_TOOLBAR_DEFAULT = SHOW_AS_NEEDED;
    private static final int EXTRA_TOOLBAR_DEFAULT = SHOW_AS_NEEDED;



    private static TracerOptions INSTANCE;

    private final Preferences prefs;


    public static synchronized TracerOptions getInstance() {
        if (INSTANCE == null) INSTANCE = new TracerOptions();
        return INSTANCE;
    }


    void setProbesApp(int probesApp) {
        prefs.putInt(PROP_PROBES_APP, probesApp);
    }

    public int getProbesApp() {
        return prefs.getInt(PROP_PROBES_APP, PROBES_APP_DEFAULT);
    }

    void setRefresh(int refreshRate) {
        prefs.putInt(PROP_REFRESH_RATE, refreshRate);
    }

    int getRefresh() {
        return prefs.getInt(PROP_REFRESH_RATE, -1);
    }

    public int getRefreshRate() {
        int refresh = getRefresh();
        return refresh != -1 ? refresh :
               GlobalPreferences.sharedInstance().getMonitoredDataPoll() * 1000;
    }

    void setRefreshCustomizable(boolean customizable) {
        prefs.putBoolean(PROP_REFRESH_CUSTOMIZABLE, customizable);
    }

    public boolean isRefreshCustomizable() {
        return prefs.getBoolean(PROP_REFRESH_CUSTOMIZABLE, false);
    }

    void setShowValuesEnabled(boolean showValuesEnabled) {
        prefs.putBoolean(PROP_SHOW_VALUES, showValuesEnabled);
    }

    public boolean isShowValuesEnabled() {
        return prefs.getBoolean(PROP_SHOW_VALUES, true);
    }

    void setShowLegendEnabled(boolean showLegendEnabled) {
        prefs.putBoolean(PROP_SHOW_LEGEND, showLegendEnabled);
    }

    public boolean isShowLegendEnabled() {
        return prefs.getBoolean(PROP_SHOW_LEGEND, true); // Default 'false' might be better
    }

    void setRowsDecorationEnabled(boolean rowsDecorationEnabled) {
        if (Utils.forceSpeed()) return;
        prefs.putBoolean(PROP_ROWS_DECORATION, rowsDecorationEnabled);
    }

    public boolean isRowsDecorationEnabled() {
        if (Utils.forceSpeed()) return false;
        return prefs.getBoolean(PROP_ROWS_DECORATION, !Utils.forceSpeed());
    }

    void setRowsSelectionEnabled(boolean rowsSelectionEnabled) {
        prefs.putBoolean(PROP_ROWS_SELECTION, rowsSelectionEnabled);
    }

    public boolean isRowsSelectionEnabled() {
        return prefs.getBoolean(PROP_ROWS_SELECTION, !Utils.forceSpeed());
    }

    void setInitiallyOpened(String opened) {
        prefs.put(PROP_INITIALLY_OPEN, opened);
    }

    public String getInitiallyOpened() {
        return prefs.get(PROP_INITIALLY_OPEN, INITIALLY_OPEN_DEFAULT);
    }

    void setOnProbeAdded(String opened) {
        prefs.put(PROP_PROBE_ADDED, opened);
    }

    public String getOnProbeAdded() {
        return prefs.get(PROP_PROBE_ADDED, PROBE_ADDED_DEFAULT);
    }

    void setOnProbeAdded2(String opened) {
        prefs.put(PROP_PROBE_ADDED2, opened);
    }

    public String getOnProbeAdded2() {
        return prefs.get(PROP_PROBE_ADDED2, PROBE_ADDED_DEFAULT2);
    }

    void setOnSessionStart(String opened) {
        prefs.put(PROP_SESSION_STARTED, opened);
    }

    public String getOnSessionStart() {
        return prefs.get(PROP_SESSION_STARTED, SESSION_STARTED_DEFAULT);
    }

     void setOnRowSelected(String opened) {
        prefs.put(PROP_ROW_SELECTED, opened);
    }

    public String getOnRowSelected() {
        return prefs.get(PROP_ROW_SELECTED, ROW_SELECTED_DEFAULT);
    }

    void setOnRowSelected2(String opened) {
        prefs.put(PROP_ROW_SELECTED2, opened);
    }

    public String getOnRowSelected2() {
        return prefs.get(PROP_ROW_SELECTED2, ROW_SELECTED_DEFAULT2);
    }

    void setZoomMode(String zoomMode) {
        if (SCALE_TO_FIT.equals(zoomMode))
            prefs.put(PROP_ZOOM_MODE, KEY_SCALE_TO_FIT);
        else
            prefs.put(PROP_ZOOM_MODE, KEY_FIXED_SCALE);
    }

    public String getZoomMode() {
        String zoomMode = prefs.get(PROP_ZOOM_MODE, KEY_FIXED_SCALE);
        if (KEY_SCALE_TO_FIT.equals(zoomMode)) return SCALE_TO_FIT;
        return FIXED_SCALE;
    }

    void setMouseWheelAction(String mouseWheelAction) {
        if (MOUSE_WHEEL_HSCROLLS.equals(mouseWheelAction))
            prefs.put(PROP_MOUSE_WHEEL_ACTION, KEY_MOUSE_WHEEL_HSCROLLS);
        else if (MOUSE_WHEEL_VSCROLLS.equals(mouseWheelAction))
            prefs.put(PROP_MOUSE_WHEEL_ACTION, KEY_MOUSE_WHEEL_VSCROLLS);
        else
            prefs.put(PROP_MOUSE_WHEEL_ACTION, KEY_MOUSE_WHEEL_ZOOMS);
    }

    public String getMouseWheelAction() {
        String mouseWheelAction = prefs.get(PROP_MOUSE_WHEEL_ACTION, KEY_MOUSE_WHEEL_ZOOMS);
        if (KEY_MOUSE_WHEEL_HSCROLLS.equals(mouseWheelAction)) return MOUSE_WHEEL_HSCROLLS;
        else if (KEY_MOUSE_WHEEL_VSCROLLS.equals(mouseWheelAction)) return MOUSE_WHEEL_VSCROLLS;
        return MOUSE_WHEEL_ZOOMS;
    }

    void setTimelineToolbar(int visible) {
        prefs.putInt(PROP_TIMELINE_TOOLBAR, visible);
    }

    public int getTimelineToolbar() {
        return prefs.getInt(PROP_TIMELINE_TOOLBAR, TIMELINE_TOOLBAR_DEFAULT);
    }

    void setSelectionToolbar(int visible) {
        prefs.putInt(PROP_SELECTION_TOOLBAR, visible);
    }

    public int getSelectionToolbar() {
        return prefs.getInt(PROP_SELECTION_TOOLBAR, SELECTION_TOOLBAR_DEFAULT);
    }

    void setExtraToolbar(int visible) {
        prefs.putInt(PROP_EXTRA_TOOLBAR, visible);
    }

    public int getExtraToolbar() {
        return prefs.getInt(PROP_EXTRA_TOOLBAR, EXTRA_TOOLBAR_DEFAULT);
    }

    void setClearSelection(boolean clear) {
        prefs.putBoolean(PROP_CLEAR_SELECTION, clear);
    }

    public boolean isClearSelection() {
        return prefs.getBoolean(PROP_CLEAR_SELECTION, true);
    }


    private TracerOptions() {
        prefs = NbPreferences.forModule(TracerOptions.class);
    }

}
