package org.eclipse.dartboard.preference;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.dartboard.Messages;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

import com.google.common.base.Strings;

public class DartSDKLocationFieldEditor extends DirectoryFieldEditor {

	public DartSDKLocationFieldEditor(String preferencesKey, String label, Composite parent) {
		super(preferencesKey, label, parent);
		setValidateStrategy(VALIDATE_ON_KEY_STROKE);
	}

	@Override
	protected boolean doCheckState() {
		boolean isValid = true;
		String location = getTextControl().getText();
		if (Strings.isNullOrEmpty(location) || !Files.exists(Paths.get(location))) {
			isValid = false;
		}
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
