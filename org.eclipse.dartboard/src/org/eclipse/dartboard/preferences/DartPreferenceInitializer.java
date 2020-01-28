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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.dartboard.util.SDKLocator;
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
			Optional<Path> binLocation;
			try {
				binLocation = SDKLocator.getLocation("flutter"); //$NON-NLS-1$
			} catch (IOException | InterruptedException e) {
				LOG.log(DartLog.createError("Could not retrieve flutter location", e)); //$NON-NLS-1$
				binLocation = Optional.empty();
			}
			if (binLocation.isPresent()) {
				Path sdkPath = binLocation.get().getParent();
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
			Optional<Path> binLocation;
			try {
				binLocation = SDKLocator.getLocation("dart"); //$NON-NLS-1$
			} catch (IOException | InterruptedException e) {
				LOG.log(DartLog.createError("Could not retrieve flutter location", e)); //$NON-NLS-1$
				binLocation = Optional.empty();
			}
			if (binLocation.isPresent()) {
				Path sdkPath = binLocation.get().getParent();
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
}
