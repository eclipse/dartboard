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
package org.eclipse.dartboard.test.project;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.dart.project.DartProjectConfigurator;
import org.junit.Before;
import org.junit.Test;

public class DartProjectConfiguratorTest {

	DartProjectConfigurator configurator;

	@Before
	public void setup() {
		configurator = new DartProjectConfigurator();
	}

	@Test
	public void getFoldersToIgnore__ValidParameters__ReturnsEmptySet() {
		assertTrue(configurator.getFoldersToIgnore(null, null).isEmpty());
	}

	@Test
	public void findConfigurableLocations__6Projects__ReturnsAllProjects() throws Exception {
		URL url = FileLocator.find(Platform.getBundle("org.eclipse.dartboard.test"),
				Path.fromPortableString("resources/projects"));
		url = FileLocator.toFileURL(url);
		File file = new File(url.getFile());

		assertThat(configurator.findConfigurableLocations(file, null), hasSize(6));
	}

}
