package org.eclipse.dartboard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class FlutterLanguageServerStreamProvider extends ProcessStreamConnectionProvider
		implements StreamConnectionProvider {

	public FlutterLanguageServerStreamProvider() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEY);

		String dartLocation = preferences.get(Constants.PREFERENCES_SDK_LOCATION, "");

		List<String> commands = new ArrayList<>();
		commands.add(dartLocation + "/bin/dart");
		commands.add(dartLocation + "/bin/snapshots/analysis_server.dart.snapshot");
		commands.add("--lsp");

		setCommands(commands);

		setWorkingDirectory(System.getProperty("user.dir"));
	}
}