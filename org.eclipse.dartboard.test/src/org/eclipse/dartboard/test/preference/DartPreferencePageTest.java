package org.eclipse.dartboard.test.preference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.test.util.DefaultPreferences;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.preference.PreferenceDialog;
import org.eclipse.reddeer.jface.preference.PreferencePage;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
public class DartPreferencePageTest {

	private PreferenceDialog preferenceDialog;
	private DartPreferencePage preferencePage;

	@Before
	public void setup() {
		preferenceDialog = new WorkbenchPreferenceDialog();
		preferencePage = new DartPreferencePage(preferenceDialog);
		preferenceDialog.open();
		preferenceDialog.select(preferencePage);
	}

	@After
	public void tearDown() {
		DefaultPreferences.resetPreferences();
		if (preferenceDialog.isOpen()) {
			preferenceDialog.cancel();
		}

	}

	@Test
	public void dartPreferencePage__DefaultPreferences__CorrectDefaultsAreDisplayed() throws Exception {
		assertEquals(preferencePage.isAutoPubSynchronization(), true);
		assertEquals(preferencePage.isUseOfflinePub(), false);

		assertEquals(preferencePage.getSDKLocation(), "/usr/lib/dart");
	}

	@Test
	public void dartPreferencePage__InvalidSDKLocation__PageIsNotValid() throws Exception {
		preferencePage.setSDKLocation("some-random-test-location/path-segment");
		assertTrue(preferencePage.isShowingSDKInvalidError());
	}

	@Test
	public void dartPreferencePage__ChangePubPreferences__UpdatedPreferencesAreSaved() throws Exception {
		preferencePage.setAutoPubSynchronization(false);
		preferencePage.setUseOfflinePub(true);

		preferencePage.apply();

		ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"org.eclipse.dartboard");

		assertEquals(preferenceStore.getBoolean(Constants.PREFERENCES_OFFLINE_PUB), true);
		assertEquals(preferenceStore.getBoolean(Constants.PREFERENCES_SYNC_PUB), false);
	}

	public class DartPreferencePage extends PreferencePage {

		public DartPreferencePage(ReferencedComposite referencedComposite) {
			super(referencedComposite, "Dart");
		}

		public DartPreferencePage setSDKLocation(String text) {
			new LabeledText("Dart SDK Location:").setText(text);
			return this;
		}

		public String getSDKLocation() {
			return new LabeledText("Dart SDK Location:").getText();
		}

		public DartPreferencePage setAutoPubSynchronization(boolean value) {
			new CheckBox("Automatic Pub dependency synchronization").toggle(value);
			return this;
		}

		public boolean isAutoPubSynchronization() {
			return new CheckBox("Automatic Pub dependency synchronization").isChecked();
		}

		public DartPreferencePage setUseOfflinePub(boolean value) {
			new CheckBox("Use cached packages (--offline flag)").toggle(value);
			return this;
		}

		public boolean isUseOfflinePub() {
			return new CheckBox("Use cached packages (--offline flag)").isChecked();
		}

		public boolean isShowingSDKInvalidError() {
			try {
				new DefaultText("Not a valid SDK location");
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

}
