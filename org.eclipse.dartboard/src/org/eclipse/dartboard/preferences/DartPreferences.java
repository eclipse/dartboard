package org.eclipse.dartboard.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartPreferences {

	private DartPreferences() {
	}

	private static Map<String, ScopedPreferenceStore> prefs = new HashMap<>();

	/**
	 * Returns the current {@link IPreferenceStore} for the plugin.
	 * 
	 * If there currently is no preference store instantiated (e.g. first access to
	 * the store), it is instantiated.
	 * 
	 * @return the current {@link IPreferenceStore}
	 */

	// TODO Drop this method once we can use 2019-12 as PlatformUI provides a
	// similar method for that.
	public static ScopedPreferenceStore getPreferenceStore(String pluginId) {
		if (!prefs.containsKey(pluginId)) {
			ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, pluginId);
			prefs.put(pluginId, preferenceStore);
		}
		return prefs.get(pluginId);
	}

}
