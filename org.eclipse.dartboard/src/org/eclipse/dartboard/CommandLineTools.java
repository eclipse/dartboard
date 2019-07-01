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
package org.eclipse.dartboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineTools {

	private static final Logger LOG = LoggerFactory.getLogger(CommandLineTools.class);

	/**
	 * Contains possible dart installation locations
	 */
	// TODO: Add more possible paths (also for macOs, Windows)
	public static final String[] POSSIBLE_DART_LOCATIONS = { "/usr/lib/dart", "/opt/dart-sdk" }; //$NON-NLS-1$ //$NON-NLS-2$

	private CommandLineTools() {
	}

	public static Optional<String> getDartSDKLocation() {
		for (String string : POSSIBLE_DART_LOCATIONS) {
			Optional<String> result = getPath(string);
			if (result.isPresent()) {
				return Optional.of(string);
			}
		}
		return Optional.empty();
	}

	private static Optional<String> getPath(String location) {
		ProcessBuilder builder = new ProcessBuilder("/usr/bin/command", "-v", location + "/bin/dart"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		builder.redirectErrorStream(true);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()))) {
			return Optional.ofNullable(reader.readLine());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return Optional.empty();
	}

}
