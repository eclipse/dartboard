package org.eclipse.dartboard.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.util.StatusUtil;

public class IsFlutterProjectPropertyTester extends PropertyTester {

	private static final ILog LOG = Platform.getLog(IsFlutterProjectPropertyTester.class);

	private static final String IS_FLUTTER_PROJECT_PROPERTY = "isFlutterProject";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (IS_FLUTTER_PROJECT_PROPERTY.equals(property)) {
			IResource resource = Adapters.adapt(receiver, IResource.class);
			if (resource == null) {
				return false;
			}

			IProject project = resource.getProject();
			if (project == null) {
				return false;
			}
			if (project.findMember(Constants.PUBSPEC) != null && project.findMember("android") != null
					&& project.findMember("ios") != null) {
				return true;
			}

			try {
				for (IResource res : project.members()) {
					if ("dart".equals(res.getFileExtension())) { //$NON-NLS-1$
						return true;
					}
				}
			} catch (CoreException e) {
				LOG.log(StatusUtil.createError("Couldn't list members of project " + project.getName(), e)); //$NON-NLS-1$
			}
		}
		return false;
	}

}
