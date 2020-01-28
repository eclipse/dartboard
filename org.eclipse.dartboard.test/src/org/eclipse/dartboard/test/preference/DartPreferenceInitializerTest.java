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
package org.eclipse.dartboard.test.preference;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.test.util.DefaultPreferences;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
public class DartPreferenceInitializerTest {
	/** Dart SDK location is operating system specific. Here catering for Linuz and Windows */
	private static String DART_SDK_LOC;
	
	private ScopedPreferenceStore preferenceStore;

	@Before
	public void setup() {
		DART_SDK_LOC = Platform.getOS().equals(Platform.OS_WIN32) ? "C:\\Program Files\\Dart\\dart-sdk" : "/usr/lib/dart";
		preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.eclipse.dartboard.dart");
		DefaultPreferences.resetPreferences(preferenceStore);
	}

	@Test
	public void preferenceStore__NormalStartup__CorrectDefaultsAreSet() {
		assertEquals(true, preferenceStore.getBoolean(Constants.PREFERENCES_SYNC_PUB));
		assertEquals(false, preferenceStore.getBoolean(Constants.PREFERENCES_OFFLINE_PUB));

		assertEquals(DART_SDK_LOC, preferenceStore.getString(Constants.PREFERENCES_SDK_LOCATION));
	}

}
