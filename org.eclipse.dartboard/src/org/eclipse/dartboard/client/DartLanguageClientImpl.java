package org.eclipse.dartboard.client;

import org.eclipse.lsp4e.LanguageClientImpl;

@SuppressWarnings("restriction")
public class DartLanguageClientImpl extends LanguageClientImpl implements DartLanguageClient {

	@Override
	public void analyzerStatus(AnalyzerStatusParams params) {
		// TODO: Add indicator to the UI to show that the server is analyzing.
		// Since the analyzer is very fast, the progress indicator is not visible at all
		// since the notification that analyzing has finished is sent almost instantly.
		// This should be tested in a larger codebase.
	}
}
