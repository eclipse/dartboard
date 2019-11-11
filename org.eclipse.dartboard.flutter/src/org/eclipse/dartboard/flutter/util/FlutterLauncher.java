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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.dartboard.flutter.commands.AppCommand;
import org.eclipse.dartboard.flutter.launch.FlutterLaunchJob;

/**
 * @author jonas
 *
 */
public class FlutterLauncher {

	private IProject project;
	private FlutterLaunchJob job;

	/**
	 * 
	 */
	public FlutterLauncher(IProject project) {
		this.project = project;
	}

	public void launch(String flutterSdk, String target) {
		// TODO: There is a flutter.bat file, need to use it on windows
		ProcessBuilder processBuilder = new ProcessBuilder(flutterSdk + "/bin/flutter", "run", "--target=" + target);

		processBuilder.directory(new File(project.getLocation().toOSString()));

		job = new FlutterLaunchJob("Running " + target, processBuilder, project);
		job.schedule();
	}
	
	public void restart() {
		try {
			job.sendCommand(AppCommand.RESTART);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			job.sendCommand(AppCommand.STOP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
