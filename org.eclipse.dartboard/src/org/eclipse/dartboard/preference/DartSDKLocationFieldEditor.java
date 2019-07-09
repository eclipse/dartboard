package org.eclipse.dartboard.preference;

import java.io.File;

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
		File file = new File(location);
		boolean isValid = file.exists();
		if (!isValid) {
			setErrorMessage(Messages.Preference_SDKNotFound_Message);
			showErrorMessage();
		}
		return isValid;
	}

	protected void addModifyListener(ModifyListener listener) {
		getTextControl().addModifyListener(listener);
	}

}
