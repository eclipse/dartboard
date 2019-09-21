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
package org.eclipse.dartboard.flutter;

public class FlutterProjectGenerator {

	private String projectName;
	private String template;
	private String description;
	private String organization;
	private String iosLanguage;
	private String androidLanguage;

	public FlutterProjectGenerator(String projectName, String template, String description, String organization,
			String iosLanguage, String androidLanguage) {
		this.projectName = projectName;
		this.template = template;
		this.description = description;
		this.organization = organization;
		this.iosLanguage = iosLanguage;
		this.androidLanguage = androidLanguage;
	}

}
