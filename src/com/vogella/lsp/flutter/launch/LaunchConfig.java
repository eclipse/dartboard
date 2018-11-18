package com.vogella.lsp.flutter.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public class LaunchConfig extends LaunchConfigurationDelegate {

	public static final String CONFIG_DART_SDK = "dart-sdk";
	public static final String CONFIG_MAIN_CLASS = "main-class";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("asdasdasd");

	}


}
