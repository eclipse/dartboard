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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class LaunchConfigTab extends AbstractLaunchConfigurationTab {

	private Text textSdkLocation;
	private Text textMainClass;
	private Combo comboProject;

	private ScopedPreferenceStore preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID);
	private Image image;

	public LaunchConfigTab() {
		ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.dartboard", //$NON-NLS-1$
				"icons/dart_16.png"); //$NON-NLS-1$
		image = descriptor != null ? descriptor.createImage() : null;
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.BORDER);
		setControl(comp);

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

		Label labelProject = new Label(comp, SWT.NONE);
		labelProject.setText(Messages.Launch_Project);
		GridDataFactory.swtDefaults().applyTo(labelProject);

		comboProject = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		for (IProject project : getProjectsInWorkspace()) {
			comboProject.add(project.getName());
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboProject);
		comboProject.addModifyListener((event) -> updateLaunchConfigurationDialog());

		Label labelSdkLocation = new Label(comp, SWT.NONE);
		labelSdkLocation.setText(Messages.Preference_SDKLocation);
		GridDataFactory.swtDefaults().applyTo(labelSdkLocation);

		textSdkLocation = new Text(comp, SWT.BORDER);
		textSdkLocation.setMessage(Messages.Launch_SDKLocation_Message);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textSdkLocation);
		textSdkLocation.addModifyListener((event) -> updateLaunchConfigurationDialog());

		Label labelMainClass = new Label(comp, SWT.NONE);
		labelMainClass.setText(Messages.Launch_MainClass);
		GridDataFactory.swtDefaults().applyTo(labelMainClass);

		textMainClass = new Text(comp, SWT.BORDER);
		textMainClass.setMessage(Messages.Launch_MainClass_Message);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textMainClass);
		textMainClass.addModifyListener((event) -> updateLaunchConfigurationDialog());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// This is currently not in use.
		// TODO(Jonas): Evaluate if this could be used
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String defaultLocation = preferences.getString(Constants.PREFERENCES_SDK_LOCATION);
			String location = configuration.getAttribute(Constants.PREFERENCES_SDK_LOCATION, defaultLocation);
			textSdkLocation.setText(location);

			String mainClass = configuration.getAttribute(Constants.LAUNCH_MAIN_CLASS, "main.dart"); //$NON-NLS-1$
			textMainClass.setText(mainClass);

			// String selectedProject =
			comboProject.setText(configuration.getAttribute(Constants.LAUNCH_SELECTED_PROJECT, "")); //$NON-NLS-1$
			this.setDirty(true);
		} catch (CoreException e) {
			// ignore here
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(Constants.PREFERENCES_SDK_LOCATION, textSdkLocation.getText());
		configuration.setAttribute(Constants.LAUNCH_MAIN_CLASS, textMainClass.getText());
		configuration.setAttribute(Constants.LAUNCH_SELECTED_PROJECT, comboProject.getText());
	}

	@Override
	public String getName() {
		return Messages.Launch_PageTitle;
	}

	private IProject[] getProjectsInWorkspace() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		return root.getProjects();
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
		}
	}
}
