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
 *     Lakshminarayana Nekkanti 
 *******************************************************************************/
package org.eclipse.dartboard.util;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dartboard.Constants;

public class ProjectUtil {
	public static IStatus addProjectNature(IProject project, IProgressMonitor monitor) throws ExecutionException {
		try {
			if (monitor != null && monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			if (project == null)
				throw new ExecutionException("Project shouldn't be a null.Unable to create a new dart project"); //$NON-NLS-1$
			if (!project.hasNature(Constants.NATURE_ID)) {
				IProjectDescription description = project.getDescription();
				String[] natures = description.getNatureIds();
				String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);

				newNatures[natures.length] = Constants.NATURE_ID;

				// validate the natures
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IStatus status = workspace.validateNatureSet(newNatures);

				// only apply new nature, if the status is ok
				if (status.getCode() == IStatus.OK) {
					description.setNatureIds(newNatures);
					project.setDescription(description, monitor);
				}
				return status;
			} else {
				if (monitor != null) {
					monitor.worked(1);
				}
			}
		} catch (CoreException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return Status.OK_STATUS;
	}
}
