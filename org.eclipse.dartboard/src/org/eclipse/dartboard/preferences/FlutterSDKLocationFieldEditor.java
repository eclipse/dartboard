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
 *     Jonas Hungershausen
 *******************************************************************************/
package org.eclipse.dartboard.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author jonas
 *
 */
public class FlutterSDKLocationFieldEditor extends DirectoryFieldEditor {

	private static final ILog LOG = Platform.getLog(FlutterSDKLocationFieldEditor.class);
	private boolean validFlutterSDK = true;

	public FlutterSDKLocationFieldEditor(String preferencesKey, String label, Composite parent) {
		super(preferencesKey, label, parent);
		setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
	}

	@Override
	protected boolean doCheckState() {
		String location = getTextControl().getText();
		Job sdkCheck = Job.create("Check Flutter and Dart SDK location", //$NON-NLS-1$
				(ICoreRunnable) monitor -> validateSDKLocation(location));
		sdkCheck.schedule();
		return validFlutterSDK;
	}

	@Override
	protected void valueChanged() {
		if (!validFlutterSDK) {
			setErrorMessage("Not a valid Flutter SDK"); //$NON-NLS-1$
			showErrorMessage();
		}
		super.valueChanged();
	}

	private void validateSDKLocation(String location) {
		if (validFlutterSDK != isValidFlutterSDK(location)) {
			validFlutterSDK = isValidFlutterSDK(location);
			Display.getDefault().asyncExec(() -> {
				valueChanged();
			});
		}
	}

	private boolean isValidFlutterSDK(String location) {


		if (location.isEmpty()) {
			return false;
		}
		boolean isWindows = Platform.OS_WIN32.equals(Platform.getOS());

		Path path = null;
		// On Windows if a certain wrong combination of characters are entered a
		// InvalidPathException is thrown. In that case we can assume that the location
		// entered is not a valid Dart SDK directory either.
		try {
			path = Paths.get(location).resolve("bin" + File.separator + (isWindows ? "flutter.bat" : "flutter"));
		} catch (InvalidPathException e) {
			return false;
		}

		// See https://github.com/eclipse/dartboard/issues/103
		// List<String> blacklist = Lists.newArrayList("/bin/dart", "/usr/bin/dart");
		// If the entered file doesn't exist, there is no need to run it
		// Similarly if the file is a directory it can't be the dart executable
		if (!Files.exists(path) || Files.isDirectory(path)) {
			return false;
		}
		// Follow symbolic links
		try {
			path = path.toRealPath();
		} catch (IOException e1) {
			LOG.log(DartLog.createError("Couldn't follow symlink", e1)); //$NON-NLS-1$
			return false;
		}

		String executablePath = path.toAbsolutePath().toString();

		String[] commands;
		if (isWindows) {
			commands = new String[] { "cmd", "/c", executablePath, "--version" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			commands = new String[] { "/bin/sh", "-c", executablePath + " --version" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

		System.out.println("xx");

		return version.startsWith("Flutter"); //$NON-NLS-1$
	}

}
