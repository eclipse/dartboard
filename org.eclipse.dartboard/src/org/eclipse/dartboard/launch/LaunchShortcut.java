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
 *     Jonas Hungershausen - https://github.com/eclipse/dartboard/issues/1
 *******************************************************************************/
package org.eclipse.dartboard.launch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;

/**
 * A {@link ILaunchShortcut} used to launch a project as a dart program.
 * 
 * This launch shortcut is used to launch a Dart project as a program.
 * 
 * @author Jonas Hungershausen
 *
 */
public class LaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		// Get the active project from the
		IProject selected = null;
		if (selection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IResource) {
				selected = ((IResource) firstElement).getProject();
			}
		}

		// Shouldn't happen
		if (selected == null) {
			MessageDialog.openError(null, Messages.Launch_NoConfigurationFound_Title,
					Messages.Launch_NoConfigurationFound_Body);
			return;
		}

		launchProject(selected, mode);
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IResource resource = editor.getEditorInput().getAdapter(IResource.class);

		if (resource == null) {
			MessageDialog.openError(null, Messages.Launch_NoConfigurationFound_Title,
					Messages.Launch_NoConfigurationFound_Body);
			return;
		}

		IProject project = resource.getProject();

		launchProject(project, mode);
	}

	private void launchProject(IProject project, String mode) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType type = manager.getLaunchConfigurationType(Constants.LAUNCH_CONFIGURATION_ID);

		Set<String> modes = new HashSet<>();
		modes.add(mode);
		try {
			// Find first launch configuration for selected project.
			ILaunchConfiguration launchConfiguration = null;
			for (ILaunchConfiguration conf : manager.getLaunchConfigurations(type)) {
				if (conf.getAttribute(Constants.LAUNCH_SELECTED_PROJECT, "").equalsIgnoreCase(project.getName())) { //$NON-NLS-1$
					launchConfiguration = conf;
				}
			}

			if (launchConfiguration != null) {
				launchConfiguration.launch(mode, null);
				return;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		MessageDialog.openError(null, Messages.Launch_NoConfigurationFound_Title,
				Messages.Launch_NoConfigurationFound_Body);

		// TODO: If we reached this part, without launching the project, no launch
		// config is available.
		// We should open the launch config manager with the prefilled project setting
		// of the config page

		// Open LaunchConfig Manager
		// Precreate a new launch config with the selected project preselected
	}
}
