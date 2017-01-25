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
package org.eclipse.visualvm.launcher.pde;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.pde.launching.EclipseApplicationLaunchConfiguration;
import org.eclipse.visualvm.launcher.api.VisualVMHelper;

public class VisualVMPDEDelegate extends EclipseApplicationLaunchConfiguration {
	volatile private long usedId = -1;
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		usedId = VisualVMHelper.getNextID();
		super.launch(configuration, mode, launch, monitor);
	}

	@Override
	public String[] getVMArguments(ILaunchConfiguration configuration)
			throws CoreException {
		String[] origArgs = super.getVMArguments(configuration);
		String[] visualvmArgs = VisualVMHelper.getJvmArgs(usedId);
		String[] newArgs = new String[origArgs.length + visualvmArgs.length];
		System.arraycopy(origArgs, 0, newArgs, 0, origArgs.length);
		System.arraycopy(visualvmArgs, 0, newArgs, origArgs.length, visualvmArgs.length);
		return newArgs;
	}

	@Override
	public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		try {
			VisualVMHelper.openInVisualVM(usedId);
			return super.getVMRunner(configuration, mode);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
