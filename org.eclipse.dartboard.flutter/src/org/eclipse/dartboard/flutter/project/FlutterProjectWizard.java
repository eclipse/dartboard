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
package org.eclipse.dartboard.flutter.project;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class FlutterProjectWizard extends Wizard implements INewWizard {

	private static final ILog LOG = Platform.getLog(FlutterProjectWizard.class);

	private FlutterProjectPage dartProjectPage;
	private IProject newProject;
	private IWorkbench workbench;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		setWindowTitle(Messages.NewProject_WindowTitle);
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		dartProjectPage = new FlutterProjectPage(FlutterProjectPage.class.getSimpleName());
		dartProjectPage.setTitle(Messages.NewProject_Title);
		dartProjectPage.setDescription(Messages.NewProject_Description);
		addPage(dartProjectPage);
	}

	private IProject createNewProject() {
		if (newProject != null) {
			return newProject;
		}

		// get a project handle
		final IProject newProjectHandle = dartProjectPage.getProjectHandle();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
		if (!dartProjectPage.useDefaults()) {
			description.setLocationURI(dartProjectPage.getLocationURI());
		}

		// create the new project operation
		IRunnableWithProgress operation = monitor -> {
			CreateProjectOperation projectOperation = new CreateProjectOperation(description,
					Messages.NewProject_WindowTitle);
			try {
				projectOperation.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
			} catch (ExecutionException e) {
				throw new InvocationTargetException(e);
			}
		};

		// run the new project creation operation
		try {
			getContainer().run(true, true, operation);
		} catch (InterruptedException e) {
			LOG.log(DartLog.createError(e.getMessage()));
			return null;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof ExecutionException && t.getCause() instanceof CoreException) {
				CoreException cause = (CoreException) t.getCause();
				StatusAdapter status;
				if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					status = new StatusAdapter(DartLog.newStatus(IStatus.WARNING,
							NLS.bind(Messages.NewProject_CaseVariantExistsError, newProjectHandle.getName()), cause));
				} else {
					status = new StatusAdapter(DartLog.newStatus(cause.getStatus().getSeverity(),
							Messages.NewProject_ErrorMessage, cause));
				}
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.NewProject_ErrorMessage);
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			} else {
//				StatusAdapter status = new StatusAdapter(new Status(IStatus.WARNING, Constants.PLUGIN_ID, 0,
//						NLS.bind(Messages.NewProject_InternalError, t.getMessage()), t));
//				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.NewProject_ErrorMessage);
//				StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
			}
			return null;
		}
		newProject = newProjectHandle;
		return newProject;
	}

	@Override
	public boolean performFinish() {
		createNewProject();
		if (newProject == null) {
			return false;
		}
		BasicNewResourceWizard.selectAndReveal(newProject, workbench.getActiveWorkbenchWindow());
		return true;
	}

}
