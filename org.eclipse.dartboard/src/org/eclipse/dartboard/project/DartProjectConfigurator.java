package org.eclipse.dartboard.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.util.StatusUtil;
import org.eclipse.ui.wizards.datatransfer.ProjectConfigurator;

public class DartProjectConfigurator implements ProjectConfigurator {

	private static final ILog LOG = Platform.getLog(DartProjectConfigurator.class);

	@Override
	public Set<File> findConfigurableLocations(File root, IProgressMonitor monitor) {
		Set<File> files = new HashSet<>();

		try {
			Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.endsWith(Constants.PUBSPEC)) {
						files.add(file.toFile().getParentFile());
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			LOG.log(StatusUtil.createError("Couldn't walk children directories", e)); //$NON-NLS-1$
		}
		return files;
	}

	@Override
	public boolean shouldBeAnEclipseProject(IContainer container, IProgressMonitor monitor) {
		return true;
	}

	@Override
	public Set<IFolder> getFoldersToIgnore(IProject project, IProgressMonitor monitor) {
		// Currently there are no to ignore in Dart projects
		return Collections.emptySet();
	}

	@Override
	public boolean canConfigure(IProject project, Set<IPath> ignoredPaths, IProgressMonitor monitor) {
		// Do nothing, as everything is handled in ListenerService
		return false;
	}

	@Override
	public void configure(IProject project, Set<IPath> ignoredPaths, IProgressMonitor monitor) {
		// Do nothing, as everything is handled in ListenerService
	}

}
