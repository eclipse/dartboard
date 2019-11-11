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

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * A {@link LanguageClient} implementation that handles custom methods of the
 * Dart analysis server.
 * 
 * @author Jonas Hungershausen
 *
 */
public interface DartLanguageClient extends LanguageClient {

	/**
	 * A handler for the $/analyzerStatus notification method.
	 * 
	 * This notification is sent by the analysis server whenever the analyzer status
	 * is changed.
	 * 
	 * @param params - The data that was contained in the notification
	 */
	@JsonNotification("$/analyzerStatus")
	void analyzerStatus(AnalyzerStatusParams params);
}
