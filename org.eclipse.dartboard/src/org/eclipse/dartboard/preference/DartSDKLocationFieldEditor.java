package org.eclipse.dartboard.preference;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.dartboard.Messages;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

public class DartSDKLocationFieldEditor extends DirectoryFieldEditor {

	public DartSDKLocationFieldEditor(String preferencesKey, String label, Composite parent) {
		super(preferencesKey, label, parent);
		setValidateStrategy(VALIDATE_ON_KEY_STROKE);
	}

	@Override
	protected boolean doCheckState() {
		String location = getTextControl().getText();
		Path path = Paths.get(location);
		if (Files.exists(path)) {
			return true;
		} else {
			setErrorMessage(Messages.Preference_SDKNotFound_Message);
			showErrorMessage();
			return false;
		}
	}

	protected void addModifyListener(ModifyListener listener) {
		getTextControl().addModifyListener(listener);
	}

}
