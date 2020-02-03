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
package org.eclipse.dartboard.flutter.pub;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.flutter.sdk.FlutterSDK;
import org.eclipse.dartboard.flutter.sdk.FlutterSDK.Command;
import org.eclipse.dartboard.flutter.util.FlutterUtil;
import org.eclipse.dartboard.logging.DartLog;

public class FlutterPubGetJob extends Job {

	private Path sdkPath;
	private IProject project;
	private Process currentProcess;

	public FlutterPubGetJob(IProject project, Path sdkPath) {
		super(FlutterSDK.Command.PUB_GET.getName() + " in project '" + project.getName() + "'");
		this.project = project;
		this.sdkPath = sdkPath;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		List<String> commands = new LinkedList<>();
		commands.add(FlutterUtil.getFlutterToolPath(sdkPath.toAbsolutePath().toString()));
		commands.addAll(Command.PUB_GET.getCommands());
		ProcessBuilder builder = new ProcessBuilder(commands);
		builder.directory(project.getLocation().toFile());

		try {
			currentProcess = builder.start();
			// We don't know how long this will take, so we can use the "indefinite" loading
			// bar here.
			monitor.beginTask(this.getName(), IProgressMonitor.UNKNOWN);
			currentProcess.waitFor();
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (IOException | InterruptedException exception) {
			return DartLog.createError("Could not start pub get process", exception);
		} catch (CoreException exception) {
			return DartLog.createError("Could not refresh workspace for project " + project.getName(), exception);
		}

		return Status.OK_STATUS;
	}

}
