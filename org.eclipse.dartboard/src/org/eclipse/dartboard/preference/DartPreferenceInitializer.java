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

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.DartUtil;
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

	private static boolean warned;

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				Constants.PLUGIN_ID);

		if (scopedPreferenceStore.getString(Constants.PREFERENCES_SDK_LOCATION).isEmpty()) {
			Optional<Path> binLocation = DartUtil.getDartLocation();
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
}
