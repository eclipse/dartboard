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
package org.eclipse.dartboard.preferences;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.dartboard.util.PlatformUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * A {@link AbstractPreferenceInitializer} that contains a hook to initialize
 * all preference values.
 *
 * @author Jonas Hungershausen
 *
 */
public class DartPreferenceInitializer extends AbstractPreferenceInitializer {

	private static final ILog LOG = Platform.getLog(DartPreferenceInitializer.class);

	private static boolean warned;

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore scopedPreferenceStore = DartPreferences.getPreferenceStore();
		boolean anySdkFound = false;

		if (scopedPreferenceStore.getString(GlobalConstants.P_SDK_LOCATION_FLUTTER).isEmpty()) {
			Optional<Path> sdkLocation;
			try {
				sdkLocation = getFlutterLocation(); // $NON-NLS-1$
			} catch (ExecutionException e) {
				LOG.log(DartLog.createError("Could not retrieve flutter location", e.getCause())); //$NON-NLS-1$
				sdkLocation = Optional.empty();
			}
			if (sdkLocation.isPresent()) {
				Path sdkPath = sdkLocation.get();
				scopedPreferenceStore.setDefault(GlobalConstants.P_SDK_LOCATION_FLUTTER, sdkPath.toString());
				scopedPreferenceStore.setValue(GlobalConstants.P_FLUTTER_ENABLED, true);
				anySdkFound = true;
				LOG.info("Flutter SDK found at " + sdkPath.toString()); //$NON-NLS-1$
			} else {
				scopedPreferenceStore.setValue(GlobalConstants.P_FLUTTER_ENABLED, false);
				LOG.error("No flutter SDK found"); //$NON-NLS-1$
			}
		} else {
			anySdkFound = true;
		}
		if (scopedPreferenceStore.getString(GlobalConstants.P_SDK_LOCATION_DART).isEmpty()) {
			Optional<Path> sdkLocation;
			try {
				sdkLocation = getDartLocation(); // $NON-NLS-1$
			} catch (ExecutionException e) {
				LOG.log(DartLog.createError("Could not retrieve flutter location", e.getCause())); //$NON-NLS-1$
				sdkLocation = Optional.empty();
			}
			if (sdkLocation.isPresent()) {
				Path sdkPath = sdkLocation.get();
				scopedPreferenceStore.setDefault(GlobalConstants.P_SDK_LOCATION_DART, sdkPath.toString());
				anySdkFound = true;
			}
		} else {
			anySdkFound = true;
		}
		if (!anySdkFound && !warned) {
			MessageDialog.openError(null, Messages.Preference_SDKNotFound_Title, Messages.Preference_SDKNotFound_Body);
		}
		scopedPreferenceStore.setDefault(GlobalConstants.P_SYNC_PUB, true);
		scopedPreferenceStore.setDefault(GlobalConstants.P_OFFLINE_PUB, false);
	}

	/**
	 * Returns a {@link Path} containing the location of the Dart SDK folder.
	 * 
	 * This method finds the location of the Dart SDK on the system, if installed.
	 * On *nix based systems it tries to locate the Dart binary by using the
	 * {@code which} command. Typically the output is a symbolic link to the actual
	 * binary. Since the Dart SDK installation folder contains more binaries that we
	 * need, we resolve the symbolic link and return the path to the parent of the
	 * /bin directory inside the SDK installation folder.
	 * 
	 * On Windows this method uses the where command to locate the binary.
	 * 
	 * @return - An {@link Optional} of {@link Path} containing the path to the
	 *         {@code /bin} folder inside the Dart SDK installation directory or
	 *         empty if the SDK is not found on the host machine.
	 */
	public static Optional<Path> getDartLocation() throws ExecutionException {
		return PlatformUtil.getInstance().getLocation("dart", false); //$NON-NLS-1$
	}

	public static Optional<Path> getFlutterLocation() throws ExecutionException {
		return PlatformUtil.getInstance().getLocation("flutter", true); //$NON-NLS-1$
	}
}
