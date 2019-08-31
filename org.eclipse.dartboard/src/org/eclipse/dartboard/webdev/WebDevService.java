package org.eclipse.dartboard.webdev;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dartboard.util.PubUtil;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDevService {

	private static final Logger LOG = LoggerFactory.getLogger(WebDevService.class);
	private static boolean activated = false;

	public static void init() {
		Job.create("Activate webdev service", (monitor) -> {
			try {
				if (isWebDevInstalled()) {
					activated = true;
				} else {
					activateWebDevPackage();
				}
			} catch (IOException | InterruptedException e) {
				LOG.error("Could not activate webdev service", e);
				activated = false;
			}
		}).schedule();
	}

	public static void serve(ILaunch launch, ILaunchConfiguration configuration) {
		runWebdevCommand("serve", launch, configuration);
	}

	public static void build(ILaunch launch, ILaunchConfiguration configuration) {
		runWebdevCommand("build", launch, configuration);
	}

	public static void runWebdevCommand(String command, ILaunch launch, ILaunchConfiguration configuration) {
		if (!activated) {
			// TODO: Add warning or throw exception, that webdev is not available
		}
	}

	public static void activateWebDevPackage() throws IOException, InterruptedException {
		ProcessBuilder builder = PubUtil.getPubProcessBuilder("global", "activate", "webdev");
		builder.start().waitFor(PubUtil.GLOBAL_ACTIVATE_TIMEOUT, TimeUnit.SECONDS);
	}

	private static boolean isWebDevInstalled() throws IOException {
		ProcessBuilder builder = PubUtil.getPubProcessBuilder("global", "list");

		InputStream input = builder.start().getInputStream();
		List<String> result = IOUtils.readLines(input, Charset.defaultCharset());
		return result.stream().anyMatch(line -> line.startsWith("webdev"));
	}

}
