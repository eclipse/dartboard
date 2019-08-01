package org.eclipse.dartboard.test.propertytester;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dartboard.propertytester.IsDartProjectPropertyTester;
import org.junit.Test;

public class IsDartProjectPropertyTesterTest {

	private static final String IS_DART_PROJECT_PROPERTY = "isDartProject";

	@Test
	public void test__ExistingPubspec__ReturnsTrue() {
		IsDartProjectPropertyTester tester = new IsDartProjectPropertyTester();
		IProject project = mock(IProject.class);

		when(project.getProject()).thenReturn(project);
		when(project.findMember("pubspec.yaml")).thenReturn(mock(IFile.class));

		boolean result = tester.test(project, IS_DART_PROJECT_PROPERTY, null, null);
		assertTrue(result);
	}

	@Test
	public void test__ExistingDartFile__ReturnsTrue() throws Exception {
		IsDartProjectPropertyTester tester = new IsDartProjectPropertyTester();
		IProject project = mock(IProject.class);
		IResource dartFile = mock(IResource.class);

		when(project.getProject()).thenReturn(project);
		when(project.findMember("pubspec.yaml")).thenReturn(null);
		when(project.members()).thenReturn(new IResource[] { dartFile });
		when(dartFile.getFileExtension()).thenReturn("dart");

		boolean result = tester.test(project, IS_DART_PROJECT_PROPERTY, null, null);
		assertTrue(result);
	}

	@Test
	public void test__MemberListingFails__ReturnsFalse() throws Exception {
		IsDartProjectPropertyTester tester = new IsDartProjectPropertyTester();
		IProject project = mock(IProject.class);

		when(project.getProject()).thenReturn(project);
		when(project.findMember("pubspec.yaml")).thenReturn(null);
		when(project.members()).thenThrow(new CoreException(Status.CANCEL_STATUS));

		boolean result = tester.test(project, IS_DART_PROJECT_PROPERTY, null, null);
		assertFalse(result);
	}

	@Test
	public void test__NullResource__ReturnsFalse() throws Exception {
		IsDartProjectPropertyTester tester = new IsDartProjectPropertyTester();

		boolean result = tester.test(null, IS_DART_PROJECT_PROPERTY, null, null);
		assertFalse(result);
	}

	@Test
	public void test__NullResourceProject__ReturnsFalse() throws Exception {
		IsDartProjectPropertyTester tester = new IsDartProjectPropertyTester();
		IResource resource = mock(IResource.class);

		when(resource.getProject()).thenReturn(null);

		boolean result = tester.test(resource, IS_DART_PROJECT_PROPERTY, null, null);
		assertFalse(result);
	}
}
