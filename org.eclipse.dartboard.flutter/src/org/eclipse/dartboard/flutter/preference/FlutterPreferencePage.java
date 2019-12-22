package org.eclipse.dartboard.flutter.preference;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.flutter.FlutterConstants;
import org.eclipse.dartboard.flutter.util.FlutterUtil;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class FlutterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final ILog LOG = Platform.getLog(FlutterPreferencePage.class);

	private FlutterSDKLocationFieldEditor flutterSDKLocationEditor;

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DartPreferences.getPreferenceStore(FlutterConstants.PLUGIN_ID));
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		// Dart SDK location text field/file browser
		flutterSDKLocationEditor = new FlutterSDKLocationFieldEditor(FlutterConstants.PREFERENCES_SDK_LOCATION,
				"Flutter SDK Location", parent);
		addField(flutterSDKLocationEditor);

		flutterSDKLocationEditor.addModifyListener(event -> {
			setValid(flutterSDKLocationEditor.doCheckState());
		});
	}

	@Override
	public boolean isValid() {
		if (flutterSDKLocationEditor.getStringValue().isEmpty()) {

			return true;
		}
		return flutterSDKLocationEditor.isValid();
	}

	@Override
	public boolean performOk() {
		String sdkLocation = flutterSDKLocationEditor.getStringValue();
		String oldValue = getPreferenceStore().getString(FlutterConstants.PREFERENCES_SDK_LOCATION);

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

		flutterSDKLocationEditor.setStringValue(path.toAbsolutePath().toString());

		boolean result = MessageDialog.openQuestion(null, "Restart required", "Restart now?");

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

	private void save() throws IOException {
		IPreferenceStore store = getPreferenceStore();
		if (store.needsSaving() && store instanceof IPersistentPreferenceStore) {
			((IPersistentPreferenceStore) store).save();
		}
	}

	private Path getPath(String location) {
		if (flutterSDKLocationEditor.isValid()) {
			Path path = null;
			try {
				path = Paths.get(FlutterUtil.getFlutterToolPath(location));
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

}
