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
 *     Jonas Hungershausen
 *******************************************************************************/
package org.eclipse.dartboard.flutter.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dartboard.flutter.sdk.FlutterSDK;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class FlutterLaunchConfig extends LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		if (!"run".equalsIgnoreCase(mode)) {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(null, "Debug not suported", "Debugging Flutter apps is not supported yet.");
			});
			return;
		}

		String target = configuration.getAttribute("flutter.targetFile", "main.dart");
		String projectName = configuration.getAttribute("selected_project", "");
		IProject project = getProject(projectName);
		if (!project.exists()) {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(null, "Project not found", "The selected project was not found.");
			});
			return;
		}

		FlutterSDK flutterSdk = FlutterSDK.forProject(project);
		flutterSdk.run(target);

	}

	public IProject getProject(String name) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		return root.getProject(name);
	}
}
