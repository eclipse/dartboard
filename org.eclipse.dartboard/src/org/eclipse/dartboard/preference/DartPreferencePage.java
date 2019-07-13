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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
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
		System.err.println(path);

		dartSDKLocationEditor.setStringValue(path.toAbsolutePath().toString());

		boolean ok = super.performOk();
		boolean result = MessageDialog.openQuestion(null, Messages.Preference_RestartRequired_Title,
				Messages.Preference_RestartRequired_Message);

		if (result) {
			try {
				// Manually save the preference store since it doesn't seem to happen when
				// restarting the IDE in the following step.
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			Display.getDefault().asyncExec(() -> {
				PlatformUI.getWorkbench().restart(true);
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
		if (dartSDKLocationEditor.isValid()) {
			Path path = null;
			try {
				path = Paths.get(location + "/bin/dart"); //$NON-NLS-1$
				// Since we append /bin/dart to resolve the symbolic links, we need to get 2
				// levels up here.
				path = path.toRealPath().toAbsolutePath().getParent().getParent();
			} catch (IOException e) {
				LOG.error("Couldn't follow symlink", e); //$NON-NLS-1$
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

	/**
	 * Saves the underlying {@link IPersistentPreferenceStore}.
	 * 
	 * @throws IOException
	 */
	private void save() throws IOException {
		IPreferenceStore store = getPreferenceStore();
		if (store.needsSaving() && store instanceof IPersistentPreferenceStore) {
			((IPersistentPreferenceStore) store).save();
		}
	}
}
