package org.eclipse.dartboard.launch;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

public abstract class BaseLaunchConfigTab extends AbstractLaunchConfigurationTab {

	protected Combo comboProject;
	protected Text textSdkLocation;
	private Image image;

	public BaseLaunchConfigTab(String iconPath) {
		Bundle bundle = Platform.getBundle(Constants.PLUGIN_ID);
		URL fileURL = bundle.getEntry(iconPath);
		ImageDescriptor createFromURL = ImageDescriptor.createFromURL(fileURL);
		image = createFromURL.createImage();
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.NONE);
		setControl(comp);
		GridLayoutFactory.fillDefaults().applyTo(comp);
		comp.setLayout(new GridLayout(1, true));

		createCommonGroup(comp);
		createExtraControls(comp);
	}

	private void createCommonGroup(Composite parent) {
		Group commonGroup = new Group(parent, SWT.NONE);
		commonGroup.setText("Common");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(commonGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(commonGroup);

		Label labelProject = new Label(commonGroup, SWT.NONE);
		labelProject.setText(Messages.Launch_Project);
		GridDataFactory.swtDefaults().applyTo(labelProject);

		comboProject = new Combo(commonGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		for (IProject project : getProjectsInWorkspace()) {
			comboProject.add(project.getName());
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboProject);
		comboProject.addModifyListener(event -> updateLaunchConfigurationDialog());

		Label labelSdkLocation = new Label(commonGroup, SWT.NONE);
		labelSdkLocation.setText(Messages.Preference_SDKLocation);
		GridDataFactory.swtDefaults().applyTo(labelSdkLocation);

		textSdkLocation = new Text(commonGroup, SWT.BORDER);
		textSdkLocation.setMessage(Messages.Launch_SDKLocation_Message);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textSdkLocation);
		textSdkLocation.addModifyListener(event -> updateLaunchConfigurationDialog());
	}

	private IProject[] getProjectsInWorkspace() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		return root.getProjects();
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(Constants.PREFERENCES_SDK_LOCATION, textSdkLocation.getText());
		configuration.setAttribute(Constants.LAUNCH_SELECTED_PROJECT, comboProject.getText());
		saveExtraAttributes(configuration);
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
		}
	}

	public abstract void createExtraControls(Composite parent);

	public abstract void saveExtraAttributes(ILaunchConfigurationWorkingCopy configuration);
}
