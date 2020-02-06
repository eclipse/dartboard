/*******************************************************************************
 * Copyright (c) 2020 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Andrew Bowley 
 *******************************************************************************/
package org.eclipse.dartboard.util;

import org.eclipse.swt.widgets.Shell;

    public interface IPlatformFactory {
    
	/**
	 * Returns a Dart SDK checker
	 * 
	 * @param shell     Parent shell of owner or null if none
	 * @param isFlutter Flag set true if Dart SDK is inside Flutter
	 * @return DartSdkChecker object
	 */
	DartSdkChecker getDartSdkChecker(Shell shell, boolean isFlutter);

	/**
	 * Returns support for locating SDK artifacts
	 * 
	 * @return SdkLocator
	 */
	SdkLocator getSdkLocator();

}