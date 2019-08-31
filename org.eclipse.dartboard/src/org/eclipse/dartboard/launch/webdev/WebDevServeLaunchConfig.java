package org.eclipse.dartboard.launch.webdev;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public class WebDevServeLaunchConfig extends LaunchConfigurationDelegate {

	public static final String OUTPUT_DIRECTORY = "output_directory";
	public static final String VERBOSE_OUTPUT = "verbose_output";
	public static final String AUTO_PERFORM = "auto_perform";
	public static final String HOSTNAME = "hostname";
	public static final String PORT = "port";
	public static final String INJECT_CLIENT = "inject_client";

	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final String DEFAULT_PORT = "8080";
	public static final String DEFAULT_OUTPUT_DIRECTORY = "";
	public static final String DEFAULT_AUTO_PERFORM = "";
	public static final boolean DEFAULT_INJECT_CLIENT = true;
	public static final boolean DEFAULT_VERBOSE_OUTPUT = false;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

	}

}
