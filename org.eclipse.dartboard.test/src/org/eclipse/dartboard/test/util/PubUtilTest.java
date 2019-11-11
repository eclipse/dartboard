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
 *     Jonas Hungershausen - initial API and implementation
 *******************************************************************************/
package org.eclipse.dartboard.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.dartboard.preferences.PubUtil;
import org.junit.Test;

public class PubUtilTest {

	@Test
	public void getUpdatePubEnviroment__NoVariable__OnlyPluginId() {
		ProcessBuilder builder = new ProcessBuilder("");
		builder.environment().clear();

		String pubEnvironment = PubUtil.getUpdatePubEnviroment(builder);

		assertThat(pubEnvironment, is("org.eclipse.dartboard"));
	}

	@Test
	public void getUpdatePubEnviroment__ExistingVariable__ExistingAndPluginId() {
		ProcessBuilder builder = new ProcessBuilder("");
		builder.environment().clear();
		String randomValue = "random_value";
		builder.environment().put("PUB_ENVIRONMENT", randomValue);

		String pubEnvironment = PubUtil.getUpdatePubEnviroment(builder);

		assertThat(pubEnvironment, is(randomValue + ":org.eclipse.dartboard"));
	}
}
