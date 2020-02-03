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
package org.eclipse.dartboard.flutter.launch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.flutter.commands.AppCommand;
import org.eclipse.dartboard.flutter.sdk.FlutterSDK.Command;
import org.eclipse.dartboard.flutter.util.FlutterLibChangeListener;
import org.eclipse.dartboard.flutter.util.FlutterUtil;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;

public class FlutterLaunchJob extends Job {

	private static final ILog LOG = Platform.getLog(FlutterLaunchJob.class);

	private Process process;

	private IProject project;

	private Path sdkPath;

	private String targetFile;

	public FlutterLaunchJob(IProject project, Path sdkPath, String targetFile) {
		super(Command.RUN.getName());
		this.project = project;
		this.sdkPath = sdkPath;
		this.targetFile = targetFile;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		List<String> commands = new LinkedList<>();
		commands.add(FlutterUtil.getFlutterToolPath(sdkPath.toAbsolutePath().toString()));
		commands.addAll(Command.RUN.getCommands());

		commands.add(targetFile);
		ProcessBuilder processBuilder = new ProcessBuilder(commands);

		processBuilder.directory(new File(project.getLocation().toOSString()));
		Launch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
		DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);

		try {
			process = processBuilder.start();
			IProcess runtimeProcess = DebugPlugin.newProcess(launch, process, "Flutter console");
			launch.addProcess(runtimeProcess); // adding also opens an Eclipse console for the process
		} catch (IOException e) {
			IStatus status = DartLog.createError("Could not start Dart process", e);
			LOG.log(status);
			return status;
		}

		FlutterLibChangeListener listener = new FlutterLibChangeListener(project, this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		}
		return Status.OK_STATUS;
	}

	public void sendCommand(AppCommand command, boolean wait) throws IOException, InterruptedException {
		if (process == null || !process.isAlive()) {
			LOG.log(DartLog.createError("Tried sending command " + command + " to closed app process."));
			return;
		}
		process.getOutputStream().write(command.getCommand().getBytes());
		process.getOutputStream().flush();
		if (wait) {
			process.waitFor();
		}
	}

}
