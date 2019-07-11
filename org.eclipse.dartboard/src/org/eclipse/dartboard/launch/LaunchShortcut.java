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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.PlatformUIUtil;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsMessages;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * A {@link ILaunchShortcut} used to launch a project as a dart program.
 * 
 * This launch shortcut is used to launch a Dart project as a program.
 * 
 * @author Jonas Hungershausen
 *
 */
@SuppressWarnings("restriction")
public class LaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		IProject selected = null;
		if (selection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IAdaptable) {
				selected = ((IAdaptable) firstElement).getAdapter(IProject.class);
			}
		}

		launchProject(selected, mode);
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IProject project = null;
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			project = ((IFileEditorInput) editorInput).getFile().getProject();
		}

		launchProject(project, mode);
	}

	private void launchProject(IProject project, String mode) {
		if (project == null) {
			MessageDialog.openError(null, Messages.Launch_ConfigurationRequired_Title,
					Messages.Launch_ConfigurationRequired_Body);
			return;
		}

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType type = manager.getLaunchConfigurationType(Constants.LAUNCH_CONFIGURATION_ID);

		try {
			// Find first launch configuration for selected project.
			ILaunchConfiguration launchConfiguration = null;
			for (ILaunchConfiguration conf : manager.getLaunchConfigurations(type)) {
				if (conf.getAttribute(Constants.LAUNCH_SELECTED_PROJECT, "").equalsIgnoreCase(project.getName())) { //$NON-NLS-1$
					launchConfiguration = conf;
				}
			}

			if (launchConfiguration != null) {
				DebugUITools.launch(launchConfiguration, mode);
			} else {
				ILaunchConfigurationWorkingCopy copy = type.newInstance(null, manager.generateLaunchConfigurationName(
						LaunchConfigurationsMessages.CreateLaunchConfigurationAction_New_configuration_2));

				copy.setAttribute(Constants.LAUNCH_SELECTED_PROJECT, project.getName());

				int result = DebugUITools.openLaunchConfigurationDialog(PlatformUIUtil.getActiveShell(), copy,
						Constants.LAUNCH_GROUP, null);

				if (result == Window.OK) {
					launchConfiguration = copy.doSave();
				}
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
