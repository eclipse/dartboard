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
package org.eclipse.dartboard;

public abstract class Constants {

	private Constants() {
	}

	public static final String PREFERENCES_SDK_LOCATION = "sdk_location";

	public static final String PREFERENCES_KEY = "com.vogella.dart.preferences";

	public static final String LAUNCH_MAIN_CLASS = "main_class";

	public static final String LAUNCH_SELECTED_PROJECT = "selected_project";

	public static final String CONSOLE_NAME = "Dart Terminal";
}
