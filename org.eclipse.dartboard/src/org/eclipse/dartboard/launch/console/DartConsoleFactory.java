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

import org.eclipse.ui.console.IConsoleFactory;

public class DartConsoleFactory implements IConsoleFactory {

	@Override
	public void openConsole() {
		DartConsoleManager.getInstance().openConsole();
	}
}
