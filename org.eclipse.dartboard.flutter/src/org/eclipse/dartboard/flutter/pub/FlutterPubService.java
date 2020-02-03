package org.eclipse.dartboard.flutter.pub;

import org.eclipse.core.resources.IProject;
import org.eclipse.dartboard.flutter.sdk.FlutterSDK;
import org.eclipse.dartboard.flutter.util.IsFlutterProjectPropertyTester;
import org.eclipse.dartboard.pub.IPubService;

public class FlutterPubService implements IPubService {

	@Override
	public void get(IProject project) {
		FlutterSDK sdk = FlutterSDK.forProject(project);
		sdk.pubGet();
	}

	@Override
	public boolean test(IProject project) {
		return new IsFlutterProjectPropertyTester().test(project, "isFlutterProject", null, null);
	}

}
