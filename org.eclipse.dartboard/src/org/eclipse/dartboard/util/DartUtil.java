package org.eclipse.dartboard.util;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartUtil {

	static final boolean IS_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	static ScopedPreferenceStore preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE,
			Constants.PLUGIN_ID);

	public static String getExecutable(String name) {
		String dartSdk = preferences.getString(Constants.PREFERENCES_SDK_LOCATION);

		String result = dartSdk + File.separator + "bin" + File.separator + name; //$NON-NLS-1$

		if (IS_WINDOWS) {
			return result + ".exe"; //$NON-NLS-1$
		}
		return result;

	}

}
