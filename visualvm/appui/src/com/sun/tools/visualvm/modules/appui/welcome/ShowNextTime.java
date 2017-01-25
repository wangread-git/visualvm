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

package com.sun.tools.visualvm.modules.appui.welcome;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author S. Aubrecht
 */
class ShowNextTime extends JPanel 
        implements ActionListener, Constants, PropertyChangeListener {

    private JCheckBox button;

    /** Creates a new instance of RecentProjects */
    public ShowNextTime() {
        super( new BorderLayout() );

        setOpaque( false );
        
        button = new JCheckBox( BundleSupport.getLabel( "ShowOnStartup" ) ); // NOI18N
        button.setSelected( WelcomeOptions.getDefault().isShowOnStartup() );
        button.setOpaque( false );
        BundleSupport.setAccessibilityProperties( button, "ShowOnStartup" ); //NOI18N
        add( button, BorderLayout.CENTER );
        button.addActionListener( this );
    }
    
    public void actionPerformed(ActionEvent e) {
        WelcomeOptions.getDefault().setShowOnStartup( button.isSelected() );
    }

    @Override
    public void addNotify() {
        super.addNotify();
        WelcomeOptions.getDefault().addPropertyChangeListener( this );
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        WelcomeOptions.getDefault().removePropertyChangeListener( this );
    }

    public void propertyChange(PropertyChangeEvent evt) {
        button.setSelected( WelcomeOptions.getDefault().isShowOnStartup() );
    }
    
    
}
