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

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.swt.widgets.Shell;

/**
 * Platform utilities
 * 
 * @author Andrew Bowley
 *
 */
public class PlatformUtil {

	private static final ILog LOG = Platform.getLog(PlatformUtil.class);

	private IPlatformFactory platformFactory;

	private static volatile PlatformUtil instance;

	private PlatformUtil() {
		boolean isWindows = Platform.OS_WIN32.equals(Platform.getOS());
		platformFactory = isWindows ? new org.eclipse.dartboard.os.windows.PlatformFactory()
				: new org.eclipse.dartboard.os.linux.PlatformFactory();
	}

	/**
	 * Returns a Dart SDK checker
	 * 
	 * @param shell     Parent shell of owner or null if none
	 * @param isFlutter Flag set true if Dart SDK is inside Flutter
	 * @return DartSdkChecker object
	 */
	public DartSdkChecker getDartSdkChecker(Shell shell, boolean isFlutter) {
		return platformFactory.getDartSdkChecker(shell, isFlutter);
	}

	/**
	 * Returns path to given command or null if not found
	 * 
	 * @param program Command as entered by user
	 * @return Optional object of parametric type Path
	 * @throws ExecutionException
	 */
	public Optional<Path> getLocation(String program) throws ExecutionException {
		return platformFactory.getSdkLocator().getLocation(program);
	}

	/**
	 * Returns path to given command or null if not found
	 * 
	 * @param program     Command as entered by user
	 * @param interactive Flag if set true allows the user to type commands. Not
	 *                    supported on Windows.
	 * @return Optional object of parametric type Path
	 * @throws ExecutionException
	 */
	public Optional<Path> getLocation(String program, boolean interactive) throws ExecutionException {
		return platformFactory.getSdkLocator().getLocation(program, interactive);
	}

	/**
	 * Returns the path to an executable within the Dart SDK bin directory in a
	 * system agnostic format.
	 *
	 * The difference to {@link #getTool(String)} is that this method returns the
	 * path to an .exe file (on windows).
	 *
	 * These executables are: dart and dartaotruntime
	 *
	 * @param sdkLocation Absolute location of Dart SDK
	 * @param name        - The name of the executable
	 * @return The path to the executable valid for the host operating system
	 */
	public String getExecutable(String sdkLocation, String name) {
		return platformFactory.getSdkLocator().resolveExecutablePath(sdkLocation, name);
	}

	/**
	 * Returns the path to a tool within the Dart SDK bin directory in a system
	 * agnostic format. The appropriate file extension, if any, is also accepted eg.
	 * ".bat" on Windows.
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
	public String resolveToolPath(String sdkLocation, String name) {
		return platformFactory.getSdkLocator().resolveToolPath(sdkLocation, name);
	}

	public static PlatformUtil getInstance() {
		if (instance == null) {

			synchronized (PlatformUtil.class) {
				// The following null check is supposedly thread safe when 'instance' is
				// volatile
				if (instance == null)
					instance = new PlatformUtil();
			}
		}

		return instance;
	}

	private static Object getClass(String nameClass) {
		try {
			return (Class.forName(nameClass)).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			LOG.log(DartLog.createError("Class.forName() failed", e)); //$NON-NLS-1$
			e.printStackTrace();
			return null;
		}
	}

}
