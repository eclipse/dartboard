package com.vogella.lsp.flutter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class FlutterLanguageServerStreamProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {


	public FlutterLanguageServerStreamProvider() {
		try {
			URL url = FileLocator.toFileURL(getClass().getResource("/language_server/bin/dart_language_server.dart.snapshot"));
			setCommands(Arrays.asList("/usr/lib/dart/bin/dart", new File(url.getPath()).getAbsolutePath()));
			setWorkingDirectory(System.getProperty("user.dir"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}