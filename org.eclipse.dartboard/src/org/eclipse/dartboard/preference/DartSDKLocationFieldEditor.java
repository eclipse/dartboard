package org.eclipse.dartboard.preference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.Messages;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DartSDKLocationFieldEditor extends DirectoryFieldEditor {

	private static final Logger LOG = LoggerFactory.getLogger(DartSDKLocationFieldEditor.class);

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

	/**
	 * Checks if a given path is the root directory of a Dart SDK installation.
	 * 
	 * Returns false if the path does not exist or the given location can not be
	 * converted to a {@link Path}.
	 * 
	 * Similarly if the Path is not a directory, false is returned.
	 * 
	 * If the location is a symbolic link but it can not be resolved, false is
	 * returned.
	 * 
	 * If the process to test the version string returned by the Dart executable can
	 * not be executed, false is returned.
	 * 
	 * Finally, if the returned version string does not start with "Dart VM
	 * version", false is returned.
	 * 
	 * @param location - A {@link String} that should be checked to be a Dart SDK
	 *                 root directory.
	 * @return <code>false</code> if the location is not a Dart SDK root directory,
	 *         <code>true</code> otherwise.
	 */
	@SuppressWarnings("nls")
	private boolean isValidDartSDK(String location) {
		// See https://github.com/eclipse/dartboard/issues/103
		if (location.equalsIgnoreCase("/") || location.equalsIgnoreCase("/usr")) {
			return false;
		}
		boolean isWindows = Platform.OS_WIN32.equals(Platform.getOS());
		Path path = null;
		// On Windows if a certain wrong combination of characters are entered a
		// InvalidPathException is thrown. In that case we can assume that the location
		// entered is not a valid Dart SDK directory either.
		try {
			path = Paths.get(location).resolve("bin" + File.separator + (isWindows ? "dart.exe" : "dart"));
		} catch (InvalidPathException e) {
			return false;
		}
		// If the entered file doesn't exist, there is no need to run it
		// Similarly if the file is a directory it can't be the dart executable
		if (!Files.exists(path) || Files.isDirectory(path)) {
			return false;
		}
		// Follow symbolic links
		try {
			path = path.toRealPath();
		} catch (IOException e1) {
			LOG.error("Couldn't follow symlink", e1);
			return false;
		}

		String executablePath = path.toAbsolutePath().toString();

		String[] commands;
		if (isWindows) {
			commands = new String[] { "cmd", "/c", executablePath, "--version" };
		} else {
			commands = new String[] { "/bin/bash", "-c", executablePath + " --version" };
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
