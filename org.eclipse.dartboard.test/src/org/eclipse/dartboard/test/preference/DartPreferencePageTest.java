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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.eclipse.dartboard.test.util.DefaultPreferences;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.preference.PreferenceDialog;
import org.eclipse.reddeer.jface.preference.PreferencePage;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.clabel.DefaultCLabel;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
public class DartPreferencePageTest {

	static private final String DIALOG_TITLE = "Dart and Flutter";
	/**
	 * Dart SDK location is operating system specific. Here catering for Linux and
	 * Windows
	 */
	private PreferenceDialog preferenceDialog;
	private DartPreferencePage preferencePage;

	@Before
	public void setup() {
		boolean firstTime = preferenceDialog == null;
		if (firstTime) {// First time - clear settings from previous test session
			DefaultPreferences.resetPreferences();
		}

		preferenceDialog = new WorkbenchPreferenceDialog();
		if (firstTime) {
			// Make sure preferences dialog is closed before test commences
			if (preferenceDialog.isOpen()) {
				preferenceDialog.cancel();
			}
		}
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

	public void doAllTests() throws Exception {
		dartPreferencePage__DefaultPreferences__CorrectDefaultsAreDisplayed();
		dartPreferencePage__InvalidToValidSDKLocation_PageIsNotValidThenOk();
	}

	@Test
	public void dartPreferencePage__DefaultPreferences__CorrectDefaultsAreDisplayed() throws Exception {
		assumeTrue(PreferenceTestConstants.DEFAULT_FLUTTER_LOCATION != null);
		assertEquals(PreferenceTestConstants.DEFAULT_FLUTTER_LOCATION, preferencePage.getFlutterSDKLocation());
		assertTrue("Auto pub synchronization not selected", preferencePage.isAutoPubSynchronization());
		assertFalse("Use offline pub is selected", preferencePage.isUseOfflinePub());
		preferencePage.setPluginMode("Dart");

		assertEquals(PreferenceTestConstants.DEFAULT_DART_LOCATION, preferencePage.getDartSDKLocation());
	}

	@Test
	public void	dartPreferencePage__InvalidToValidSDKLocation_PageIsNotValidThenOk() throws Exception {
		preferencePage.setPluginMode("Dart");
		String result = preferencePage.getDartSDKLocation();
		assertTrue(PreferenceTestConstants.DEFAULT_DART_LOCATION.equals(result));
		// Change away from default so it can be changed back
		preferencePage.setSDKLocation(PreferenceTestConstants.INVALID_SDK_LOCATION);
		assertTrue(preferencePage.isShowingSDKInvalidError());
		// Always leave an open preference page set to a valid value or a modal dialog pops up
		// warning the page has an invalid value. RedDeer is unable to close the modal
		// dialog because it is native.
		preferencePage.setSDKLocation(PreferenceTestConstants.DEFAULT_DART_LOCATION);
		new WaitUntil(new WaitForValidState(preferencePage, DIALOG_TITLE));
		result = preferencePage.getDartSDKLocation();
		assertTrue(PreferenceTestConstants.DEFAULT_DART_LOCATION.equals(result));
	}


	public class DartPreferencePage extends PreferencePage implements ValidPreferenceState {

		public DartPreferencePage(ReferencedComposite referencedComposite) {
			super(referencedComposite, DIALOG_TITLE);
		}

		public DartPreferencePage setSDKLocation(String text) {
			new LabeledText("Dart SDK Location:").setText(text);
			return this;
		}

		public String getDartSDKLocation() {
			return new LabeledText("Dart SDK Location:").getText();
		}

		public String getFlutterSDKLocation() {
			return new LabeledText("Flutter SDK Location:").getText();
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

		public DartPreferencePage setPluginMode(String value) {
			new RadioButton(value).click();
			return this;
		}

		public boolean isFlutterMode() {
			return new RadioButton("Flutter").isSelected();
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
		
		@Override
		public boolean isValid() {
			try {
				new DefaultCLabel(DIALOG_TITLE);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
}
