package org.eclipse.dartboard.project.flutter;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class FlutterProjectWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private FlutterProjectPage flutterProjectPage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("New Flutter project");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		flutterProjectPage = new FlutterProjectPage(FlutterProjectPage.class.getSimpleName());
		flutterProjectPage.setTitle("New Flutter project");
		flutterProjectPage.setDescription("Create a new Flutter project");
		addPage(flutterProjectPage);
	}

	@Override
	public boolean performFinish() {
		String projectName = flutterProjectPage.getProjectName();
		String template = flutterProjectPage.getTemplate();
		String description = flutterProjectPage.getDescription();
		String organization = flutterProjectPage.getOrganization();
		String iosLanguage = flutterProjectPage.getIosLanguage();
		String androidLanguage = flutterProjectPage.getAndroidLanguage();

		return false;
	}

}
