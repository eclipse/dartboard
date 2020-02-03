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
package org.eclipse.dartboard.launch;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author jonas
 *
 */
public abstract class BaseLaunchConfigTab extends AbstractLaunchConfigurationTab {

	private String name;

	protected Text textSdkLocation;
	protected Combo comboProject;

	private Image image;
	private String imagePath;

	public BaseLaunchConfigTab(String name, String imagePath) {
		this.name = name;
		this.imagePath = imagePath;
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
	public String getName() {
		return name;
	}

	@Override
	public Image getImage() {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		URL fileURL = bundle.getEntry(imagePath);
		ImageDescriptor createFromURL = ImageDescriptor.createFromURL(fileURL);
		image = createFromURL.createImage();
		return image;
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
		}
	}

}
