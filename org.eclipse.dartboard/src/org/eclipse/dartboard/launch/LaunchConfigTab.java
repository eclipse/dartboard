package org.eclipse.dartboard.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LaunchConfigTab extends AbstractLaunchConfigurationTab {

	private Text textSdkLocation;
	private Text textMainClass;
	private Combo comboProject;

	private IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEY);

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.BORDER);
		setControl(comp);

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

		Label labelProject = new Label(comp, SWT.NONE);
		labelProject.setText("Project: ");
		GridDataFactory.swtDefaults().applyTo(labelProject);

		comboProject = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		for (var project : getProjectsInWorkspace()) {
			comboProject.add(project.getName());
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboProject);
		comboProject.addModifyListener((event) -> updateLaunchConfigurationDialog());

		Label labelSdkLocation = new Label(comp, SWT.NONE);
		labelSdkLocation.setText("Dart SDK Location: ");
		GridDataFactory.swtDefaults().applyTo(labelSdkLocation);

		textSdkLocation = new Text(comp, SWT.BORDER);
		textSdkLocation.setMessage("The location the dart sdk is installed to");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textSdkLocation);
		textSdkLocation.addModifyListener((event) -> updateLaunchConfigurationDialog());

		Label labelMainClass = new Label(comp, SWT.NONE);
		labelMainClass.setText("Main class: ");
		GridDataFactory.swtDefaults().applyTo(labelMainClass);

		textMainClass = new Text(comp, SWT.BORDER);
		textMainClass.setMessage("Main class");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textMainClass);
		textMainClass.addModifyListener((event) -> updateLaunchConfigurationDialog());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// This is currently not in use.
		// TODO(Jonas): Evaluate if this could be used
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String defaultLocation = preferences.get(Constants.PREFERENCES_SDK_LOCATION, null);
			String location = configuration.getAttribute(Constants.PREFERENCES_SDK_LOCATION, defaultLocation);
			textSdkLocation.setText(location);

			String mainClass = configuration.getAttribute(Constants.LAUNCH_MAIN_CLASS, "main.dart");
			textMainClass.setText(mainClass);

			// String selectedProject =
			comboProject.setText(configuration.getAttribute(Constants.LAUNCH_SELECTED_PROJECT, ""));
			this.setDirty(true);
		} catch (CoreException e) {
			// ignore here
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(Constants.PREFERENCES_SDK_LOCATION, textSdkLocation.getText());
		configuration.setAttribute(Constants.LAUNCH_MAIN_CLASS, textMainClass.getText());
		configuration.setAttribute(Constants.LAUNCH_SELECTED_PROJECT, comboProject.getText());
	}

	@Override
	public String getName() {
		return "Dart Configuration";
	}

	private IProject[] getProjectsInWorkspace() {
		var workspace = ResourcesPlugin.getWorkspace();
		var root = workspace.getRoot();
		return root.getProjects();
	}
}
