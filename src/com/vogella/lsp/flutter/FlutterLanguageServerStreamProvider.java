package com.vogella.lsp.flutter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class FlutterLanguageServerStreamProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {

	public FlutterLanguageServerStreamProvider() {
		// Replace this with the location on your file system
		String dartLocation = "/home/jhungershausen/Library/dart-nightly";

		List<String> commands = new ArrayList<>();
		commands.add(dartLocation + "/bin/dart");
		commands.add(dartLocation + "/bin/snapshots/analysis_server.dart.snapshot");
		commands.add("--lsp");

		setCommands(commands);

		setWorkingDirectory(System.getProperty("user.dir"));
	}
}