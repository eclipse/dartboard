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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class FlutterFileWizard extends Wizard implements INewWizard {

	private static final ILog LOG = Platform.getLog(FlutterFileWizard.class);

	private FlutterFilePage dartFilePage;
	private IStructuredSelection selection;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		setWindowTitle(Messages.NewFile_WindowTitle);
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		dartFilePage = new FlutterFilePage(FlutterFilePage.class.getSimpleName(), selection);
		dartFilePage.setTitle(Messages.NewFile_Title);
		dartFilePage.setDescription(Messages.NewFile_Description);
		dartFilePage.setFileExtension("dart"); //$NON-NLS-1$
		addPage(dartFilePage);
	}

	@Override
	public boolean performFinish() {
		final String containerName = dartFilePage.getContainerFullPath().toOSString();
		final String fileName = dartFilePage.getFileName();
		IRunnableWithProgress op = monitor -> {
			try {
				doFinish(containerName, fileName, monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			DartLog.applyToStatusLine(dartFilePage, DartLog.createError(e.getTargetException()));
			return false;
		}
		return true;
	}

	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
		// create a dart file
		monitor.beginTask(NLS.bind(Messages.NewFile_Creating, fileName), 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			DartLog.throwCoreException(NLS.bind(Messages.NewFile_Container_Doesnot_Exist, containerName));
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try (InputStream stream = new ByteArrayInputStream("".getBytes())) { //$NON-NLS-1$
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
		} catch (IOException e) {
			LOG.log(DartLog.createError(e.getMessage()));
		}
		monitor.worked(1);
		monitor.setTaskName(Messages.NewFile_OpeningFile);
		getShell().getDisplay().asyncExec(() -> {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, true);
			} catch (PartInitException e) {
				LOG.log(DartLog.createError(e.getMessage()));
			}
		});
		monitor.worked(1);
	}
}
