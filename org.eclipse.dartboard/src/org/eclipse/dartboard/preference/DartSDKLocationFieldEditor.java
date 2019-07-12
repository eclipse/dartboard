package org.eclipse.dartboard.preference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.Platform;
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
		boolean isValid = isValidDartSDK(location);
		if (!isValid) {
			setErrorMessage(Messages.Preference_SDKNotFound_Message);
			showErrorMessage();
		}
		return isValid;
	}

	@SuppressWarnings("nls")
	private boolean isValidDartSDK(String location) {
		Path path = Paths.get(location);
		if (!Files.exists(path)) {
			return false;
		}

		String[] commands;
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			commands = new String[] { "cmd", "/c", location + "/bin/dart --version" };
		} else {
			commands = new String[] { "/bin/bash", "-c", location + "/bin/dart --version" };
		}

		ProcessBuilder processBuilder = new ProcessBuilder(commands);

		processBuilder.redirectErrorStream(true);
		String version = null;
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(processBuilder.start().getInputStream()))) {
			version = reader.readLine();
		} catch (IOException e) {
			return false;
		}

		return version.startsWith("Dart VM version");
	}

	protected void addModifyListener(ModifyListener listener) {
		getTextControl().addModifyListener(listener);
	}

}
