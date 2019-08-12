package org.eclipse.dartboard.util;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartPreferences {

	private DartPreferences() {
	}

	private static ScopedPreferenceStore preferenceStore;

	/**
	 * Returns the current {@link IPreferenceStore} for the plugin.
	 * 
	 * If there currently is no preference store instantiated (e.g. first access to
	 * the store), it is instantiated.
	 * 
	 * @return the current {@link IPreferenceStore}
	 */
	public static ScopedPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID);
		}
		return preferenceStore;
	}

}
