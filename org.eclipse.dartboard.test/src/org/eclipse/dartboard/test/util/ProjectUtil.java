package org.eclipse.dartboard.test.util;

import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.workbenchmenu.WorkbenchMenuWizardDialog;

public class ProjectUtil {

	public static void createDartProject(String name) {

		DartProjectPage dialog = new DartProjectPage();
		dialog.open();
		dialog.activate();

		new LabeledText("Project name:").setText(name);
		new FinishButton().click();
		new WaitUntil(new ProjectExists(name, new ProjectExplorer()));

	}

	private static class DartProjectPage extends WorkbenchMenuWizardDialog {

		public DartProjectPage() {
			super("New Dart Project", "File", "New", "Dart Project");
		}
	}


	private static class FlutterProjectPage extends WorkbenchMenuWizardDialog {

		public FlutterProjectPage() {
			super("New Flutter Project", "File", "New", "Dart Project");
		}
	}

}
