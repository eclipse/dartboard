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
 *     Lakshminarayana Nekkanti
 *******************************************************************************/
package org.eclipse.dartboard.flutter.project;

import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class FlutterProjectPage extends WizardNewProjectCreationPage {

	private ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore();

	public FlutterProjectPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		createAdditionalControls((Composite) getControl());
	}

	private void createAdditionalControls(Composite parent) {
		Group dartGroup = new Group(parent, SWT.NONE);
		dartGroup.setFont(parent.getFont());
		dartGroup.setText(Messages.NewProject_Group_Label);
		dartGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		dartGroup.setLayout(new GridLayout(2, false));

		Label labelSdkLocation = new Label(dartGroup, SWT.NONE);
		labelSdkLocation.setText(Messages.Preference_SDKLocation_Dart);
		GridDataFactory.swtDefaults().applyTo(labelSdkLocation);

		Label sdkLocation = new Label(dartGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(sdkLocation);
		sdkLocation.setText(preferences.getString(GlobalConstants.P_SDK_LOCATION_FLUTTER));

	}


	@Override
	protected boolean validatePage() {
		boolean isValid = super.validatePage();
		if (isValid && "".equals(preferences.getString(GlobalConstants.P_SDK_LOCATION_FLUTTER))) { //$NON-NLS-1$
			setMessage(Messages.NewProject_SDK_Not_Found, IMessageProvider.WARNING);
			// not making as invalid.Since its the temporary solution
		}
		return isValid;
	}
}
