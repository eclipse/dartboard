package org.eclipse.dartboard.test.project;

import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.core.resources.DefaultProject;
import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.condition.ShellIsActive;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.menu.ShellMenu;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
public class DartProjectTest {

	@Test
	public void projectWizard__NewMenu__HasDartProjectItem() {
		assertTrue(new ShellMenu().hasItem("File", "New", "Dart Project"));
	}

	@Test
	public void projectWizard__NewMenu__HasDartFileItem() {
		assertTrue(new ShellMenu().hasItem("File", "New", "Dart File"));
	}

	@Test
	public void projectWizard__NewDartProject__CreatesNewProject() {
		new ShellMenu().getItem("File", "New", "Dart Project").select();
		new WaitUntil(new ShellIsActive("New Dart Project"));
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);

		new LabeledText("Project name:").setText("some-random-project");
		new FinishButton().click();
		new WaitUntil(new ProjectExists("some-random-project"));

		PackageExplorerPart packageExplorer = new PackageExplorerPart();
		packageExplorer.open();
		assertTrue(packageExplorer.getProject("some-random-project").containsResource("pubspec.yaml"));
	}

	@Test
	public void projectWizard__NewDartProjectWithStagehand__CreatesNewProjectWithTemplate() {
		new ShellMenu().getItem("File", "New", "Dart Project").select();
		new WaitUntil(new ShellIsActive("New Dart Project"));
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);

		new LabeledText("Project name:").setText("some-random-project");
		new CheckBox("Use Stagehand template").click();
		new DefaultCombo().setSelection(0);
		new FinishButton().click();
		new WaitUntil(new ProjectExists("some-random-project"));

		PackageExplorerPart packageExplorer = new PackageExplorerPart();
		packageExplorer.open();
		DefaultProject project = packageExplorer.getProject("some-random-project");
		assertTrue(project.containsResource("pubspec.yaml"));
		assertTrue(project.containsResource("analysis_options.yaml"));
		assertTrue(project.containsResource("CHANGELOG.md"));
		assertTrue(project.containsResource("README.md"));
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		assertTrue(project.containsResource("pubspec.lock"));
	}
}
