package org.eclipse.dartboard.logging;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;

public class DartLog {

	// TODO might get obsolete with
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=550399 in 2019-12
	private DartLog() {
	}

	public static IStatus newStatus(int severity, String message, Throwable exception) {
		String statusMessage = message;
		if (message == null || message.trim().isEmpty()) {
			if (exception == null) {
				throw new IllegalArgumentException();
			} else {
				statusMessage = exception.getMessage() == null ? exception.toString() : exception.getMessage();
			}
		}
		return new Status(severity, GlobalConstants.BASE_PLUGIN_ID, severity, statusMessage, exception);
	}

	public static IStatus createError(Throwable exception) {
		return newStatus(IStatus.ERROR, "", exception); //$NON-NLS-1$
	}

	public static IStatus createWarning(Throwable exception) {
		return newStatus(IStatus.WARNING, "", exception); //$NON-NLS-1$
	}

	public static IStatus createError(String message, Throwable exception) {
		return newStatus(IStatus.ERROR, message, exception);
	}

	public static IStatus createWarning(String message, Throwable exception) {
		return newStatus(IStatus.WARNING, message, exception);
	}

	public static IStatus newStatus(int severity, String message) {
		return new Status(severity, GlobalConstants.BASE_PLUGIN_ID, message);
	}

	public static IStatus createError(String message) {
		return newStatus(IStatus.ERROR, message);
	}

	public static IStatus createWarning(String message) {
		return newStatus(IStatus.WARNING, message);
	}

	public static void throwCoreException(String message) throws CoreException {
		throw new CoreException(createError(message));
	}

	public static void applyToStatusLine(DialogPage page, IStatus status) {
		String message = status.getMessage();
		switch (status.getSeverity()) {
		case IStatus.OK:
			page.setMessage(message, IMessageProvider.NONE);
			page.setErrorMessage(null);
			break;
		case IStatus.WARNING:
			page.setMessage(message, IMessageProvider.WARNING);
			page.setErrorMessage(null);
			break;
		case IStatus.INFO:
			page.setMessage(message, IMessageProvider.INFORMATION);
			page.setErrorMessage(null);
			break;
		default:
			if (message.isEmpty()) {
				message = null;
			}
			page.setMessage(null);
			page.setErrorMessage(message);
			break;
		}
	}
}
