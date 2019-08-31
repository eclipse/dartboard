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
package org.eclipse.dartboard.launch.dart;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.launch.BaseLaunchConfigTab;
import org.eclipse.dartboard.util.DartPreferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DartLaunchConfigTab extends BaseLaunchConfigTab {

	private Text textMainClass;
	private static final Logger LOG = LoggerFactory.getLogger(DartLaunchConfigTab.class);

	private ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore();

	public DartLaunchConfigTab() {
		super("/icons/dart.png"); //$NON-NLS-1$
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
	public String getName() {
		return Messages.Launch_PageTitle;
	}

	@Override
	public void createExtraControls(Composite parent) {
		Label labelMainClass = new Label(parent, SWT.NONE);
		labelMainClass.setText(Messages.Launch_MainClass);
		GridDataFactory.swtDefaults().applyTo(labelMainClass);

		textMainClass = new Text(parent, SWT.BORDER);
		textMainClass.setMessage(Messages.Launch_MainClass_Message);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textMainClass);
		textMainClass.addModifyListener(event -> updateLaunchConfigurationDialog());
	}

	@Override
	public void saveExtraAttributes(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(Constants.LAUNCH_MAIN_CLASS, textMainClass.getText());
	}

}
