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

/**
 * A data class that contains information sent in the $/analyzerStatus method of
 * the analysis server.
 * 
 * @author Jonas Hungershausen
 *
 */
public class AnalyzerStatusParams {

	private boolean isAnalyzing;

	/**
	 * Indicates whether the analyzer is currently analyzing.
	 * 
	 * @return <code>true</code> if the analyzer is analyzing, <code>false</code> if
	 *         not.
	 */
	public boolean isAnalyzing() {
		return isAnalyzing;
	}

	/**
	 * Sets the current analyzer status
	 * 
	 * @param isAnalyzing - a {@link Boolean} indicating wether the analyzer is
	 *                    currently analyzing
	 */
	public void setAnalyzing(boolean isAnalyzing) {
		this.isAnalyzing = isAnalyzing;
	}
}
