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
package org.eclipse.dartboard.util;


@SuppressWarnings("nls")
public abstract class GlobalConstants {

	private GlobalConstants() {
	}

	public static final String BASE_PLUGIN_ID = "org.eclipse.dartboard";

	/**
	 * ID of the extension point used to register special pub services
	 */
	public static final String PUB_SERVICE_EXTENSION_POINT = BASE_PLUGIN_ID + ".pubService";

	/**
	 * Name of the pubspec file
	 */
	public static final String PUBSPEC_YAML = "pubspec.yaml";

	public static final String P_FLUTTER_ENABLED = "flutter.enabled";

	public static final String P_SDK_LOCATION_FLUTTER = "sdk.location.flutter";
	public static final String P_SDK_LOCATION_DART = "sdk.location.dart";

	/**
	 * Preferences key for the option to automatically sync pub dependencies on
	 * pubspec.yaml file save.
	 */
	public static final String P_SYNC_PUB = "auto_pub_sync"; //$NON-NLS-1$

	/**
	 * Preferences key for the --offline flag to pub get operations
	 */
	public static final String P_OFFLINE_PUB = "offline_pub"; //$NON-NLS-1$

}
