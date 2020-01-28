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
package org.eclipse.dartboard.flutter.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.util.GlobalConstants;

import com.google.common.io.CharSource;
import com.google.common.io.Files;

/**
 * @author jonas
 *
 */
public class IsFlutterProjectPropertyTester extends PropertyTester {

	private static final ILog LOG = Platform.getLog(IsFlutterProjectPropertyTester.class);

	private static final Pattern FLUTTER_SDK = Pattern.compile(".*sdk\\s*:\\s*flutter", Pattern.CASE_INSENSITIVE);

	private static final String IS_FLUTTER_PROJECT_PROPERTY = "isFlutterProject"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (IS_FLUTTER_PROJECT_PROPERTY.equalsIgnoreCase(property)) {
			IResource resource = Adapters.adapt(receiver, IResource.class);
			if (resource == null) {
				return false;
			}

			IProject project = resource.getProject();
			if (project == null) {
				return false;
			}
			IResource pubspec = project.findMember(GlobalConstants.PUBSPEC_YAML);
			if (pubspec == null) {
				return false;
			}
			File pubspecFile = pubspec.getRawLocation().toFile();
			CharSource pubspecContent = Files.asCharSource(pubspecFile, Charset.defaultCharset());
			try {
				for (String line : pubspecContent.readLines()) {
					if (FLUTTER_SDK.matcher(line).matches()) {
						return true;
					}
				}
			} catch (IOException e) {
				LOG.log(DartLog.createError("Could not open pubspec.yaml", e));
			}
		}
		return false;
	}

}
