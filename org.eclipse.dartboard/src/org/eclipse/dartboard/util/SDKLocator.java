package org.eclipse.dartboard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.core.runtime.Platform;

public class SDKLocator {

	public static final boolean IS_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	/**
	 * Returns a {@link Path} containing the location of the Dart SDK folder.
	 * 
	 * This method finds the location of the Dart SDK on the system, if installed.
	 * On *nix based systems it tries to locate the Dart binary by using the
	 * {@code which} command. Typically the output is a symbolic link to the actual
	 * binary. Since the Dart SDK installation folder contains more binaries that we
	 * need, we resolve the symbolic link and return the path to the /bin directory
	 * inside the SDK installation folder.
	 * 
	 * On Windows this method uses the where command to locate the binary.
	 * 
	 * @return - An {@link Optional} of {@link Path} containing the path to the
	 *         {@code /bin} folder inside the Dart SDK installation directory or
	 *         empty if the SDK is not found on the host machine.
	 */
	public static Optional<Path> getDartLocation() throws IOException, InterruptedException {
		return getLocation("dart", false); //$NON-NLS-1$
	}

	public static Optional<Path> getFlutterLocation() throws IOException, InterruptedException {
		return getLocation("flutter", true); //$NON-NLS-1$
	}

	public static Optional<Path> getLocation(String program, boolean interactive)
			throws IOException, InterruptedException {
		Path path = null;
		String[] command;
		if (IS_WINDOWS) {
			command = new String[] { "cmd", "/c", "where " + program }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			String shell = getShell();
			if (interactive) {
				command = new String[] { shell, "-i", "-c", "which " + program }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				command = new String[] { shell, "-c", "which " + program }; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(command);
		Process process = processBuilder.start();
		process.waitFor();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String location = reader.readLine();

			if (location != null) {
				path = Paths.get(location);
				path = path.toRealPath().getParent();
			}
		}

		// TODO: Try different default installs (need to collect them)
		return Optional.ofNullable(path);
	}

	public static String getShell() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "echo $SHELL"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Process process = builder.start();
		process.waitFor();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String location = reader.readLine();
			return location;
		}
	}
}
