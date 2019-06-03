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

import org.apache.commons.io.IOUtils;
import org.eclipse.dartboard.Constants;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DartConsoleFactory implements IConsoleFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DartConsoleFactory.class);
	
	private InputStream inputStream;

	public DartConsoleFactory(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void openConsole() {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		IOConsole console = new IOConsole(Constants.CONSOLE_NAME, null);
		IOConsoleOutputStream outputSteam = console.newOutputStream();

		try {
			IOUtils.copy(inputStream, outputSteam);
		} catch (IOException ioException) {
			LOG.error(ioException.getMessage());
		}

		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
	}

}
