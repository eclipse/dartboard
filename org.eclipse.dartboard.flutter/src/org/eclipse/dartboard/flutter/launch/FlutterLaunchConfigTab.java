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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.util.BaseLaunchConfigTab;
import org.eclipse.dartboard.util.StatusUtil;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author jonas
 *
 */
public class FlutterLaunchConfigTab extends BaseLaunchConfigTab {

	private static final ILog LOG = Platform.getLog(FlutterLaunchConfigTab.class);

	protected Text textMainClass;

	public FlutterLaunchConfigTab() {
		super("Launch Flutter App", "/icons/flutter.png", "flutter.sdkLocation");
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
		super.initializeFrom(configuration);
		try {
			String mainClass = configuration.getAttribute("flutter.targetFile", "lib/main.dart"); //$NON-NLS-1$
			textMainClass.setText(mainClass);
			setDirty(true);
		} catch (CoreException e) {
			LOG.log(StatusUtil.createError("Couldn't initialize LaunchConfigTab", e)); //$NON-NLS-1$
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		configuration.setAttribute("flutter.targetFile", textMainClass.getText());
	}

}