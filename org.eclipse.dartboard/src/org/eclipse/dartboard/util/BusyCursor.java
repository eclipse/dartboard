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
 *     Andrew Bowley 
 *******************************************************************************/
package org.eclipse.dartboard.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Shows a Busy Cursor during a long running process. Based on SWT BusyIndicator
 * example.
 * 
 * @author Andrew Bowley
 */
public class BusyCursor {

	/**
	 * Runnable which shows busy cursor pending arrival of a return object.
	 * @param <T> the return object type
	 */
	static final class BusyRunnable<T> implements Runnable {

		private final Shell shell;
		private final Callable<T> supplier;
		private T value;
		private Throwable caught;
		private volatile boolean terminate;

		public BusyRunnable(Shell shell, Callable<T> supplier) {
			this.shell = shell;
			this.supplier = supplier;
			terminate = false;
		}

		public T getValue() {
			return value;
		}

		public Throwable getCaught() {
			return caught;
		}

		@Override
		public void run() {
			Display display = shell.getDisplay();
			Thread thread = new Thread(() -> {
				try {
					value = supplier.call();
					terminate = true;
				} catch (Exception e) {
					caught = e;
				}
				display.wake();
			});
			thread.start();
			while (!terminate && (caught == null) && !shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			if (shell.isDisposed())
				caught = new IllegalStateException("Task cancelled by user"); //$NON-NLS-1$
		}
	}

	/**
	 * Runnable to execute busy cursor display
	 */
	static final class SyncRunner<T> implements Runnable {

		private BusyRunnable<T> busyRunnable;
		private Display display;

		public SyncRunner(BusyRunnable<T> busyRunnable, Display display) {
			this.busyRunnable = busyRunnable;
			this.display = display;
		}

		@Override
		public void run() {
			BusyIndicator.showWhile(display, busyRunnable);
		}

	}

	/** Temporary shell created when active is not available  */
	private Shell ownShell;
	/** Active shell of application. May be null if one not yet created */
	private Shell activeShell;

	/**
	 * Construct BusyCursor object
	 * @param shell Active shell or null if not available
	 */
	public BusyCursor(Shell shell) {
		if (shell == null) {
			// Create temporary shell to use until the shell is updated by calling #setShell()
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					ownShell = new Shell(SWT.TOOL | SWT.NO_TRIM);
				}});
		} else {
			activeShell = shell;
		}
	}

	/**
	 * Sets shell for case active shell only available post-construction
	 * @param shell
	 */
	public void setShell(Shell shell) {
		if (ownShell != null) {
			ownShell.getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					ownShell.close();
				}});
			ownShell = null;
		}
		activeShell = shell;
    }

	/**
	 * Wait for object to be supplied calling from non-UI thread
	 * @param <T> Object type
	 * @param supplier Object supplier
	 * @return object
	 * @throws ExecutionException
	 */
	public <T> T syncWaitForObject(Callable<T> supplier) throws ExecutionException {
		Shell shell = activeShell == null ? ownShell : activeShell;
		BusyRunnable<T> busyRunnable = new BusyRunnable<>(shell, supplier);
		Display display = shell.getDisplay();
		display.syncExec(new SyncRunner<>(busyRunnable, display));
		if (busyRunnable.getCaught() != null)
			throw new ExecutionException("Task failed", busyRunnable.getCaught()); //$NON-NLS-1$
		return busyRunnable.getValue();
	}

	/**
	 * Wait for object to be supplied calling from UI thread
	 * @param <T> Object type
	 * @param supplier Object supplier
	 * @return object
	 * @throws ExecutionException
	 */
	public <T> T waitForObject(Callable<T> supplier) throws ExecutionException {
		Shell shell = activeShell == null ? ownShell : activeShell;
		BusyRunnable<T> busyRunnable = new BusyRunnable<>(shell, supplier);
		BusyIndicator.showWhile(shell.getDisplay(), busyRunnable);
		if (busyRunnable.getCaught() != null)
			throw new ExecutionException("Task failed", busyRunnable.getCaught()); //$NON-NLS-1$
		return busyRunnable.getValue();
	}
}
