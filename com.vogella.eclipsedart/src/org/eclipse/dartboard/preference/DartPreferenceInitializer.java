package org.eclipse.dartboard.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.CommandLineTools;
import org.eclipse.dartboard.Constants;

public class DartPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEY);

		CommandLineTools.getDartSDKLocation().ifPresent(location -> {
			preferences.put(Constants.PREFERENCES_SDK_LOCATION, location);
		});

	}

}
