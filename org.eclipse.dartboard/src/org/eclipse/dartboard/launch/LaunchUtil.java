package org.eclipse.dartboard.launch;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dartboard.launch.console.DartConsoleFactory;

public class LaunchUtil {

	public static void launchDartFile(String dartSdk, String dartFile) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(dartSdk + "/bin/dart", dartFile);//$NON-NLS-1$

		Process process = processBuilder.start();
		new DartConsoleFactory(process.getInputStream(), process.getErrorStream()).openConsole();
	}

	public static void launchDartFile(String dartSdk, IPath dartFile) throws IOException {
		launchDartFile(dartSdk, dartFile.toOSString());
	}

}
