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
 *     Lakshminarayana Nekkanti 
 *******************************************************************************/
package org.eclipse.dartboard.util;

import java.text.MessageFormat;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dartboard.DartBoardPlugin;

public class Logger {
	public static final int DEFAULT_SEVERITY = IStatus.ERROR;

	public static void log(String message) {
		log(message, null, DEFAULT_SEVERITY);
	}

	public static void log(String message, String... params) {
		log(MessageFormat.format(message, (Object[]) params), null, DEFAULT_SEVERITY);
	}

	public static void log(String message, int severity) {
		log(message, null, severity);
	}

	public static void log(Throwable e) {
		log(e.getMessage(), e);
	}

	public static void log(String message, Throwable e) {
		log(message, e, DEFAULT_SEVERITY);
	}

	public static void log(String message, Throwable t, String... params) {
		log(MessageFormat.format(message, (Object[]) params), t, DEFAULT_SEVERITY);
	}

	public static void log(String message, Throwable e, int severity) {
		if (severity < IStatus.OK || severity > IStatus.CANCEL) {
			severity = DEFAULT_SEVERITY;
		}
		getLog().log(StatusUtil.newStatus(severity, message, e));
	}

	public static void log(IStatus status) {
		getLog().log(status);
	}

	private static ILog getLog() {
		return DartBoardPlugin.getDefault().getLog();
	}
}
