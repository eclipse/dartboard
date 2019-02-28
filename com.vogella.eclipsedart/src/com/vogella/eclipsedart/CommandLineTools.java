package com.vogella.eclipsedart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineTools {
	
	private static final Logger LOG = LoggerFactory.getLogger(CommandLineTools.class);

	public static Optional<String> getDartSDKLocation() {
		return getLocation("dart");
	}

	protected static Optional<String> getLocation(String program) {
		String[] commands = new String[] { "/usr/bin/sh", "-c", "which " + program };

		return execute(commands);
	}

	public static Optional<String> execute(String... commands) {
		try(BufferedReader reader 
				= new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(commands).getInputStream()))){
			
			String output = reader.readLine();

			return Optional.ofNullable(output);
		} catch (IOException exception) {
			LOG.error(exception.getMessage());
		}

		return Optional.empty();

	}

}
