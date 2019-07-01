package org.eclipse.dartboard.launch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dartboard.launch.console.DartConsoleFactory;
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

		Thread thread = new Thread(() -> {
			Process process;
			try {
				process = processBuilder.start();
				new DartConsoleFactory(process.getInputStream(), process.getErrorStream()).openConsole();
			} catch (IOException e) {
				LOG.error("Could not start Dart process", e); //$NON-NLS-1$
			}
		});

		thread.setDaemon(true);
		thread.start();
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
