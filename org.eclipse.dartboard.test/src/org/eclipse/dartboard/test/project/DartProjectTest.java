package org.eclipse.dartboard.test.project;

import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.condition.ShellIsActive;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.menu.ShellMenu;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
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
	public void projectWizard__NewMenuDartProject__CreatesNewProject() {
		new ShellMenu().getItem("File", "New", "Dart Project").select();
		new WaitUntil(new ShellIsActive("New Dart Project"));
		new LabeledText("Project name:").setText("some-random-project");
		new FinishButton().click();
		new WaitUntil(new ProjectExists("some-random-project"));
		new PackageExplorerPart().getProject("some-random-project").containsResource("pubspec.yaml");
	}
}
