package org.eclipse.dartboard.stagehand;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.dartboard.util.PubUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class StagehandService {

	private static final Logger LOG = LoggerFactory.getLogger(StagehandService.class);

	/**
	 * A local cache that should not be refreshed (hence it's static)
	 */
	private static List<StagehandTemplate> stagehandTemplates;

	/**
	 * The timeout in seconds to wait until the activation process is considered
	 * stuck
	 */
	private static final int STAGEHAND_ACTIVATE_TIMEOUT = 30;

	public static List<StagehandTemplate> getStagehandTemplates() {
		// The actual gathering process should only be run once (if the cache is not
		// populated already)
		if (stagehandTemplates != null) {
			return stagehandTemplates;
		}

		activateStagehand();

		@SuppressWarnings("nls")
		ProcessBuilder builder = PubUtil.getPubProcessBuilder("global", "run", "stagehand", "--machine");

		try {
			InputStreamReader reader = new InputStreamReader(builder.start().getInputStream(),
					Charset.defaultCharset());

			Gson gson = new Gson();
			StagehandTemplate[] templates = gson.fromJson(reader, StagehandTemplate[].class);

			stagehandTemplates = Lists.newArrayList(templates);
			stagehandTemplates
					.sort((first, second) -> first.getDisplayName().compareToIgnoreCase(second.getDisplayName()));

		} catch (IOException e) {
			LOG.error("Could not fetch stagehand template list", e); //$NON-NLS-1$
		}

		return stagehandTemplates;
	}

	public static void activateStagehand() {
		@SuppressWarnings("nls")
		ProcessBuilder builder = PubUtil.getPubProcessBuilder("global", "activate", "stagehand");
		try {
			builder.start().waitFor(STAGEHAND_ACTIVATE_TIMEOUT, TimeUnit.SECONDS);
		} catch (IOException | InterruptedException e) {
			LOG.error("Could not activate stagehand globally", e); //$NON-NLS-1$
		}
	}
}
