package com.vogella.eclipsedart.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.vogella.eclipsedart.Constants;
import com.vogella.eclipsedart.CommandLineTools;

public class DartPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = DefaultScope.INSTANCE.getNode(Constants.PREFERENCES_KEY);

		CommandLineTools.getDartSDKLocation().ifPresent(location -> {
			preferences.put(Constants.PREFERENCES_SDK_LOCATION, location);
		});
		
	}

	
}
