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
package org.eclipse.dartboard.os.linux;

import java.io.File;
import java.util.List;

import org.eclipse.dartboard.util.DartSdkChecker;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.Lists;

/**
 * Checks if a given Linux location contains a Dart SDK
 *
 * @author Andrew Bowley
 *
 */
@SuppressWarnings("nls")
public class PlatformDartSdkChecker extends DartSdkChecker {

	/**
	 * Construct LinuxDartSdkChecker object
	 *
	 * @param shell     Parent shell of owner or null if none
	 * @param isFlutter Flag set true if Dart SDK is inside Flutter
	 */
	public PlatformDartSdkChecker(Shell shell, boolean isFlutter) {
		super(shell, !isFlutter ? "bin"
				: "bin" + File.separator + "cache" + File.separator + "dart-sdk" + File.separator + "bin");
	}

	@Override
	public String[] getDartVersionCommands(String executablePath) {
		return new String[] { "/bin/bash", "-c", executablePath + " --version" };
	}

	@Override
	public List<String> getBlacklist() {
		return Lists.newArrayList("/bin/dart", "/usr/bin/dart");
	}

	@Override
	public String getDartExecutable() {
		return "dart"; //$NON-NLS-1$
	}
}
