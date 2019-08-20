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

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.DartPreferences;
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
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchConfigTab extends AbstractLaunchConfigurationTab {

	private static final Logger LOG = LoggerFactory.getLogger(LaunchConfigTab.class);

	private Text textSdkLocation;
	private Text textMainClass;
	private Combo comboProject;

	private ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore();
	private Image image;

	public LaunchConfigTab() {
		Bundle bundle = Platform.getBundle(Constants.PLUGIN_ID);
		URL fileURL = bundle.getEntry("/icons/dart.png"); //$NON-NLS-1$
		ImageDescriptor createFromURL = ImageDescriptor.createFromURL(fileURL);
		image = createFromURL.createImage();
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.NONE);
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
		comboProject.addModifyListener(event -> updateLaunchConfigurationDialog());

		Label labelSdkLocation = new Label(comp, SWT.NONE);
		labelSdkLocation.setText(Messages.Preference_SDKLocation);
		GridDataFactory.swtDefaults().applyTo(labelSdkLocation);

		textSdkLocation = new Text(comp, SWT.NONE);
		textSdkLocation.setMessage(Messages.Launch_SDKLocation_Message);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textSdkLocation);
		textSdkLocation.addModifyListener(event -> updateLaunchConfigurationDialog());

		Label labelMainClass = new Label(comp, SWT.NONE);
		labelMainClass.setText(Messages.Launch_MainClass);
		GridDataFactory.swtDefaults().applyTo(labelMainClass);

		textMainClass = new Text(comp, SWT.NONE);
		textMainClass.setMessage(Messages.Launch_MainClass_Message);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textMainClass);
		textMainClass.addModifyListener(event -> updateLaunchConfigurationDialog());
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
			setDirty(true);
		} catch (CoreException e) {
			LOG.error("Couldn't initialize LaunchConfigTab", e); //$NON-NLS-1$
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
