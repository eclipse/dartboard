package org.eclipse.dartboard.dart.pub;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.dart.Constants;
import org.eclipse.dartboard.dart.propertytester.IsDartProjectPropertyTester;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.dartboard.pub.IPubService;
import org.eclipse.dartboard.util.PlatformUIUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DartPubService implements IPubService {

	private static final ILog LOG = Platform.getLog(DartPubService.class);

	private ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore(Constants.PLUGIN_ID);

	@Override
	public boolean test(IProject project) {
		return new IsDartProjectPropertyTester().test(project, "isDartProject", null, null);
	}

	@Override
	public void get(IProject project) {
		IPath location = project.getLocation();
		if (location == null) {
			LOG.log(DartLog.createError(NLS.bind(Messages.PubSync_CouldNotDeterminePath, project.getName())));
			return;
		}
		boolean offline = preferences.getBoolean(Constants.PREFERENCES_OFFLINE_PUB);
		IWorkbench workbench = PlatformUIUtil.getWorkbench();
		PubService pub = workbench.getService(PubService.class);
		pub.get(project, offline);
	}

}
