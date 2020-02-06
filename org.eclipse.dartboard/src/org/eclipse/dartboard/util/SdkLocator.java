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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Base support for locating SDK artifacts
 * 
 * @author Andrew Bowley
 *
 */
public abstract class SdkLocator {

	protected SdkLocator() {
	}

	/**
	 * Returns arguments to execute a query in a command shell which returns the
	 * path to a given command
	 * 
	 * @param program     Command to locate
	 * @param interactive Flag if set true allows the user to type commands. May be
	 *                    ignored if no OS support.
	 * @return String[]
	 * @throws ExecutionException
	 */
	public abstract String[] getLocationCommand(String program, boolean interactive) throws ExecutionException;

	/**
	 * Returns the path to a tool within the SDK bin directory in a system agnostic
	 * format. The appropriate file extension, if any, is also accepted eg. ".bat"
	 * on Windows.
	 *
	 * A tool in the Dart SDK bin directory is any of the various executables, that
	 * are .bat files on the windows version of the SDK.
	 *
	 * These tools are: dart2aot, dart2js, dartanalyzer, dartdevc, dartdoc, dartfmt,
	 * pub
	 *
	 * @param sdkLocation Absolute location of Dart SDK
	 * @param name        - The name of the tool
	 * @return The path to the tool valid for the host operating system
	 */
	public abstract String resolveToolPath(String sdkLocation, String name);

	/**
	 * Returns the path to an executable within the SDK bin directory in a system
	 * agnostic format.
	 *
	 * The difference to {@link #resolveToolPath(String)} is that, on Windows, this
	 * method returns the path to an .exe file.
	 *
	 * These executables are: dart and dartaotruntime
	 *
	 * @param sdkLocation Absolute location of Dart SDK
	 * @param name        - The name of the executable
	 * @return The path to the executable valid for the host operating system
	 */
	public abstract String resolveExecutablePath(String sdkLocation, String name);

	/**
	 * Returns path to given command or null if not found
	 * 
	 * @param program Command as entered by user
	 * @return Optional object of parametric type Path
	 * @throws ExecutionException
	 */
	public Optional<Path> getLocation(String program) throws ExecutionException {
		return getLocation(program, false);
	}

	/**
	 * Returns path to SDK home of given command or null if not found. Assumes the
	 * command is in the 'bin' folder directly under home or in home itself
	 * 
	 * @param program     Command as entered by user
	 * @param interactive Flag if set true allows the user to type commands. Not
	 *                    supported on Windows.
	 * @return Optional object of parametric type Path
	 * @throws ExecutionException
	 */
	public Optional<Path> getLocation(String program, boolean interactive) throws ExecutionException {

		Path path = null;
		String[] command = getLocationCommand(program, interactive);
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(command);
		Process process;
		try {
			process = processBuilder.start();
			process.waitFor();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String location = reader.readLine();
				if (location != null) {
					try {
						path = Paths.get(location);
					} catch (InvalidPathException e) {
						return Optional.ofNullable(null);
					}
					path = path.toRealPath().getParent();
					if ((path == null) || (path.getFileName() == null))
						return Optional.ofNullable(null);
					if (path.getFileName().toString().equals("bin")) { //$NON-NLS-1$
						path = path.getParent();
						if (path == null)
							return Optional.ofNullable(null);
					}
				}
				// TODO: Try different default installs (need to collect them)
				return Optional.ofNullable(path);
			}
		} catch (IOException | InterruptedException e) {
			throw new ExecutionException(String.format("Error while locating command %s", program), e); //$NON-NLS-1$
		}
	}
}
