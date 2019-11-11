package org.eclipse.dartboard.test.launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dartboard.dart.Constants;
import org.eclipse.dartboard.test.util.ProjectUtil;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.matcher.WithMnemonicTextMatcher;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.condition.ShellIsActive;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.bytebuddy.utility.RandomString;

@RunWith(RedDeerSuite.class)
public class LaunchShortcutTest {

	@SuppressWarnings("unchecked")
	@Test
	public void launchShortcut__DartProjectNoLaunchConfig__NewLaunchConfigIsCreated() throws Exception {
		String projectName = RandomString.make(8);
		String mainClass = RandomString.make(4) + ".dart";
		ProjectUtil.createDartProject(projectName);

		ProjectExplorer projectExplorer = new ProjectExplorer();
		projectExplorer.open();
		projectExplorer.getProject(projectName).select();

		new ContextMenuItem(new WithMnemonicTextMatcher("Run As"), new RegexMatcher("\\d Run as Dart Program"))
				.select();

		new WaitUntil(new ShellIsActive("Edit Configuration"));
		new LabeledCombo("Project:").setSelection(projectName);
		new LabeledText("Main class:").setText(mainClass);
		new PushButton("Run").click();
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		// Find last launch configuration for selected project.
		ILaunchConfiguration launchConfiguration = null;
		for (ILaunchConfiguration conf : manager.getLaunchConfigurations()) {
			if (conf.getAttribute(Constants.LAUNCH_SELECTED_PROJECT, "").equalsIgnoreCase(projectName)) { //$NON-NLS-1$
				launchConfiguration = conf;
			}
		}

		assertNotNull(launchConfiguration);
		assertEquals(launchConfiguration.getAttribute("main_class", ""), mainClass);

		// Cleanup
		launchConfiguration.delete();
	}

}
