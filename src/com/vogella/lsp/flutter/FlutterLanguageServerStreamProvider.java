package com.vogella.lsp.flutter;

import java.io.File;
import java.util.Arrays;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class FlutterLanguageServerStreamProvider extends ProcessStreamConnectionProvider implements StreamConnectionProvider {


	public FlutterLanguageServerStreamProvider() {
		super(
			Arrays.asList("/bin/sh", "/home/jhungershausen/.pub-cache/bin/dart_language_server"),
			new File(".").getAbsolutePath());
	}
	
}