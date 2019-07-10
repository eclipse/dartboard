package org.eclipse.dartboard.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.launch.console.DartConsoleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchUtil {

	private static final Logger LOG = LoggerFactory.getLogger(LaunchUtil.class);

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
		ProcessBuilder processBuilder = new ProcessBuilder(dartSdk + "/bin/dart", dartFile);//$NON-NLS-1$

		Job job = Job.create("Running " + dartFile, runnable -> { //$NON-NLS-1$
			Process process;
			try {
				process = processBuilder.start();
				DartConsoleManager.getInstance().openConsole(process.getInputStream(), process.getErrorStream());
			} catch (IOException e) {
				LOG.error("Could not start Dart process", e); //$NON-NLS-1$
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
