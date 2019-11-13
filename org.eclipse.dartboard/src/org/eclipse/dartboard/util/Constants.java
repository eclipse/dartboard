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
public abstract class Constants {

	private Constants() {
	}

	public static final String PLUGIN_ID = "org.eclipse.dartboard";

	/**
	 * ID of the extension point used to register special pub services
	 */
	public static final String PUB_SERVICE_EXTENSION_POINT = PLUGIN_ID + ".pubService";

	/**
	 * Name of the pubspec file
	 */
	public static final String PUBSPEC_YAML = "pubspec.yaml";

}
