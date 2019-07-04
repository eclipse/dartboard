package org.eclipse.dartboard.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;

public class IsDartProjectPropertyTester extends PropertyTester {

	private static final String IS_DART_PROJECT_PROPERTY = "isDartProject"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (IS_DART_PROJECT_PROPERTY.equals(property)) {
			IResource resource = Adapters.adapt(receiver, IResource.class);
			if (resource == null) {
				return false;
			}

			IProject project = resource.getProject();
			if (project == null) {
				return false;
			}

			try {
				for (IResource res : project.members(IResource.DEPTH_INFINITE)) {
					if ("pubspec.yaml".equals(res.getName()) || "dart".equals(res.getFileExtension())) { //$NON-NLS-1$ //$NON-NLS-2$
						return true;
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

}
