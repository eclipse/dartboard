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
 *     Jonas Hungershausen - https://github.com/eclipse/dartboard/issues/1
 *******************************************************************************/
package org.eclipse.dartboard.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dartboard.dart.Constants;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.dartboard.util.LaunchUtil;
import org.eclipse.dartboard.util.PlatformUIUtil;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * A {@link ILaunchShortcut} used to launch a single *.dart file.
 * 
 * The launch shortcut can be triggered from the context menu of a file in the
 * Project Explorer. It will run the selected file using the Dart sdk from the
 * preferences.
 * 
 * For more advanced settings the project should be launched using the
 * {@link LaunchShortcut} for projects.
 * 
 * @author Jonas Hungershausen
 * @see LaunchShortcut
 *
 */
public class LaunchFileShortcut implements ILaunchShortcut {

	private ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore(Constants.PLUGIN_ID);

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IFile) {
				IFile file = (IFile) firstElement;
				launch(file.getLocation(), null);
			}
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput editorInput = editor.getEditorInput();
		// We don't need this check once we create and and run using launch configuration for file
		if (IDE.saveAllEditors(new IResource[] { editorInput.getAdapter(IResource.class) }, true) && editorInput instanceof FileEditorInput) {
			launch(((FileEditorInput) editorInput).getPath(), null);
		}
	}

	protected void launch(IPath file, String sdk) {
		if (sdk == null) {
			sdk = preferences.getString(Constants.PREFERENCES_SDK_LOCATION);
		}
		if (sdk == null || sdk.isEmpty()) {
			MessageDialog.openError(PlatformUIUtil.getActiveShell(), Messages.Launch_PageTitle,
					Messages.NewProject_SDK_Not_Found);
			return;
		}

		LaunchUtil.launchDartFile(sdk, file);
	}
}
