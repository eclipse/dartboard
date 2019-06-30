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

import org.eclipse.dartboard.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class DartProjectPage extends WizardNewProjectCreationPage {

	public DartProjectPage(String pageName) {
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
		dartGroup.setText(Messages.NewProject_group_label);
		dartGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		dartGroup.setLayout(new GridLayout(2, false));

		Label label = new Label(dartGroup, SWT.NONE);
		label.setText(Messages.NewProject_sdk_version_label);

		Combo combo = new Combo(dartGroup, SWT.READ_ONLY | SWT.BORDER);
		GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textData.horizontalIndent = 0;
		combo.setLayoutData(textData);
	}
}
