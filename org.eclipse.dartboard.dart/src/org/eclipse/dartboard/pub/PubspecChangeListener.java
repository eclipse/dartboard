package org.eclipse.dartboard.pub;

import static org.eclipse.core.resources.IResourceDelta.ADDED;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.dart.Constants;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.dartboard.util.StatusUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PubspecChangeListener implements IResourceChangeListener {

	private static final ILog LOG = Platform.getLog(PubspecChangeListener.class);

	private ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore(Constants.PLUGIN_ID);

	private PubService pub;

	public PubspecChangeListener(PubService pubService) {
		pub = pubService;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		boolean syncPub = preferences.getBoolean(Constants.PREFERENCES_SYNC_PUB);
		// Don't sync if the user has turned off this option in the preferences
		if (!syncPub) {
			return;
		}

		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {

					@Override
					public boolean visit(IResourceDelta delta) throws CoreException {
						IResource resource = delta.getResource();
						if (resource.getType() == IResource.FILE && Constants.PUBSPEC.equals(resource.getName())
								&& (delta.getKind() == ADDED || isContentChanged(delta))) {
							boolean offline = preferences.getBoolean(Constants.PREFERENCES_OFFLINE_PUB);
							pub.get(resource.getProject(), offline);
						}
						return true;
					}
				});
			} catch (CoreException e) {
				LOG.log(StatusUtil.createError(e.getMessage(), e));
			}
		}
	}

	/**
	 * Determines whether an {@link IResourceDelta} indicates that a
	 * {@link IResource}'s content was changed.
	 *
	 * @param delta - The {@link IResourceDelta} to be checked
	 * @return <code>true</code> if the {@link IResource}'s content was changed,
	 *         false otherwise.
	 */
	private boolean isContentChanged(IResourceDelta delta) {
		return delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.CONTENT) != 0;
	}
}
