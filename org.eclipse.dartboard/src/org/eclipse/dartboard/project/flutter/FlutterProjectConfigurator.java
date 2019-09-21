/*******************************************************************************
 * Copyright (c) 2019 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jonas Hungershausen
 *******************************************************************************/
package org.eclipse.dartboard.project.flutter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

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

/**
 * @author jonas
 *
 */
public class FlutterProjectConfigurator implements ProjectConfigurator {

	// Using the of Dart-Code. See
	// https://github.com/Dart-Code/Dart-Code/blob/b8c0f6f138b31748822c0f4ff471ddfa277c7cfa/src/extension/sdk/utils.ts#L340
	private static final Pattern FLUTTER_SDK = Pattern.compile("sdk\\s*:\\s*flutter", Pattern.CASE_INSENSITIVE);

	private static final ILog LOG = Platform.getLog(FlutterProjectConfigurator.class);

	@Override
	public Set<File> findConfigurableLocations(File root, IProgressMonitor monitor) {
		Set<File> files = new HashSet<>();

		try {
			Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					if (path.endsWith(Constants.PUBSPEC)) {

						try (Scanner scanner = new Scanner(path.toFile())) {
							while (scanner.hasNextLine()) {
								if (FLUTTER_SDK.matcher(scanner.nextLine().trim()).matches()) {
									files.add(path.toFile().getParentFile());
									break;
								}
							}
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (

		IOException e) {
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
