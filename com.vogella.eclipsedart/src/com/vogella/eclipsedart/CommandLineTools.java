package com.vogella.eclipsedart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

public class CommandLineTools {

	public static Optional<String> getDartSDKLocation() {
		return getLocation("dart");
	}

	protected static Optional<String> getLocation(String program) {
		String[] commands = new String[] { "/usr/bin/sh", "-c", "which " + program };

		return execute(commands);
	}

	public static Optional<String> execute(String... commands) {
		BufferedReader reader = null;
		try {
			Process process = Runtime.getRuntime().exec(commands);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = reader.readLine();

			return Optional.ofNullable(output);
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return Optional.empty();

	}

}
