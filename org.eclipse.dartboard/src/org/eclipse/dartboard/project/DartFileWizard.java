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
package org.eclipse.dartboard.project;

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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.StatusUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DartFileWizard extends Wizard implements INewWizard {
	private static final Logger LOG = LoggerFactory.getLogger(DartFileWizard.class);

	private DartFilePage dartFilePage;
	private IStructuredSelection selection;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		setWindowTitle(Messages.NewFile_windowTitle);
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		dartFilePage = new DartFilePage(DartFilePage.class.getSimpleName(), selection);
		dartFilePage.setTitle(Messages.NewFile_title);
		dartFilePage.setDescription(Messages.NewFile_description);
		dartFilePage.setFileExtension("dart"); //$NON-NLS-1$
		dartFilePage.setFileName("NewDart"); //$NON-NLS-1$
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
			StatusUtil.applyToStatusLine(dartFilePage, StatusUtil.createError(e.getTargetException()));
			return false;
		}
		return true;
	}

	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
		// create a dart file
		monitor.beginTask(NLS.bind(Messages.NewFile_creating, fileName), 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			StatusUtil.throwCoreException(NLS.bind(Messages.NewFile_container_doesnot_exist, containerName));
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			String contents = "void main() {\r\n" + "    print('Hello World');\r\n" + "}"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
			InputStream stream = new ByteArrayInputStream(contents.getBytes());
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		monitor.worked(1);
		monitor.setTaskName(Messages.NewFile_opening_file);
		getShell().getDisplay().asyncExec(() -> {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, true);
			} catch (PartInitException e) {
				LOG.error(e.getMessage());
			}
		});
		monitor.worked(1);
	}
}
