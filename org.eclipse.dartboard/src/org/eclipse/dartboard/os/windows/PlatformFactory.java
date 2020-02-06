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
package org.eclipse.dartboard.os.windows;

import org.eclipse.dartboard.util.DartSdkChecker;
import org.eclipse.dartboard.util.IPlatformFactory;
import org.eclipse.dartboard.util.SdkLocator;
import org.eclipse.swt.widgets.Shell;

    public class PlatformFactory implements IPlatformFactory {

	private final PlatformSdkLocator sdkLocator;

	public PlatformFactory() {
		sdkLocator = new PlatformSdkLocator();
	}

	@Override
	public DartSdkChecker getDartSdkChecker(Shell shell, boolean isFlutter) {

		/**
		 * Returns a Dart SDK checker
		 *
		 * @param shell     Parent shell of owner or null if none
		 * @param isFlutter Flag set true if Dart SDK is inside Flutter
		 * @return DartSdkChecker object
		 */
		return new PlatformDartSdkChecker(shell, isFlutter);

    }

	/**
	 * Returns support for locating SDK artifacts
	 *
	 * @return SdkLocator
	 */
	@Override
	public SdkLocator getSdkLocator() {
		return sdkLocator;
	}
}
