package org.eclipse.dartboard.test.pub;

import static org.junit.Assert.assertFalse;

import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.dartboard.test.util.ProjectUtil;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.direct.project.Project;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.impl.menu.ContextMenu;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.bytebuddy.utility.RandomString;

@RunWith(RedDeerSuite.class)
public class PubGetHandlerTest {

	private String projectName;

	@Before
	public void setup() {
		projectName = RandomString.make(8);
	}

	@After
	public void teardown() {
		Project.delete(projectName, true, true);
	}

	@Test
	public void pubGetCommand__ExistingDartProject__CommandIsAvailableAndRunsPubGet() {
		DartPreferences.getPreferenceStore().setValue(GlobalConstants.P_SYNC_PUB, false);

		ProjectUtil.createDartProject(projectName);

		ProjectExplorer projectExplorer = new ProjectExplorer();
		projectExplorer.open();
		projectExplorer.getProject(projectName).select();

		new ContextMenuItem("Pub", "Get dependencies").select();

		new WaitWhile(new JobIsRunning());
	}

	@Test
	public void pubGetCommand__ExistingNonDartProject__CommandIsNotAvailable() {

		Project.create(projectName);
		ProjectExplorer projectExplorer = new ProjectExplorer();
		projectExplorer.open();
		new WaitUntil(new ProjectExists(projectName, projectExplorer));
		projectExplorer.getProject(projectName).select();

		assertFalse(new ContextMenu().hasItem("Pub", "Get dependencies"));
	}
}
