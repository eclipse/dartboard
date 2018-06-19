package com.vogella.lsp.flutter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class FlutterLanguageServerStreamProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {


	public FlutterLanguageServerStreamProvider() {
		String dartLocation = DartPathLocator.getDartSDKLocation();
		List<String> commands = new ArrayList<>();
		commands.add(dartLocation);
		try {
			URL url = FileLocator.toFileURL(getClass().getResource("/language_server/bin/dart_language_server.dart.snapshot"));
			commands.add(new File(url.getPath()).getAbsolutePath());
			setCommands(commands);
			setWorkingDirectory(System.getProperty("user.dir"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}