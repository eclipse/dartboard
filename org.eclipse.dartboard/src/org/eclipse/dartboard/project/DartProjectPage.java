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
package org.eclipse.dartboard.project;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.stagehand.StagehandService;
import org.eclipse.dartboard.stagehand.StagehandTemplate;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartProjectPage extends WizardNewProjectCreationPage {

	private ScopedPreferenceStore preferences;
	private Combo stagehandTemplates;
	private Button useStagehandButton;
	private List<StagehandTemplate> templates;

	public DartProjectPage(String pageName) {
		super(pageName);
		preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID);
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
		labelSdkLocation.setText(Messages.Preference_SDKLocation);
		GridDataFactory.swtDefaults().applyTo(labelSdkLocation);

		Label sdkLocation = new Label(dartGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(sdkLocation);
		sdkLocation.setText(preferences.getString(Constants.PREFERENCES_SDK_LOCATION));

		// ------------------------------------------
		Group stagehandGroup = new Group(parent, SWT.NONE);
		stagehandGroup.setFont(parent.getFont());
		stagehandGroup.setText(Messages.NewProject_Stagehand_Title);
		stagehandGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		stagehandGroup.setLayout(new GridLayout(1, false));

		useStagehandButton = new Button(stagehandGroup, SWT.CHECK);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(useStagehandButton);
		useStagehandButton.setEnabled(false);

		stagehandTemplates = new Combo(stagehandGroup, SWT.READ_ONLY);
		stagehandTemplates.setEnabled(useStagehandButton.getSelection());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(stagehandTemplates);

		useStagehandButton.setText(Messages.NewProject_Stagehand_UseStagehandButtonText);
		useStagehandButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Enable/Disable the stagehand templates list if the button is
				// checked/unchecked
				stagehandTemplates.setEnabled(useStagehandButton.getSelection());
				if (stagehandTemplates.getSelectionIndex() == -1) {
					stagehandTemplates.select(0);
				}
			}
		});

		ProgressIndicator indicator = new ProgressIndicator(stagehandGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(indicator);
		indicator.beginAnimatedTask();

		Job.create(Messages.NewProject_Stagehand_FetchStagehand, monitor -> {
			templates = StagehandService.getStagehandTemplates();

			Display.getDefault().asyncExec(() -> {
				templates.forEach(str -> stagehandTemplates.add(str.getDisplayName()));
				indicator.done();
				useStagehandButton.setEnabled(true);
			});
		}).schedule();


	}

	public StagehandTemplate getGenerator() {
		if (useStagehandButton.getSelection()) {
			return templates.get(stagehandTemplates.getSelectionIndex());
		}
		return null;
	}

	@Override
	protected boolean validatePage() {
		boolean isValid = super.validatePage();
		if (isValid && "".equals(preferences.getString(Constants.PREFERENCES_SDK_LOCATION))) { //$NON-NLS-1$
			setMessage(Messages.NewProject_SDK_Not_Found, IMessageProvider.WARNING);
			// not making as invalid.Since its the temporary solution
		}
		return isValid;
	}
}
