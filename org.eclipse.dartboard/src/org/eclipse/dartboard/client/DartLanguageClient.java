package org.eclipse.dartboard.client;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;

public interface DartLanguageClient extends LanguageClient {

	@JsonNotification("$/analyzerStatus")
	void analyzerStatus(AnalyzerStatusParams params);
}
