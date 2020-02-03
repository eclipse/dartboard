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
package org.eclipse.dartboard.flutter.sdk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.flutter.commands.AppCommand;
import org.eclipse.dartboard.flutter.launch.FlutterLaunchJob;
import org.eclipse.dartboard.flutter.pub.FlutterPubGetJob;
import org.eclipse.dartboard.flutter.util.FlutterUtil;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.ImmutableList;

/**
 * @author jonas
 *
 */
public class FlutterSDK {

	private static final ILog LOG = Platform.getLog(FlutterSDK.class);

	private static final Map<String, FlutterSDK> SDK_CACHE = new HashMap<>();

	private final IProject project;
	private final Path sdkPath;
	private FlutterLaunchJob flutterLaunchJob;
	private FlutterPubGetJob pubGetJob;

	private FlutterSDK(IProject project, Path sdkPath) {
		this.project = project;
		this.sdkPath = sdkPath;
	}

	public static FlutterSDK forProject(IProject project) {
		if (project == null) {
			return null;
		}

		if (SDK_CACHE.containsKey(project.getName())) {
			return SDK_CACHE.get(project.getName());
		} else {
			String sdkLocation = FlutterUtil.getDefaultSDKPath();
			Path path = Paths.get(sdkLocation);
			FlutterSDK sdk = new FlutterSDK(project, path);
			SDK_CACHE.put(project.getName(), sdk);
			return sdk;
		}
	}

	public IProject getProject() {
		return project;
	}

	public Path getSdkPath() {
		return sdkPath;
	}

	public void run(String targetPath) {
		Display.getDefault().syncExec(() -> {
			if (flutterLaunchJob != null && flutterLaunchJob.getState() == Job.RUNNING) {
				boolean restart = MessageDialog.openQuestion(null, "Launch running",
						"Do you want to iniate a restart on the running app?");
				if (restart) {
					try {
						flutterLaunchJob.sendCommand(AppCommand.RESTART, false);
					} catch (IOException | InterruptedException e) {
						LOG.log(DartLog.createError("Could not restart app", e));
					}
					return;
				} else {
					try {
						flutterLaunchJob.sendCommand(AppCommand.STOP, true);
					} catch (IOException | InterruptedException e) {
						LOG.log(DartLog.createError("Could not stop app", e));
					}
				}
			}
			flutterLaunchJob = new FlutterLaunchJob(project, sdkPath, targetPath);
			flutterLaunchJob.schedule();
		});

	}

	public void pubGet() {
		if (pubGetJob != null) {
			pubGetJob.cancel();
		}
		pubGetJob = new FlutterPubGetJob(project, sdkPath);
		pubGetJob.schedule();
	}

	@Override
	public String toString() {
		return "FlutterSDK [project=" + project + ", sdkPath=" + sdkPath + "]";
	}

	public enum Command {
		PUB_GET("Pub get", "pub", "get"), RUN("Run", "run");

		private final String name;
		private final ImmutableList<String> commands;

		private Command(String name, String... commands) {
			this.name = name;
			this.commands = ImmutableList.copyOf(commands);
		}

		public ImmutableList<String> getCommands() {
			return commands;
		}

		public String getName() {
			return name;
		}
	}

}
