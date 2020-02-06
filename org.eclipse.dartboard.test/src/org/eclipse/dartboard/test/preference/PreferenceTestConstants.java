/*******************************************************************************
 * Copyright (c) 2020 vogella GmbH and others.
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

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.eclipse.dartboard.util.PlatformUtil;

public class PreferenceTestConstants {

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static final String DEFAULT_DART_LOCATION;
	public static final String INVALID_SDK_LOCATION = "some-random-test-location/path-segment";

	public static final String DEFAULT_FLUTTER_LOCATION;

	static {
		boolean isWindows = OS.indexOf("win") >= 0;
		DEFAULT_DART_LOCATION = isWindows ? "C:\\Program Files\\Dart\\dart-sdk" : "/usr/lib/dart";
		Optional<Path> flutterSdkLocation = null;
		try {
			flutterSdkLocation = PlatformUtil.getInstance().getLocation("flutter");
		} catch (ExecutionException e) {
		}
		boolean isFlutterAvailable = (flutterSdkLocation != null) && flutterSdkLocation.isPresent();
		DEFAULT_FLUTTER_LOCATION = isFlutterAvailable ? flutterSdkLocation.get().toString() : null;
	}
}