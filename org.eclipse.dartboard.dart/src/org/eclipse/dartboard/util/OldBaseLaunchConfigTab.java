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
package org.eclipse.dartboard.util;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.dart.Constants;
import org.eclipse.dartboard.messages.Messages;
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

/**
 * @author jonas
 *
 */
public abstract class OldBaseLaunchConfigTab extends AbstractLaunchConfigurationTab {

	private String name;

	protected Text textSdkLocation;
	protected Combo comboProject;

	
	private Image image;
	private String sdkLocationPreferenceKey;

	public OldBaseLaunchConfigTab(String name, String imagePath, String sdkLocationPreferenceKey) {
		this.name = name;
		this.sdkLocationPreferenceKey = sdkLocationPreferenceKey;
		Bundle bundle = Platform.getBundle(Constants.PLUGIN_ID);
		URL fileURL = bundle.getEntry(imagePath);
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

		textSdkLocation = new Text(comp, SWT.BORDER);
		textSdkLocation.setMessage("SDK Location");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textSdkLocation);
		textSdkLocation.addModifyListener(event -> updateLaunchConfigurationDialog());

		createPageSpecificControls(comp);
	}

	public abstract void createPageSpecificControls(Composite comp);

	public IProject[] getProjectsInWorkspace() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		return root.getProjects();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(sdkLocationPreferenceKey, textSdkLocation.getText());
		configuration.setAttribute(Constants.LAUNCH_SELECTED_PROJECT, comboProject.getText());
	}

	@Override
	public String getName() {
		return name;
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
