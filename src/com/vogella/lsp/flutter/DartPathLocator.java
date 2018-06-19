package com.vogella.lsp.flutter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

public class DartPathLocator {

	public static String getDartSDKLocation() {
		String result = "/path/to/dartsdk";
		String[] commands = new String[] { "/bin/bash", "-c", "which dart" };
		//TODO: Add support for Windows and MacOS
		
		BufferedReader reader = null;
		try {
			Process process = Runtime.getRuntime().exec(commands);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			result = reader.readLine();
		}catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return result;
	}
	
	public static void warnDartSDKMIssing() {
		
	}
	
}
