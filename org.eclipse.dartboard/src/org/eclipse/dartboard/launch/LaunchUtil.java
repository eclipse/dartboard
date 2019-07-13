package org.eclipse.dartboard.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.Logger;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;

public class LaunchUtil {

	/**
	 * Passes a supplied file path to the Dart binary at a supplied SDK location.
	 * 
	 * @param dartSdk  - The location of the Dart SDK that should be used for the
	 *                 execution
	 * @param dartFile - The path to a file that should be executed. Assumes correct
	 *                 OS strings (e.g. correct separators). These can be obtained
	 *                 from {@link File#separator}.
	 */
	public static void launchDartFile(String dartSdk, String dartFile) {
		Launch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
		DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
		launchDartFile(launch, dartSdk, dartFile);
	}

	/**
	 * Passes a supplied file path to the Dart binary at a supplied SDK location.
	 * 
	 * @param launch   - The launch where the started Dart process will be attached
	 *                 to.
	 * @param dartSdk  - The location of the Dart SDK that should be used for the
	 *                 execution
	 * @param dartFile - The path to a file that should be executed. Assumes correct
	 *                 OS strings (e.g. correct separators). These can be obtained
	 *                 from {@link File#separator}.
	 */
	public static void launchDartFile(ILaunch launch, String dartSdk, String dartFile) {
		ProcessBuilder processBuilder = new ProcessBuilder(dartSdk + "/bin/dart", dartFile);//$NON-NLS-1$

		Job job = Job.create("Running " + dartFile, runnable -> { //$NON-NLS-1$
			Process process;
			try {
				process = processBuilder.start();
				IProcess runtimeProcess = DebugPlugin.newProcess(launch, process, Messages.Console_Name);
				launch.addProcess(runtimeProcess); // adding also opens an Eclipse console for the process
			} catch (IOException e) {
				Logger.logError("Could not start Dart process", e); //$NON-NLS-1$
			}
		});
		job.schedule();
	}

	/**
	 * Passes a supplied file path to the Dart binary at a supplied SDK location.
	 * 
	 * @param dartSdk  - The location of the Dart SDK that should be used for the
	 *                 execution
	 * @param dartFile - An {@link IPath} of the file that should be executed.
	 */
	public static void launchDartFile(String dartSdk, IPath dartFile) {
		launchDartFile(dartSdk, dartFile.toOSString());
	}

}
