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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.eclipse.dartboard.util.SdkLocator;

public class PlatformSdkLocator extends SdkLocator {

	public PlatformSdkLocator() {
	}

	@Override
	public String resolveToolPath(String sdkLocation, String name) {
		return sdkLocation + File.separator + "bin" + File.separator + name; //$NON-NLS-1$
	}

	@Override
	public String resolveExecutablePath(String sdkLocation, String name) {
		return resolveToolPath(sdkLocation, name);
	}

	@Override
	public String[] getLocationCommand(String program, boolean interactive) throws ExecutionException {
		String shell = null;
		try {
			shell = getShell();
		} catch (IOException | InterruptedException e) {
			throw new ExecutionException("Error obtaining shell command", e); //$NON-NLS-1$
		}
		if (interactive) {
			return new String[] { shell, "-i", "-c", "which " + program }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			return new String[] { shell, "-c", "which " + program }; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static String getShell() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "echo $SHELL"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Process process = builder.start();
		process.waitFor();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String location = reader.readLine();
			return location;
		}
	}

}
