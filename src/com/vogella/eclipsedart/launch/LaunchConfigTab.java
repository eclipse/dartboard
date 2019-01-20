package com.vogella.eclipsedart.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class LaunchConfigTab extends AbstractLaunchConfigurationTab {

	private Text textSdkLocation;
	private Text textMainClass;

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.BORDER);
		setControl(comp);

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

		Label labelSdkLocation = new Label(comp, SWT.NONE);
		labelSdkLocation.setText("Dart SDK Location: ");
		GridDataFactory.swtDefaults().applyTo(labelSdkLocation);

		textSdkLocation = new Text(comp, SWT.BORDER);
		textSdkLocation.setMessage("The location the dart sdk is installed to");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textSdkLocation);

		Label labelMainClass = new Label(comp, SWT.NONE);
		labelMainClass.setText("Main class: ");
		GridDataFactory.swtDefaults().applyTo(labelMainClass);

		textMainClass = new Text(comp, SWT.BORDER);
		textMainClass.setMessage("Main class");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textMainClass);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String location = configuration.getAttribute(LaunchConfig.CONFIG_DART_SDK, "/usr/lib/dart");
			textSdkLocation.setText(location);

			String mainClass = configuration.getAttribute(LaunchConfig.CONFIG_MAIN_CLASS, "main.dart");
			textMainClass.setText(mainClass);
		} catch (CoreException e) {
			// ignore here
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchConfig.CONFIG_DART_SDK, textSdkLocation.getText());
		configuration.setAttribute(LaunchConfig.CONFIG_MAIN_CLASS, textMainClass.getText());
	}

	@Override
	public String getName() {
		return "Dart Configuration";
	}

}
