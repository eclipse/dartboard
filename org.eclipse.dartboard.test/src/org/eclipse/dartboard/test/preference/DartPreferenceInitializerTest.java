package org.eclipse.dartboard.test.preference;

import static org.junit.Assert.assertEquals;

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

	private ScopedPreferenceStore preferenceStore;

	@Before
	public void setup() {
		DefaultPreferences.resetPreferences(preferenceStore);

		preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.eclipse.dartboard");
	}

	@Test
	public void preferenceStore__NormalStartup__CorrectDefaultsAreSet() {
		assertEquals(preferenceStore.getBoolean(Constants.PREFERENCES_SYNC_PUB), true);
		assertEquals(preferenceStore.getBoolean(Constants.PREFERENCES_OFFLINE_PUB), false);

		assertEquals(preferenceStore.getString(Constants.PREFERENCES_SDK_LOCATION), "/usr/lib/dart");
	}

}
