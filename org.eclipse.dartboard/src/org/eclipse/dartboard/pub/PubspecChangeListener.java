package org.eclipse.dartboard.pub;

import static org.eclipse.core.resources.IResourceDelta.ADDED;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.util.GlobalConstants;

public class PubspecChangeListener implements IResourceChangeListener {

	private static final ILog LOG = Platform.getLog(PubspecChangeListener.class);

	private List<IPubService> pubServices;

	public PubspecChangeListener(List<IPubService> pubServices) {
		this.pubServices = pubServices;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {

					@Override
					public boolean visit(IResourceDelta delta) throws CoreException {
						IResource resource = delta.getResource();
						if (resource.getType() == IResource.FILE && GlobalConstants.PUBSPEC_YAML.equals(resource.getName())
								&& (delta.getKind() == ADDED || isContentChanged(delta))) {
							for (IPubService abstractPubService : pubServices) {
								// At this point, resource is the pubspec.yaml
								if (abstractPubService.test(resource.getProject())) {
									abstractPubService.get(resource.getProject());
									break;
								}
							}
						}
						return true;
					}
				});
			} catch (CoreException e) {
				LOG.log(DartLog.createError(e.getMessage(), e));
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
