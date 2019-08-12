package org.eclipse.dartboard.util;

import org.eclipse.dartboard.Constants;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class PubUtil {

	private PubUtil() {
	}

	/**
	 * Constructs a process builder with the given {@code args}.
	 * 
	 * If pub is invoked from a tool there should be a PUB_ENVIRONMENT variable
	 * present describing the tool. This methods returns a {@link ProcessBuilder}
	 * that has it and the correct value in it.
	 * 
	 * @param args - the arguments passed to the ProcessBuilder
	 * @return the {@link ProcessBuilder} with the correct environment variables and
	 *         args
	 */
	public static ProcessBuilder getPubProcessBuilder(String... args) {
		ProcessBuilder builder = new ProcessBuilder(Lists.asList(DartUtil.getTool("pub"), args)); //$NON-NLS-1$
		builder.environment().put(Constants.PUB_ENVIRONMENT_VARIABLE, getUpdatePubEnviroment(builder));
		return builder;
	}

	/**
	 * Returns a String containing the current PUB_ENVIRONMENT plus
	 * {@link Constants#PLUGIN_ID}.
	 * 
	 * @param builder - The {@link ProcessBuilder} off of which the environment
	 *                variables are gotten
	 * @return the pub environment variable plus the id of the plugin
	 */
	public static String getUpdatePubEnviroment(ProcessBuilder builder) {
		String pubEnv = builder.environment().get(Constants.PUB_ENVIRONMENT_VARIABLE);
		if (Strings.isNullOrEmpty(pubEnv)) {
			return Constants.PLUGIN_ID;
		} else {
			return pubEnv + ":" + Constants.PLUGIN_ID; //$NON-NLS-1$
		}
	}
}
