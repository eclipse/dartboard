/*******************************************************************************
 * Copyright (c) 2019 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jonas Hungershausen
 *******************************************************************************/
package org.eclipse.dartboard.dart.client;

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
