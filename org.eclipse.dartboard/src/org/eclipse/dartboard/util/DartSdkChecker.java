/*******************************************************************************
 * Copyright (c) 2020 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Andrew Bowley 
 *******************************************************************************/
package org.eclipse.dartboard.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.eclipse.swt.widgets.Shell;

/**
 * Base for checking if a given location contains a Dart SDK. Values subject to
 * context (Dart or Flutter) and operating system (Linux or Windows) are to be
 * provided by sub classes.
 * 
 * @author Andrew Bowley
 *
 */
public abstract class DartSdkChecker {

	private static final String DART_PREFIX = "Dart VM version"; //$NON-NLS-1$

	/** Parent shell required by busy cursor */
	private final Shell shell;
	/** Relative path from SDK root to Dart executable, no terminating path separator */
	private final String relativePath;

	/**
	 * Construct DartSdkChecker object
	 * 
	 * @param shell        Parent shell or null if not available
	 * @param relativePath Relative path from SDK root to Dart executable
	 */
	protected DartSdkChecker(Shell shell, String relativePath) {
		this.shell = shell;
		this.relativePath = relativePath;
	}

	/**
	 * Returns arguments to execute a query in a command shell which returns the
	 * Dart version
	 * 
	 * @param executablePath Absolute path to Dart executable
	 * @return String[]
	 */
	public abstract String[] getDartVersionCommands(String executablePath);

	/**
	 * Return black list, or empty list if none. See
	 * https://github.com/eclipse/dartboard/issues/103
	 * 
	 * @return List<String>
	 */
	public abstract List<String> getBlacklist();

	/**
	 * Returns name of Dart executable
	 * 
	 * @return filename
	 */
	public abstract String getDartExecutable();

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
	public boolean isValidDartSDK(String location) throws ExecutionException {
		if (location.isEmpty()) {

			return false;
		}
		Path path = null;
		// On Windows if a certain wrong combination of characters are entered a
		// InvalidPathException is thrown. In that case we can assume that the location
		// entered is not a valid Dart SDK directory either.
		String internalPath = 
				relativePath.isEmpty() ? 
				getDartExecutable() :
				relativePath + File.separator + getDartExecutable();
		try {
			path = Paths.get(location).resolve(internalPath);
		} catch (InvalidPathException e) {

			return false;
		}

		// See https://github.com/eclipse/dartboard/issues/103
		List<String> blacklist = getBlacklist();
		if (!blacklist.isEmpty() && blacklist.contains(path.toString().toLowerCase())) {

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
			throw new ExecutionException("Couldn't follow symlink", e1);
		}

		// Show busy cursor while running command shell to verify Dart SDK
		BusyCursor busyCursor = new BusyCursor(shell);
		final String executablePath = path.toAbsolutePath().toString();

		return busyCursor.waitForObject(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return verifyDartSdk(getProcessBuilder(executablePath));
			}
		});
	}

	protected ProcessBuilder getProcessBuilder(String executablePath) {
		String[] commands = getDartVersionCommands(executablePath);
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.redirectErrorStream(true);

		return processBuilder;
	}

	private boolean verifyDartSdk(ProcessBuilder processBuilder) {
		String version = null;
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(processBuilder.start().getInputStream()))) {
			version = reader.readLine();
		} catch (IOException e) {
			return false;
		}

		return (version != null) && version.startsWith(DART_PREFIX);
	}
}
