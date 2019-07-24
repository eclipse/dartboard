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
package org.eclipse.dartboard.preference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.DartUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link AbstractPreferenceInitializer} that contains a hook to initialize
 * all preference values.
 * 
 * @author Jonas Hungershausen
 *
 */
public class DartPreferenceInitializer extends AbstractPreferenceInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(DartPreferenceInitializer.class);

	private static boolean warned;

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				Constants.PLUGIN_ID);

		if (scopedPreferenceStore.getString(Constants.PREFERENCES_SDK_LOCATION).isEmpty()) {
			Optional<Path> binLocation = getDartLocation();
			if (binLocation.isPresent()) {
				Path sdkPath = binLocation.get().getParent();
				scopedPreferenceStore.setDefault(Constants.PREFERENCES_SDK_LOCATION, sdkPath.toString());
			} else if (!warned) {
				MessageDialog.openError(null, Messages.Preference_SDKNotFound_Title,
						Messages.Preference_SDKNotFound_Body);
			}
		}
		scopedPreferenceStore.setDefault(Constants.PREFERENCES_SYNC_PUB, true);
		scopedPreferenceStore.setDefault(Constants.PREFERENCES_OFFLINE_PUB, false);
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
	public Optional<Path> getDartLocation() {
		Path path = null; // $NON-NLS-1$
		String[] command;
		if (DartUtil.IS_WINDOWS) {
			command = new String[] { "cmd", "/c", "where dart" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			command = new String[] { "/bin/bash", "-c", "which dart" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		try {
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
		} catch (IOException | InterruptedException e) {
			LOG.error("Could not locate Dart SDK location.", e); //$NON-NLS-1$
		}

		// TODO: Try different default installs (need to collect them)
		return Optional.ofNullable(path);
	}
}
