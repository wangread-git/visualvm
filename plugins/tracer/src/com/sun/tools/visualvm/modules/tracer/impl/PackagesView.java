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


package com.sun.tools.visualvm.modules.tracer.impl;

import com.sun.tools.visualvm.modules.tracer.impl.probes.ProbeDescriptorComponent;
import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.datasupport.Positionable;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import com.sun.tools.visualvm.core.ui.components.ScrollableContainer;
import com.sun.tools.visualvm.modules.tracer.TracerPackage;
import com.sun.tools.visualvm.modules.tracer.TracerProbeDescriptor;
import com.sun.tools.visualvm.modules.tracer.impl.options.TracerOptions;
import com.sun.tools.visualvm.modules.tracer.impl.swing.CategoryList;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jiri Sedlacek
 */
final class PackagesView {

    private final TracerModel model;
    private final TracerController controller;


    PackagesView(TracerModel model, TracerController controller) {
        this.model = model;
        this.controller = controller;
    }


    // --- Implementation ------------------------------------------------------

    private void loadPackages(final JComponent view) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                final List<TracerPackage> packages = model.getPackages();
                if (packages != null) {
                    final List<List<TracerProbeDescriptor>> descriptors = new ArrayList();
                    for (TracerPackage p : packages) {
                        TracerProbeDescriptor[] da = p.getProbeDescriptors();
                        Arrays.sort(da, Positionable.COMPARATOR);
                        List<TracerProbeDescriptor> dl = Arrays.asList(da);
                        descriptors.add(dl);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() { displayPackages(packages, descriptors, view); }
                    });
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() { displayError(view); }
                    });
                }
            }
        });
    }


    // --- UI implementation ---------------------------------------------------

    DataViewComponent.DetailsView getView() {
        JComponent view = createComponents();
        initListeners(view);
        refreshState(view);
        return new DataViewComponent.DetailsView("Probes", null, 10, new ScrollableContainer(view), null);
    }

    private void refreshState(JComponent view) {
        switch (controller.getState()) {
            case TracerController.STATE_SESSION_INACTIVE:
                view.setEnabled(true);
                break;
            case TracerController.STATE_SESSION_RUNNING:
            case TracerController.STATE_SESSION_IMPOSSIBLE:
            case TracerController.STATE_SESSION_STARTING:
            case TracerController.STATE_SESSION_STOPPING:
                view.setEnabled(false);
                break;
        }
    }

    private void initListeners(final JComponent view) {
        view.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (view.isShowing()) {
                        view.removeHierarchyListener(this);
                        loadPackages(view);
                    }
                }
            }
        });
        controller.addListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                refreshState(view);
            }
        });
    }

    private void displayPackages(List<TracerPackage> packages,
                                 List<List<TracerProbeDescriptor>> descriptors,
                                 JComponent view) {

        int packagesCount = descriptors.size();

        String[] categories = new String[packagesCount];
        String[] tooltips = new String[packagesCount];
        boolean[] initialStates = new boolean[packagesCount];
        Component[][] items = new Component[packagesCount][];

        ProbeDescriptorComponent.SelectionHandler selectionHandler = new
                ProbeDescriptorComponent.SelectionHandler() {
            public void descriptorSelected(TracerPackage<DataSource> p, TracerProbeDescriptor d) {
                model.addDescriptor(p, d);
            }
            public void descriptorUnselected(TracerPackage<DataSource> p, TracerProbeDescriptor d) {
                model.removeDescriptor(p, d);
            }
        };

        int probesApp = TracerOptions.getInstance().getProbesApp();
        Boolean expanded = null;
        if (probesApp == TracerOptions.KEY_PROBES_ALLEXP) expanded = true;
        else if (probesApp == TracerOptions.KEY_PROBES_ALLCOLL) expanded = false;

        for (int i = 0; i < packagesCount; i++) {
            TracerPackage p = packages.get(i);
            categories[i] = new String(p.getName());
            tooltips[i] = new String(p.getDescription());
            initialStates[i] = expanded == null || expanded;
            if (expanded == null) expanded = false;

            List<TracerProbeDescriptor> d = descriptors.get(i);
            int descriptorsCount = d.size();

            items[i] = new Component[descriptorsCount];
            for (int j = 0; j < descriptorsCount; j++)
                items[i][j] = new ProbeDescriptorComponent(d.get(j), p,
                                                           selectionHandler);
        }

        view.removeAll();
        view.add(new CategoryList(categories, tooltips, initialStates, items));
        
        view.revalidate();
        view.repaint();
    }
    
    private void displayError(JComponent view) {
        view.removeAll();
        view.add(new JLabel("Failed to load probes, check the logfile",
                 SwingConstants.CENTER), BorderLayout.CENTER);
        
        view.revalidate();
        view.repaint();
    }

    private JComponent createComponents() {
        JPanel panel = new JPanel(new BorderLayout()) {
            public void setEnabled(boolean enabled) {
                Component[] components = getComponents();
                for (Component c : components) c.setEnabled(enabled);
            }
        };
        panel.setOpaque(false);

        JLabel waitLabel = new JLabel("Loading probes...", SwingConstants.CENTER);
        waitLabel.setEnabled(false);
        panel.add(waitLabel, BorderLayout.CENTER);
        
        return panel;
    }

}
