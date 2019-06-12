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
package org.eclipse.dartboard.nature;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dartboard.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * The project nature for Dart projects
 * 
 * @author Jonas Hungershausen
 * @see DartProjectNatureHandler
 */
public class DartProjectNature implements IProjectNature {

	/**
	 * The current project this nature instance is being applied to
	 */
	private IProject project;

	@Override
	public void configure() throws CoreException {

		// Create pubspec file
		IFile pubspecFile = project.getFile("pubspec.yaml"); //$NON-NLS-1$
		if (!pubspecFile.exists()) {
			pubspecFile.create(null, false, null);
		}

		// TODO: Configure pub sync
		// If the project already contains a pubspec file, we should run `pub get`
		// Maybe ask if it should be run with `--offline`? -- This should probably be a
		// preference
	}

	@Override
	public void deconfigure() throws CoreException {
		IFile pubspecFile = project.getFile("pubspec.yaml"); //$NON-NLS-1$
		if (pubspecFile.exists()) {
			Display.getDefault().syncExec(() -> {
				boolean result = MessageDialog.openQuestion(null, Messages.ProjectNature_DeletePubspec_Title,
						Messages.ProjectNature_DeletePubspec_Message);
				if (result) {
					try {
						pubspecFile.delete(false, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}
}
