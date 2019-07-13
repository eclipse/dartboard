/*******************************************************************************
 * Copyright (c) 2019 vogella GmbH and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Lakshminarayana Nekkanti
 *******************************************************************************/
package org.eclipse.dartboard.util;

import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class Logger {
	static org.eclipse.e4.core.services.log.Logger logger;

	public static org.eclipse.e4.core.services.log.Logger getLogger() {
		if (logger == null) {
			logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
		}
		return logger;
	}

	public static void logError(String message) {
		getLogger().error(message);
	}

	public static void logError(Throwable throwable) {
		getLogger().error(throwable);
	}

	public static void logError(String message, Throwable throwable) {
		getLogger().error(throwable, message);
	}

	public static void logWarning(String message) {
		getLogger().warn(message);
	}

	public static void logWarning(Throwable throwable) {
		getLogger().warn(throwable);
	}

	public static void logWarning(String message, Throwable throwable) {
		getLogger().warn(throwable, message);
	}

	public static void logInfomation(String message) {
		getLogger().info(message);
	}

	public static void logInfomation(Throwable throwable) {
		getLogger().info(throwable);
	}

	public static void logInfomation(String message, Throwable throwable) {
		getLogger().info(throwable, message);
	}
}
