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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
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
	private DartSDKLocationFieldEditor dartSDKLocationEditor;

	/**
	 * Initializes the {@link DartPreferencePage#getPreferenceStore()} to the
	 * default preference store of the plugin
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID));
	}

	@Override
	public boolean performOk() {
		String sdkLocation = dartSDKLocationEditor.getStringValue();
		String oldValue = getPreferenceStore().getString(Constants.PREFERENCES_SDK_LOCATION);
		// Don't update the preference store if the oldValue matches the new value
		if (sdkLocation.equals(oldValue)) {
			return true;
		}
		Path path = getPath(sdkLocation);
		// If the path is not valid, the page should not be able to be validated;
		// *should* never happen
		if (path == null) {
			setValid(false);
			return false;
		}

		dartSDKLocationEditor.setStringValue(path.toAbsolutePath().toString());

		boolean ok = super.performOk();
		boolean result = MessageDialog.openQuestion(null, Messages.Preference_RestartRequired_Title,
				Messages.Preference_RestartRequired_Message);
		if (result) {
			Display.getDefault().asyncExec(() -> {
				PlatformUI.getWorkbench().restart();
			});
		}
		return ok;
	}

	/**
	 * Normalizes and transforms the path of a supplied location.
	 * 
	 * @param location
	 * @return
	 */
	private Path getPath(String location) {
		Path path = Paths.get(location);
		if (Files.exists(path)) {
			try {
				path = path.toRealPath();
			} catch (IOException e) {
				LOG.error("Couldn't follow symlink", e); //$NON-NLS-1$
			}

			// Sometimes users put in the path to the Dart executable directly, instead of
			// the directory of the installation. Here we use the parent first (which should
			// be /bin)
			if (path.endsWith("dart")) { //$NON-NLS-1$
				path = path.getParent();
			}
			// Sometimes users put in the path when it still contains the /bin portion.
			// Since we only want the root of the Dart SDK installation we use the parent if
			// /bin was supplied.
			if (path.endsWith("bin")) {//$NON-NLS-1$
				path = path.getParent();
			}

			return path;
		} else {
			return null;
		}
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		// Dart SDK location text field/file browser
		dartSDKLocationEditor = new DartSDKLocationFieldEditor(Constants.PREFERENCES_SDK_LOCATION,
				Messages.Preference_SDKLocation, parent);
		addField(dartSDKLocationEditor);

		dartSDKLocationEditor.addModifyListener(event -> {
			setValid(dartSDKLocationEditor.doCheckState());
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
			setValid(dartSDKLocationEditor.doCheckState());
		}
		super.propertyChange(event);
	}
}
