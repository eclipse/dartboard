package org.eclipse.dartboard;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dartboard.pub.PubspecChangeListener;
import org.eclipse.ui.IStartup;

public class StartupListener implements IStartup {

	@Override
	public void earlyStartup() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new PubspecChangeListener());
	}
}
