package org.eclipse.dartboard.test.pub;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.dartboard.util.PubUtil;
import org.junit.Test;

public class PubUtilTest {

	@Test
	public void getUpdatePubEnviroment__NoVariable__OnlyPluginId() {
		ProcessBuilder builder = new ProcessBuilder("");
		builder.environment().clear();

		String pubEnvironment = PubUtil.getUpdatePubEnviroment(builder);

		assertThat(pubEnvironment, is("org.eclipse.dartboard"));
	}

	@Test
	public void getUpdatePubEnviroment__ExistingVariable__ExistingAndPluginId() {
		ProcessBuilder builder = new ProcessBuilder("");
		builder.environment().clear();
		String randomValue = "random_value";
		builder.environment().put("PUB_ENVIRONMENT", randomValue);

		String pubEnvironment = PubUtil.getUpdatePubEnviroment(builder);

		assertThat(pubEnvironment, is(randomValue + ":org.eclipse.dartboard"));
	}
}
