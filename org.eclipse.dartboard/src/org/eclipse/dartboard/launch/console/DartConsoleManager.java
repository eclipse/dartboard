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
 * Lakshminarayana Nekkanti
 *******************************************************************************/
package org.eclipse.dartboard.launch.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.Messages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DartConsoleManager {

	private static DartConsoleManager consoleManager;
	private DartConsole console;

	public static DartConsoleManager getInstance() {
		if (consoleManager == null) {
			consoleManager = new DartConsoleManager();
		}
		return consoleManager;
	}

	public synchronized DartConsole getDartConsole() {
		if (console == null) {
			// temperory image
			ImageDescriptor descripter = AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.console", //$NON-NLS-1$
					"/icons/full/cview16/console_view.png"); //$NON-NLS-1$
			console = new DartConsole(Messages.Console_Name, descripter);
		}
		return console;
	}

	public void openConsole() {
		openConsole(null, null);
	}

	public void openConsole(InputStream inputStream, InputStream errorStream) {
		Display.getDefault().asyncExec(() -> {
			DartConsole console = getDartConsole();

			if (inputStream != null) {
				IOConsoleOutputStream outputStream = console.newOutputStream();
				copy(inputStream, outputStream);
			}

			IOConsoleOutputStream errorOutputStream = console.newOutputStream();
			errorOutputStream.setColor(new Color(null, 255, 0, 0));
			if (errorStream != null) {
				copy(errorStream, errorOutputStream);
			}
			console.openConsole();
		});
	}

	/**
	 * Transfers the contents of an {@link InputStream} asynchronously to an
	 * {@link OutputStream}.
	 * 
	 * This method transfers the contents of a supplied {@link InputStream} to
	 * another {@link OutputStream}. This is done asynchronously so any content
	 * coming into the {@link InputStream} after this method was called will still
	 * be passed onto the {@link OutputStream}
	 * 
	 * @param inputStream  - An {@link InputStream} of which the content should be
	 *                     transferred
	 * @param outputStream - An {@link OutputStream} the data should be written to
	 */
	private void copy(InputStream inputStream, OutputStream outputStream) {

		// TODO: This shows up in the progress widget which is suboptimal. This is not a
		// task the user should see.
		Job job = Job.create("Transferring inputStream to outputStream", runnable -> { //$NON-NLS-1$
			try {
				int data;
				while ((data = inputStream.read()) != -1) {
					outputStream.write(data);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		job.setPriority(Job.LONG);
		job.schedule();
	}
}
