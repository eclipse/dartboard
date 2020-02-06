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
package org.eclipse.dartboard.os.windows;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.eclipse.dartboard.util.SdkLocator;

@SuppressWarnings("nls")
public class PlatformSdkLocator extends SdkLocator {

	public PlatformSdkLocator() {
	}

	@Override
	public String resolveToolPath(String sdkLocation, String name) {
		if (!name.endsWith(".bat"))
			name += ".bat";
		return sdkLocation + File.separator + "bin" + File.separator + name;
	}

	@Override
	public String resolveExecutablePath(String sdkLocation, String name) {
		if (!name.endsWith(".exe"))
			name += ".exe";
		return sdkLocation + File.separator + "bin" + File.separator + name;
	}

	@Override
	public String[] getLocationCommand(String program, boolean interactive) throws ExecutionException {
		return new String[] { "cmd", "/c", "where " + program };
	}


}
