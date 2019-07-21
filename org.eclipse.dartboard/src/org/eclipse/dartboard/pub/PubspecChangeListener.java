package org.eclipse.dartboard.pub;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PubspecChangeListener implements IResourceChangeListener {

	private ScopedPreferenceStore preferences;

	private Pub pub;

	public PubspecChangeListener() {
		preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID);
		pub = Pub.getInstance();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		boolean syncPub = preferences.getBoolean(Constants.PREFERENCES_SYNC_PUB);
		if (!syncPub) {
			return;
		}

		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new DeltaPrinter());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private class DeltaPrinter implements IResourceDeltaVisitor {

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (delta.getResource().getType() == IResource.FILE) {
				IFile file = (IFile) delta.getResource();
				if (file.getName().equalsIgnoreCase(Constants.PUBSPEC)
						&& (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED)) {
					pub.get(delta.getResource().getProject());
				}
			}
			return true;
		}

	}

}
