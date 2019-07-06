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
package org.eclipse.dartboard.launch.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.Messages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class DartConsoleFactory implements IConsoleFactory {

	private InputStream inputStream;

	private InputStream errorStream;

	public DartConsoleFactory(InputStream inputStream, InputStream errorStream) {
		this.inputStream = inputStream;
		this.errorStream = errorStream;
	}

	@Override
	public void openConsole() {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		IOConsole console = new IOConsole(Messages.Console_Name, null);
		IOConsoleOutputStream outputStream = console.newOutputStream();
		IOConsoleOutputStream errorOutputStream = console.newOutputStream();
		errorOutputStream.setColor(new Color(null, 255, 0, 0));

		copy(inputStream, outputStream);
		copy(errorStream, errorOutputStream);

		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
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
				while((data = inputStream.read()) != -1) {
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
