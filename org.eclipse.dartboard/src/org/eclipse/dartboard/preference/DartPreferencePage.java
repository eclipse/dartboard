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
package org.eclipse.dartboard.preference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link FieldEditorPreferencePage} that lets the user set various
 * preferences related to the Dart SDK, or other development related settings.
 * 
 * @author Jonas Hungershausen
 *
 */
public class DartPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final Logger LOG = LoggerFactory.getLogger(DartPreferencePage.class);

	/**
	 * A {@link DirectoryFieldEditor} used to obtain the Dart SDK location
	 */
	private DirectoryFieldEditor dartSDKLocationEditor;

	/**
	 * Initializes the {@link DartPreferencePage#getPreferenceStore()} to the
	 * default preference store of the plugin
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID));
	}

	/**
	 * Called once the "Apply" or "Apply and Close" buttons on the preference page
	 * are pressed.
	 */
	@Override
	public boolean performOk() {
		String sdkLocation = dartSDKLocationEditor.getStringValue();
		checkOk(sdkLocation);
		return super.performOk();
	}

	/**
	 * Checks if the value of {@link DartPreferencePage#dartSDKLocationEditor} is a
	 * valid Dart SDK location.
	 * 
	 * If the check is unsuccessful the page's validity is set to {@code false} to
	 * prevent clicks on apply. The user then has to choose a different location
	 * 
	 * @param location - The location of the Dart SDK that should be checked
	 */
	private void checkOk(String location) {
		Optional<String> optionalVersion = getVersion(location);
		if (optionalVersion.isPresent()) {
			getPreferenceStore().setValue(Constants.PREFERENCES_SDK_LOCATION, dartSDKLocationEditor.getStringValue());
			// TODO: Add version label?
			setValid(true);
		} else {
			dartSDKLocationEditor.setErrorMessage(Messages.Preference_SDKNotFound_Message);
			dartSDKLocationEditor.showErrorMessage();
			setValid(false);
		}
	}

	/**
	 * Returns the version of the Dart SDK at a given location. The version is
	 * obtained by executing the Dart SDK binary with the {@code --version} flag.
	 * 
	 * @param location - The location of the Dart SDK
	 * @return an {@link Optional} of a {@link String} containing the version of the
	 *         Dart SDK or {@link Optional#empty()} if the Dart SDK version could
	 *         not be obtained
	 */
	private Optional<String> getVersion(String location) {
		ProcessBuilder builder = new ProcessBuilder(location + "/bin/dart", "--version"); //$NON-NLS-1$ //$NON-NLS-2$

		builder.redirectErrorStream(true);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()))) {
			return Optional.ofNullable(reader.readLine());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return Optional.empty();
	}

	/**
	 * Creates the editor fields for the page
	 */
	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		dartSDKLocationEditor = new DirectoryFieldEditor(Constants.PREFERENCES_SDK_LOCATION,
				Messages.Preference_SDKLocation, parent);
		addField(dartSDKLocationEditor);

		Text textControl = dartSDKLocationEditor.getTextControl(parent);
		textControl.addModifyListener((event) -> {
			checkOk(textControl.getText());
		});
	}

	/**
	 * Called when changes to the property fields of the page are made.
	 * 
	 * This method performs a validity check on the fields.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (FieldEditor.VALUE.equals(event.getProperty())) {
			checkOk((String) event.getNewValue());
		}
		super.propertyChange(event);
	}
}
