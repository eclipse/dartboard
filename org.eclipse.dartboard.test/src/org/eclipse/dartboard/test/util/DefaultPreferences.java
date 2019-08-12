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
package org.eclipse.dartboard.test.util;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DefaultPreferences {

	public static void resetPreferences(ScopedPreferenceStore preferenceStore) {
		if (preferenceStore == null) {
			preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.eclipse.dartboard");
		}

		preferenceStore.setToDefault(Constants.PREFERENCES_OFFLINE_PUB);
		preferenceStore.setToDefault(Constants.PREFERENCES_SDK_LOCATION);
		preferenceStore.setToDefault(Constants.PREFERENCES_SYNC_PUB);
	}

	public static void resetPreferences() {
		resetPreferences(null);
	}
}
