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

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

public class DartConsole extends IOConsole {

	public DartConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
	}

	public void openConsole() {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		boolean exists = Arrays.stream(manager.getConsoles()).filter(element -> element == this).findFirst()
				.isPresent();
		if (!exists) {
			manager.addConsoles(new IConsole[] { this });
		}
		manager.showConsoleView(this);
	}
}
