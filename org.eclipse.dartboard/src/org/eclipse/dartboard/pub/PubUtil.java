package org.eclipse.dartboard.pub;

import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.util.DartUtil;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class PubUtil {

	private PubUtil() {

	}

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
	private static String getUpdatePubEnviroment(ProcessBuilder builder) {
		String pubEnv = builder.environment().get(Constants.PUB_ENVIRONMENT_VARIABLE);
		if (Strings.isNullOrEmpty(pubEnv)) {
			return Constants.PLUGIN_ID;
		} else {
			return pubEnv += Constants.PLUGIN_ID;
		}
	}

}
