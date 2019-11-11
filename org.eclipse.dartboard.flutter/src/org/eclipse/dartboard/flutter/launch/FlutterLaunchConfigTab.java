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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.launch.BaseLaunchConfigTab;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class FlutterLaunchConfigTab extends BaseLaunchConfigTab {

	private String sdkLocationPreferenceKey;
	protected ScopedPreferenceStore preferences = DartPreferences
			.getPreferenceStore(org.eclipse.dartboard.flutter.Constants.PLUGIN_ID);

	private static final ILog LOG = Platform.getLog(FlutterLaunchConfigTab.class);

	protected Text textMainClass;

	public FlutterLaunchConfigTab() {
		super("Launch Flutter App", "/icons/flutter.png", "flutter.sdkLocation");
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

	@Override
	public void createPageSpecificControls(Composite comp) {
		Label labelMainClass = new Label(comp, SWT.NONE);
		labelMainClass.setText("Entry point");
		GridDataFactory.swtDefaults().applyTo(labelMainClass);

		textMainClass = new Text(comp, SWT.BORDER);
		textMainClass.setMessage("The main entry-point file of the application");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textMainClass);
		textMainClass.addModifyListener(event -> updateLaunchConfigurationDialog());
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String defaultLocation = preferences.getString(sdkLocationPreferenceKey);
			String location = configuration.getAttribute(sdkLocationPreferenceKey, defaultLocation);
			textSdkLocation.setText(location);

			comboProject.setText(
					configuration.getAttribute(org.eclipse.dartboard.flutter.Constants.LAUNCH_SELECTED_PROJECT, "")); //$NON-NLS-1$
			setDirty(true);
		} catch (CoreException e) {
			LOG.log(DartLog.createError("Couldn't initialize LaunchConfigTab", e)); //$NON-NLS-1$
		}
		try {
			String mainClass = configuration.getAttribute("flutter.targetFile", "lib/main.dart"); //$NON-NLS-1$
			textMainClass.setText(mainClass);
			setDirty(true);
		} catch (CoreException e) {
			LOG.log(DartLog.createError("Couldn't initialize LaunchConfigTab", e)); //$NON-NLS-1$
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("flutter.targetFile", textMainClass.getText());
	}

}