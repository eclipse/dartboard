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
 *     Jonas Hungershausen
 *******************************************************************************/
package org.eclipse.dartboard.flutter.util;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author jonas
 *
 */
public class FlutterUtil {

	public static final boolean IS_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	private static ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore();

	public static String getDefaultSDKPath() {
		return preferences.getString(GlobalConstants.P_SDK_LOCATION_FLUTTER);
	}

	public static String getFutterTool() {
		return IS_WINDOWS ? "flutter.bat" : "flutter";
	}

	public static String getFlutterToolPath(String sdkPath) {
		return sdkPath + File.separator + "bin" + File.separator + FlutterUtil.getFutterTool();
	}
}
