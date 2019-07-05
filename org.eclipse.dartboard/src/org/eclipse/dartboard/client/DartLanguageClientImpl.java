package org.eclipse.dartboard.client;

import org.eclipse.lsp4e.LanguageClientImpl;

@SuppressWarnings("restriction")
public class DartLanguageClientImpl extends LanguageClientImpl implements DartLanguageClient {

	@Override
	public void analyzerStatus(AnalyzerStatusParams params) {
		// TODO: Handle the result of isAnalyzing
	}
}
