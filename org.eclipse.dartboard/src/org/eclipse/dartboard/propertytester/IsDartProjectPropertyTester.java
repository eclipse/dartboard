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
package org.eclipse.dartboard.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dartboard.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsDartProjectPropertyTester extends PropertyTester {

	private final static Logger LOG = LoggerFactory.getLogger(IsDartProjectPropertyTester.class);

	private static final String IS_DART_PROJECT_PROPERTY = "isDartProject"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (IS_DART_PROJECT_PROPERTY.equals(property)) {
			IResource resource = Adapters.adapt(receiver, IResource.class);
			if (resource == null) {
				return false;
			}

			IProject project = resource.getProject();
			if (project == null) {
				return false;
			}
			if (project.findMember(Constants.PUBSPEC) != null) {
				return true;
			}

			try {
				for (IResource res : project.members()) {
					if ("dart".equals(res.getFileExtension())) { //$NON-NLS-1$
						return true;
					}
				}
			} catch (CoreException e) {
				LOG.error("Couldn't list members of project " + project.getName(), e); //$NON-NLS-1$
			}
		}

		return false;
	}
}
