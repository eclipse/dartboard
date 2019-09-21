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
package org.eclipse.dartboard.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.Constants;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartUtil {

	private DartUtil() {
	}

	public static final boolean IS_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	static ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore();

	/**
	 * Returns the path to an executable within the Dart SDK bin directory in a
	 * system agnostic format.
	 * 
	 * The difference to {@link #getTool(String)} is that this method returns the
	 * path to an .exe file (on windows).
	 * 
	 * These executables are: dart and dartaotruntime
	 * 
	 * @param name - The name of the executable
	 * @return The path to the executable valid for the host operating system
	 */
	public static String getExecutable(String name) {
		String dartSdk = preferences.getString(Constants.PREFERENCES_SDK_LOCATION);

		String result = dartSdk + File.separator + "bin" + File.separator + name; //$NON-NLS-1$

		if (IS_WINDOWS) {
			return result + ".exe"; //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Returns the path to a tool within the Dart SDK bin directory in a system
	 * agnostic format.
	 * 
	 * A tool in the Dart SDK bin directory is any of the various executables, that
	 * are .bat files on the windows version of the SDK.
	 * 
	 * These tools are: dart2aot, dart2js, dartanalyzer, dartdevc, dartdoc, dartfmt,
	 * pub
	 * 
	 * @param name - The name of the tool
	 * @return The path to the tool valid for the host operating system
	 */
	public static String getTool(String name) {
		String dartSdk = preferences.getString(Constants.PREFERENCES_SDK_LOCATION);

		String result = dartSdk + File.separator + "bin" + File.separator + name; //$NON-NLS-1$

		if (IS_WINDOWS) {
			return result + ".bat"; //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Returns a {@link Path} containing the location of the Dart SDK folder.
	 * 
	 * This method finds the location of the Dart SDK on the system, if installed.
	 * On *nix based systems it tries to locate the Dart binary by using the
	 * {@code which} command. Typically the output is a symbolic link to the actual
	 * binary. Since the Dart SDK installation folder contains more binaries that we
	 * need, we resolve the symbolic link and return the path to the /bin directory
	 * inside the SDK installation folder.
	 * 
	 * On Windows this method uses the where command to locate the binary.
	 * 
	 * @return - An {@link Optional} of {@link Path} containing the path to the
	 *         {@code /bin} folder inside the Dart SDK installation directory or
	 *         empty if the SDK is not found on the host machine.
	 */
	public static Optional<Path> getDartLocation() throws IOException, InterruptedException {
		return getLocation("dart"); //$NON-NLS-1$
	}

	public static Optional<Path> getLocation(String program) throws IOException, InterruptedException {
		Path path = null;
		String[] command;
		if (IS_WINDOWS) {
			command = new String[] { "cmd", "/c", "where " + program }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			command = new String[] { "/bin/bash", "-c", "which " + program }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(command);
		Process process = processBuilder.start();
		process.waitFor();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String location = reader.readLine();
			if (location != null) {
				path = Paths.get(location);
				path = path.toRealPath().getParent();
			}
		}

		// TODO: Try different default installs (need to collect them)
		return Optional.ofNullable(path);
	}
}
