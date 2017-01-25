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
package com.sun.tools.visualvm.core.ui.actions;

import com.sun.tools.visualvm.core.snapshot.Snapshot;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;


/**
 *
 * @author Jiri Sedlacek
 */
class SaveSnapshotAsAction extends SingleDataSourceAction<Snapshot> {
    
    private static final String ICON_PATH = "com/sun/tools/visualvm/core/ui/resources/saveSnapshot.png";    // NOI18N
    private static final Image ICON = ImageUtilities.loadImage(ICON_PATH);
    
    private static SaveSnapshotAsAction instance;
    
    public static synchronized SaveSnapshotAsAction instance() {
        if (instance == null) 
            instance = new SaveSnapshotAsAction();
        return instance;
    }
    
    
    protected void actionPerformed(Snapshot snapshot, ActionEvent actionEvent) {
        snapshot.saveAs();
    }

    protected boolean isEnabled(Snapshot snapshot) {
        return snapshot.supportsSaveAs();
    }
    
    
    private SaveSnapshotAsAction() {
        super(Snapshot.class);
        putValue(NAME, NbBundle.getMessage(SaveSnapshotAsAction.class, "LBL_Save_As")); // NOI18N
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(SaveSnapshotAsAction.class, "LBL_Save_Snapshot_As"));   // NOI18N
        putValue(SMALL_ICON, new ImageIcon(ICON));
        putValue("iconBase", ICON_PATH);    // NOI18N
    }
}
