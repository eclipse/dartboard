package com.vogella.eclipsedart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

public class PathLocator {

	public static Optional<String> getDartSDKLocation() {
		return getLocation("dart");
	}

	public static Optional<String> getLanguageServerLocation() {
		return Optional.of(".pub-cache/bin/dart_language_server");
	}

	public static Optional<String> getPubLocation() {
		return getLocation("pub");
	}

	protected static Optional<String> getLocation(String program) {
		String location = null;
		String[] commands = new String[] { "/usr/bin/zsh", "-c", "which " + program };
		//TODO: Add support for Windows and MacOS

		BufferedReader reader = null;
		try {
			Process process = Runtime.getRuntime().exec(commands);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			location = reader.readLine();
			return Optional.of(location);
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return Optional.empty();
	}

}
