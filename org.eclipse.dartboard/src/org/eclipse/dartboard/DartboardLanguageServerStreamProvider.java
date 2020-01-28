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

import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartboardLanguageServerStreamProvider extends ProcessStreamConnectionProvider
		implements StreamConnectionProvider {

	public DartboardLanguageServerStreamProvider() {
		ScopedPreferenceStore globalPreferences = DartPreferences.getPreferenceStore();
		List<String> commands = new ArrayList<>();


		String dartLocation;
		if (globalPreferences.getBoolean(GlobalConstants.FLUTTER_ENABLED)) {
			String flutterSDK = globalPreferences.getString(GlobalConstants.P_SDK_LOCATION_FLUTTER);
			dartLocation = flutterSDK + "/bin/cache/dart-sdk"; //$NON-NLS-1$
		} else {
			dartLocation = globalPreferences.getString(GlobalConstants.P_SDK_LOCATION_DART);
		}
		commands.add(dartLocation + "/bin/dart"); //$NON-NLS-1$
		commands.add(dartLocation + "/bin/snapshots/analysis_server.dart.snapshot"); //$NON-NLS-1$
		setWorkingDirectory(System.getProperty("user.dir")); //$NON-NLS-1$
		setCommands(commands);
		commands.add("--lsp"); //$NON-NLS-1$
	}

}