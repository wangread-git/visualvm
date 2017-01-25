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

import com.sun.tools.visualvm.core.properties.PropertiesPanel;
import com.sun.tools.visualvm.core.properties.PropertiesProvider;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.openide.util.NbBundle;

/**
 *
 * @author Jiri Sedlacek
 */
class GeneralPropertiesProvider extends PropertiesProvider<ApplicationSupport.CurrentApplication> {

    public GeneralPropertiesProvider() {
        super(NbBundle.getMessage(GeneralPropertiesProvider.class, "LBL_ConnectionProperties"), // NOI18N
              NbBundle.getMessage(GeneralPropertiesProvider.class, "DESCR_ConnectionProperties"), // NOI18N
              CATEGORY_GENERAL, 50);
    }


    public PropertiesPanel createPanel(ApplicationSupport.CurrentApplication dataSource) {
        PropertiesPanel panel = new PropertiesPanel();
        panel.setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea(NbBundle.getMessage(
                GeneralPropertiesProvider.class, "MSG_ConnectionProperties")) { // NOI18N
            public Dimension getMinimumSize() {
                Dimension prefSize = getPreferredSize();
                Dimension minSize = super.getMinimumSize();
                prefSize.width = 0;
                if (minSize.height < prefSize.height) return prefSize;
                else return minSize;
            }
        };
        textArea.setBorder(BorderFactory.createEmptyBorder());
        textArea.setOpaque(false);
        // Nimbus LaF doesn't respect setOpaque(false), this is a workaround.
        // May cause delays for remote X sessions due to color transparency.
        if (UIManager.getLookAndFeel().getID().equals("Nimbus")) // NOI18N
            textArea.setBackground(new Color(0, 0, 0, 0));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);
        textArea.setMinimumSize(new Dimension(1, 1));
        panel.add(textArea, BorderLayout.CENTER);

        return panel;
    }


    public boolean supportsDataSource(ApplicationSupport.CurrentApplication dataSource) {
        return true;
    }

    public void propertiesDefined(PropertiesPanel panel, ApplicationSupport.CurrentApplication dataSource) {}

    public void propertiesChanged(PropertiesPanel panel, ApplicationSupport.CurrentApplication dataSource) {}

    public void propertiesCancelled(PropertiesPanel panel, ApplicationSupport.CurrentApplication dataSource) {}

}
