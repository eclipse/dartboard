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
package org.eclipse.dartboard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartLanguageServerStreamProvider extends ProcessStreamConnectionProvider
		implements StreamConnectionProvider {

	public DartLanguageServerStreamProvider() {
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				Constants.PLUGIN_ID);

		String dartLocation = scopedPreferenceStore.getString(Constants.PREFERENCES_SDK_LOCATION);

		List<String> commands = new ArrayList<>();
		commands.add(dartLocation + "/bin/dart"); //$NON-NLS-1$
		commands.add(dartLocation + "/bin/snapshots/analysis_server.dart.snapshot"); //$NON-NLS-1$
		commands.add("--lsp"); //$NON-NLS-1$

		setCommands(commands);

		setWorkingDirectory(System.getProperty("user.dir")); //$NON-NLS-1$
	}
}