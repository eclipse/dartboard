package com.vogella.eclipsedart.launch;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.eclipsedart.Constants;
import com.vogella.eclipsedart.launch.console.DartConsoleFactory;

public class LaunchConfig extends LaunchConfigurationDelegate {

	private static final Logger LOG = LoggerFactory.getLogger(LaunchConfig.class);
	
	IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEY);

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		if (!mode.equalsIgnoreCase("run")) {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(null, "Debug is not yet supported.",
						"Debug launch is not yet supported. Please use the standard run config.");
			});
		}

		String mainClass = configuration.getAttribute(Constants.LAUNCH_MAIN_CLASS, "main.dart");
		String sdk = configuration.getAttribute(Constants.PREFERENCES_SDK_LOCATION, "");
		String projectName = configuration.getAttribute(Constants.LAUNCH_SELECTED_PROJECT, "");
		var project = getProject(projectName);
		if (!project.exists()) {
			MessageDialog.openError(null, "No project selected", "Please select a project in the run configuration.");
			return;
		}

		String location = project.getLocation().toOSString();
		var processBuilder = new ProcessBuilder(sdk + "/bin/dart", location + "/" + mainClass);

		try {
			var process = processBuilder.start();
			new DartConsoleFactory(process.getInputStream()).openConsole();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}

	}

	public IProject getProject(String name) {
		var workspace = ResourcesPlugin.getWorkspace();
		var root = workspace.getRoot();
		return root.getProject(name);
	}

}
