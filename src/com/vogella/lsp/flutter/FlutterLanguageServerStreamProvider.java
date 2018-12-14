package com.vogella.lsp.flutter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class FlutterLanguageServerStreamProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {

	public FlutterLanguageServerStreamProvider() {
		String userDir = System.getProperty("user.dir");
		String dartLocation = userDir + "/Library/dart-sdk/bin";

		List<String> commands = new ArrayList<>();
		commands.add(dartLocation + "/dart");
		commands.add(dartLocation + "/snapshots/analysis_server.dart.snapshot");
		commands.add("--lsp");

		setCommands(commands);

		setWorkingDirectory(System.getProperty("user.dir"));
	}
}