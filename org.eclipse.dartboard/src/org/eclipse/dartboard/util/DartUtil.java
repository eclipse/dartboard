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

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartUtil {

	private DartUtil() {
	}

	public static final boolean IS_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	static ScopedPreferenceStore preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID);

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
}
