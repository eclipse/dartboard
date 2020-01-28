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
package org.eclipse.dartboard.flutter;


public abstract class FlutterConstants {

	private FlutterConstants() {
	}

	/**
	 * Plugin ID of the plugin
	 */
	public static final String PLUGIN_ID = "org.eclipse.dartboard.flutter"; //$NON-NLS-1$

	/**
	 * Key for the main class of a launch configuration
	 */
	public static final String LAUNCH_MAIN_CLASS = "main_class"; //$NON-NLS-1$

	/**
	 * Key for the selected project of a launch configuration
	 */
	public static final String LAUNCH_SELECTED_PROJECT = "selected_project"; //$NON-NLS-1$

	/**
	 * The ID of {@link LaunchConfig} inside the IDE
	 */
	public static final String LAUNCH_CONFIGURATION_ID = "org.eclipse.dartboard.launch"; //$NON-NLS-1$

	/**
	 * The ID of the Dart launch group
	 */
	public static final String LAUNCH_GROUP = "org.eclipse.dartboard.launchGroup"; //$NON-NLS-1$

	/**
	 * Name of the PUB_ENVIRONMENT variable used by pub
	 */
	public static final String PUB_ENVIRONMENT_VARIABLE = "PUB_ENVIRONMENT"; //$NON-NLS-1$

}
