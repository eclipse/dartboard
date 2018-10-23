package com.vogella.lsp.flutter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class FlutterLanguageServerStreamProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {

	public FlutterLanguageServerStreamProvider() {
		List<String> commands = new ArrayList<>();
		Optional<String> languageServerLocation = PathLocator.getLanguageServerLocation();
		languageServerLocation.ifPresentOrElse(location -> {
			commands.add(location);
		}, () -> {
			// Install the language via pub global activate dart_language_server
		});
		setCommands(commands);
		setWorkingDirectory(System.getProperty("user.dir"));
	}
}