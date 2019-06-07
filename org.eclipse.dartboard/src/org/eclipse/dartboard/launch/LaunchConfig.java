/*******************************************************************************
 * Copyright (c) 2019 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jonas Hungershausen - initial API and implementation
 *******************************************************************************/
package org.eclipse.dartboard.launch;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.launch.console.DartConsoleFactory;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchConfig extends LaunchConfigurationDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(LaunchConfig.class);

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		if (!mode.equalsIgnoreCase("run")) { //$NON-NLS-1$
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(null, Messages.Launch_DebugNotSupported_Title,
						Messages.Launch_DebugNotSupported_Body);
			});
		}

		String mainClass = configuration.getAttribute(Constants.LAUNCH_MAIN_CLASS, "main.dart"); //$NON-NLS-1$
		String sdk = configuration.getAttribute(Constants.PREFERENCES_SDK_LOCATION, ""); //$NON-NLS-1$
		String projectName = configuration.getAttribute(Constants.LAUNCH_SELECTED_PROJECT, ""); //$NON-NLS-1$
		IProject project = getProject(projectName);
		if (!project.exists()) {
			MessageDialog.openError(null, Messages.Launch_NoProjectSelected_Title, Messages.Launch_NoProjectSelected_Body);
			return;
		}

		String location = project.getLocation().toOSString();
		ProcessBuilder processBuilder = new ProcessBuilder(sdk + "/bin/dart", location + "/" + mainClass); //$NON-NLS-1$ //$NON-NLS-2$

		try {
			Process process = processBuilder.start();
			new DartConsoleFactory(process.getInputStream()).openConsole();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}

	}

	public IProject getProject(String name) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		return root.getProject(name);
	}

}
