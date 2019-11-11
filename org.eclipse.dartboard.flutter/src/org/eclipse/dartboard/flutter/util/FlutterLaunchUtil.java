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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.util.StatusUtil;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;

/**
 * @author jonas
 *
 */
public class FlutterLaunchUtil {

	private static final ILog LOG = Platform.getLog(FlutterLaunchUtil.class);

	public static void launch(String flutterSdk, String target, String workingDir) {
		Launch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
		DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
		// TODO: There is a flutter.bat file, need to use it on windows
		ProcessBuilder processBuilder = new ProcessBuilder(flutterSdk + "/bin/flutter", "run", "--target=" + target);
		System.out.println(workingDir);
		processBuilder.directory(new File(workingDir));

		Job job = Job.create("Running " + target, runnable -> { //$NON-NLS-1$
			Process process;
			try {
				process = processBuilder.start();
				IProcess runtimeProcess = DebugPlugin.newProcess(launch, process, "Flutter console");
				launch.addProcess(runtimeProcess); // adding also opens an Eclipse console for the process
			} catch (IOException e) {
				LOG.log(StatusUtil.createError("Could not start Dart process", e)); //$NON-NLS-1$
			}
		});
		job.schedule();
	}

}
