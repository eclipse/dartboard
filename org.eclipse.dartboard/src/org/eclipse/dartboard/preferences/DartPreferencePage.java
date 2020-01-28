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
package org.eclipse.dartboard.preferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * A {@link FieldEditorPreferencePage} that lets the user set various
 * preferences related to the Dart SDK, or other development related settings.
 *
 * @author Jonas Hungershausen
 *
 */
public class DartPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final ILog LOG = Platform.getLog(DartPreferencePage.class);

	/**
	 * A {@link DirectoryFieldEditor} used to obtain the Dart SDK location
	 */
	private DartSDKLocationFieldEditor dartSDKLocationEditor;
	private FlutterSDKLocationFieldEditor flutterSDKLocationFieldEditor;

	public DartPreferencePage() {
		super(GRID);
	}

	/**
	 * Initializes the {@link DartPreferencePage#getPreferenceStore()} to the
	 * default preference store of the plugin
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DartPreferences.getPreferenceStore());
	}

	@Override
	public boolean performOk() {
		String sdkLocation = dartSDKLocationEditor.getStringValue();
		String oldValue = getPreferenceStore().getString(GlobalConstants.P_SDK_LOCATION_DART);

		boolean ok = super.performOk();
		// Don't update the preference store if the oldValue matches the new value
		if (sdkLocation.equals(oldValue)) {
			return true;
		}
		Path path = getPath(sdkLocation);
		// If the path is not valid, the page should not be able to be validated
		// *should* never happen
		if (path == null) {
			setValid(false);
			return false;
		}

		dartSDKLocationEditor.setStringValue(path.toAbsolutePath().toString());

		boolean result = MessageDialog.openQuestion(null, Messages.Preference_RestartRequired_Title,
				Messages.Preference_RestartRequired_Message);

		if (result) {
			try {
				// Manually save the preference store since it doesn't seem to happen when
				// restarting the IDE in the following step.
				save();
			} catch (IOException e) {
				LOG.log(DartLog.createError("Could not save IDE preferences", e)); //$NON-NLS-1$
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
				boolean isWindows = Platform.OS_WIN32.equals(Platform.getOS());
				path = Paths.get(location + "bin" + File.separator + (isWindows ? "dart.exe" : "dart")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				// Since we append /bin/dart to resolve the symbolic links, we need to get 2
				// levels up here.
				path = path.toRealPath().toAbsolutePath().getParent().getParent();
			} catch (IOException e) {
				LOG.log(DartLog.createError("Couldn't follow symlink", e)); //$NON-NLS-1$
			}
			return path;
		} else {
			return null;
		}
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		String[][] labelAndValues = new String[][] { { "Dart", "false" }, { "Flutter", "true" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		RadioGroupFieldEditor editor = new RadioGroupFieldEditor(GlobalConstants.P_FLUTTER_ENABLED,
				Messages.Preference_PluginMode_Label, 2, labelAndValues, parent, true);
		addField(editor);

		// Dart SDK location text field/file browser
		dartSDKLocationEditor = new DartSDKLocationFieldEditor(GlobalConstants.P_SDK_LOCATION_DART,
				Messages.Preference_SDKLocation_Dart, parent);
		addField(dartSDKLocationEditor);

		dartSDKLocationEditor.addModifyListener(event -> {
			setValid(dartSDKLocationEditor.doCheckState());
		});

		flutterSDKLocationFieldEditor = new FlutterSDKLocationFieldEditor(GlobalConstants.P_SDK_LOCATION_FLUTTER,
				Messages.Preference_SDKLocation_Flutter, parent);
		addField(flutterSDKLocationFieldEditor);

		BooleanFieldEditor autoPubSyncEditor = new BooleanFieldEditor(GlobalConstants.P_SYNC_PUB,
				Messages.Preference_PubAutoSync_Label, parent);
		addField(autoPubSyncEditor);
		BooleanFieldEditor useOfflinePub = new BooleanFieldEditor(GlobalConstants.P_OFFLINE_PUB,
				Messages.Preference_PubOffline_Label, parent);
		addField(useOfflinePub);

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
