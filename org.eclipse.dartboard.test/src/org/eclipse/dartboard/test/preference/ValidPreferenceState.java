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
package org.eclipse.dartboard.test.preference;

/**
 * Interface for preference dialog which can flag when it is displaying it's
 * valid state
 * 
 * @author Andrew Bowley
 *
 */
public interface ValidPreferenceState {

	boolean isValid();
}
