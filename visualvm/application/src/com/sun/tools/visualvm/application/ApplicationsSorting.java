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

package com.sun.tools.visualvm.application;

import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.datasource.DataSourceRepository;
import com.sun.tools.visualvm.core.datasource.descriptor.DataSourceDescriptor;
import com.sun.tools.visualvm.core.datasource.descriptor.DataSourceDescriptorFactory;
import com.sun.tools.visualvm.host.Host;
import com.sun.tools.visualvm.host.LocalHostDescriptor;
import com.sun.tools.visualvm.host.RemoteHostDescriptor;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Jiri Sedlacek
 */
final class ApplicationsSorting implements Presenter.Menu {

    private static final String PROP_APPS_SORTING = "Applications.sorting"; // NOI18N

    private static ApplicationsSorting instance;
    
    private static final Comparator<DataSource> BY_TIME_COMPARATOR = byTimeComparator();
    private static final Comparator<DataSource> BY_NAME_COMPARATOR = byNameComparator();
    private static final Comparator<DataSource> BY_PID_COMPARATOR = byPidComparator();
    private static final List<Comparator<DataSource>> COMPARATORS = new ArrayList();
    static { COMPARATORS.add(BY_TIME_COMPARATOR); COMPARATORS.add(BY_NAME_COMPARATOR);
             COMPARATORS.add(BY_PID_COMPARATOR); }

    private final Preferences prefs;

    private JMenuItem presenter;
    private final Sorter sorter;


    public static synchronized ApplicationsSorting instance() {
        if (instance == null) instance = new ApplicationsSorting();
        return instance;
    }

    public JMenuItem getMenuPresenter() {
        if (presenter == null) presenter = createPresenter();
        return presenter;
    }


    Comparator<DataSource> getInitialSorting() {
        return COMPARATORS.get(prefs.getInt(PROP_APPS_SORTING, COMPARATORS.
                                            indexOf(BY_TIME_COMPARATOR)));
    }


    private JMenuItem createPresenter() {
        final JMenu menu = new JMenu() {
            protected void fireMenuSelected() {
                Component[] items = getMenuComponents();
                for (Component item : items)
                    if (item instanceof SortAction)
                        ((SortAction)item).updateAction();
            }
        };
        Mnemonics.setLocalizedText(menu, NbBundle.getMessage(ApplicationsSorting.class,
                                   "ACT_SortApps")); // NOI18N

        menu.add(new SortAction(NbBundle.getMessage(ApplicationsSorting.class,
                                "ACT_TimeAdded"), BY_TIME_COMPARATOR, sorter)); // NOI18N
        menu.add(new SortAction(NbBundle.getMessage(ApplicationsSorting.class,
                                "ACT_DisplayName"), BY_NAME_COMPARATOR, sorter)); // NOI18N
        menu.add(new SortAction(NbBundle.getMessage(ApplicationsSorting.class,
                                "ACT_Pid"), BY_PID_COMPARATOR, sorter)); // NOI18N

        return menu;
    }
    
    private static Comparator<DataSource> byTimeComparator() {
        return null;
    }

    private static Comparator<DataSource> byNameComparator() {
        return new Comparator<DataSource>() {
            public int compare(DataSource d1, DataSource d2) {
                DataSourceDescriptor dd1 = DataSourceDescriptorFactory.getDescriptor(d1);
                DataSourceDescriptor dd2 = DataSourceDescriptorFactory.getDescriptor(d2);

                return dd1.getName().compareTo(dd2.getName());
            }
        };
    }

    private static Comparator<DataSource> byPidComparator() {
        return new Comparator<DataSource>() {
            public int compare(DataSource d1, DataSource d2) {
                boolean d1app = d1 instanceof Application;
                boolean d2app = d2 instanceof Application;

                if (!d1app && !d2app) return BY_NAME_COMPARATOR.compare(d1, d2);
                else if (!d1app) return 1;
                else if (!d2app) return -1;

                int d1pid = ((Application)d1).getPid();
                if (d1pid == Application.UNKNOWN_PID) d1pid = Integer.MAX_VALUE;
                int d2pid = ((Application)d2).getPid();
                if (d2pid == Application.UNKNOWN_PID) d2pid = Integer.MAX_VALUE;

                if (d1pid == d2pid) return BY_NAME_COMPARATOR.compare(d1, d2);
                else if (d1pid > d2pid) return 1;
                else return -1;
            }
        };
    }

    private ApplicationsSorting() {
        prefs = NbPreferences.forModule(ApplicationsSorting.class);

        sorter = new Sorter() {
            public void sort(Comparator<DataSource> comparator) {
                // Sort localhost
                DataSourceDescriptor ld = DataSourceDescriptorFactory.getDescriptor(
                                         Host.LOCALHOST);
                if (ld instanceof LocalHostDescriptor) {
                    ((LocalHostDescriptor)ld).setChildrenComparator(comparator);
                    prefs.putInt(PROP_APPS_SORTING, COMPARATORS.indexOf(comparator));
                }

                // Sort remote hosts
                Set<Host> remoteHosts = DataSourceRepository.sharedInstance().
                                        getDataSources(Host.class);
                for (Host host : remoteHosts) {
                    if (host == Host.LOCALHOST) continue;
                    DataSourceDescriptor rd = DataSourceDescriptorFactory.
                                              getDescriptor(host);
                    if (rd instanceof RemoteHostDescriptor)
                        ((RemoteHostDescriptor)rd).setChildrenComparator(comparator);
                }
            }
        };
    }


    private static class SortAction extends JRadioButtonMenuItem {

        private final Sorter sorter;
        private final Comparator<DataSource> comparator;
        private boolean currentlySelected;

        SortAction(String name, Comparator<DataSource> comparator, Sorter sorter) {
            Mnemonics.setLocalizedText(this, name);
            this.comparator = comparator;
            this.sorter = sorter;
        }

        void updateAction() {
            DataSourceDescriptor d = DataSourceDescriptorFactory.getDescriptor(
                                     Host.LOCALHOST);
            setEnabled(d instanceof LocalHostDescriptor);
            currentlySelected = d.getChildrenComparator() == comparator;
            setSelected(currentlySelected);
        }

        protected void fireActionPerformed(ActionEvent e) {
            if (!currentlySelected) sorter.sort(comparator);
        }

    }

    private static interface Sorter {

        void sort(Comparator<DataSource> comparator);

    }

}
