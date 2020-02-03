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

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.flutter.commands.AppCommand;
import org.eclipse.dartboard.flutter.launch.FlutterLaunchJob;
import org.eclipse.dartboard.logging.DartLog;

/**
 * @author jonas
 *
 */
public class FlutterLibChangeListener implements IResourceChangeListener {

	private IProject project;
	private boolean reloadInProgress = false;

	private static final ILog LOG = Platform.getLog(FlutterLibChangeListener.class);
	private FlutterLaunchJob flutterLaunchJob;

	/**
	 * @param project2
	 */
	public FlutterLibChangeListener(IProject project, FlutterLaunchJob flutterLaunchJob) {
		this.project = project;
		this.flutterLaunchJob = flutterLaunchJob;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {

					@Override
					public boolean visit(IResourceDelta delta) throws CoreException {
						IResource resource = delta.getResource();
						if (resource == null || resource.getFileExtension() == null || !isContentChanged(delta)) {
							return true;
						}
						if (!"dart".equalsIgnoreCase(resource.getFileExtension()) || reloadInProgress) {
							return true;
						}
						IProject proj = resource.getProject();
						if (proj == null || !proj.equals(project)) {
							return true;
						}
						reloadInProgress = true;
						try {
							flutterLaunchJob.sendCommand(AppCommand.RELOAD, false);
						} catch (IOException | InterruptedException e) {
							LOG.log(DartLog.createError("Could not initiate hot reload on save", e));
						} finally {
							reloadInProgress = false;
						}
						return true;
					}
				});
			} catch (CoreException e) {
				LOG.log(DartLog.createError(e.getMessage(), e));
			}
		}
	}

	/**
	 * Determines whether an {@link IResourceDelta} indicates that a
	 * {@link IResource}'s content was changed.
	 *
	 * @param delta - The {@link IResourceDelta} to be checked
	 * @return <code>true</code> if the {@link IResource}'s content was changed,
	 *         false otherwise.
	 */
	private boolean isContentChanged(IResourceDelta delta) {
		return delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.CONTENT) != 0;
	}

}
