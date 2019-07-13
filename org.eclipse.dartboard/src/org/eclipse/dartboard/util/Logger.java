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

import java.lang.reflect.Field;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.Constants;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
public class Logger {
	static org.eclipse.e4.core.services.log.Logger logger;

	private static org.eclipse.e4.core.services.log.Logger getLogger() {
		if (logger == null) {
			logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
			Bundle bundle = Platform.getBundle("org.eclipse.e4.ui.workbench"); //$NON-NLS-1$
			if (bundle != null) {
				try {
					final Class<?> loggerClass = bundle
							.loadClass("org.eclipse.e4.ui.internal.workbench.WorkbenchLogger"); //$NON-NLS-1$
					final Field field = loggerClass.getDeclaredField("bundleName"); //$NON-NLS-1$
					field.setAccessible(true);
					field.set(logger, Constants.PLUGIN_ID);
				} catch (Exception e) {
					logError(e);
				}
			}
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
