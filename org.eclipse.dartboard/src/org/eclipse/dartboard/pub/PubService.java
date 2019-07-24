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
 *     Jonas Hungershausen - initial API and implementation
 *******************************************************************************/
package org.eclipse.dartboard.pub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.DartUtil;
import org.eclipse.dartboard.util.StatusUtil;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Singleton} service that exposes various Pub functions.
 * 
 * Currently the main purpose of this class is to execute the {@code pub get} in
 * a given directory (project).
 * 
 * @author Jonas Hungershausen
 * @see PubspecChangeListener
 * 
 */
public class PubService {

	private static final Logger LOG = LoggerFactory.getLogger(PubService.class);

	private static PubService instance;

	private PubService() {
	}

	/**
	 * A {@link Map} holding an {@link IProject} and a {@link Job} as ideally there
	 * should only be one {@code pub get} job running per project.
	 */
	private Map<IProject, Job> pubGetJobs = new HashMap<>();

	/**
	 * Runs the {@code pub get} command in a {@link IProject}'s location.
	 * 
	 * If there already is a job running for a {@link IProject} it is cancelled.
	 * 
	 * 
	 * @param project
	 */
	public void get(IProject project, boolean offline) {
		// Sometimes the same project has 2 or more pubspec.yaml files (see stagehand
		// project).
		// This leads to the same job being executed once for every pubspec file.
		// The result is a huge performance hit.
		// Instead we store the active jobs for any project and cancel already running
		// jobs
		Job active = pubGetJobs.get(project);
		if (active != null) {
			active.cancel();
			pubGetJobs.remove(project);
		}

		List<String> commands = new ArrayList<>();
		commands.add(DartUtil.getTool("pub")); //$NON-NLS-1$
		commands.add("get"); //$NON-NLS-1$

		// Use --offline flag if applicable
		if (offline) {
			commands.add("--offline"); //$NON-NLS-1$
		}

		Job pubSync = Job.create(NLS.bind(Messages.PubSync_Job_Name, project.getName()), (monitor) -> {
			IPath location = project.getLocation();
			if (location == null) {
				return StatusUtil.createError(NLS.bind(Messages.PubSync_CouldNotDeterminePath, project.getName()));
			}

			ProcessBuilder builder = new ProcessBuilder(commands);
			builder.directory(location.makeAbsolute().toFile());
			Process process = null;
			try {
				process = builder.start();
			} catch (IOException exception) {
				return StatusUtil.createError(Messages.PubSync_CouldNotStartProcess, exception);
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				SubMonitor mo = SubMonitor.convert(monitor, 100);

				String taskName;
				while ((taskName = reader.readLine()) != null) {
					// We only want updates to the progress bar if one of the 2 operations are
					// started. Not for every dependency added or compilation step done.
					if (taskName.equalsIgnoreCase(Messages.PubSync_Task_ResolvingDependencies)
							|| taskName.equalsIgnoreCase(Messages.PubSync_Task_PrecompilingExecutables)) {
						mo.split(50);
						mo.setTaskName(taskName);
					}
				}
			} catch (IOException exception) {
				return StatusUtil.createError(Messages.PubSync_CouldNotStartProcess, exception);
			}
			return Status.OK_STATUS;
		});

		pubSync.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				// Always remove the job from the pubGetJobs map when it finished
				pubGetJobs.remove(project);

				IStatus result = event.getResult();
				// Only refresh the project if return status is OK or if the sync was cancelled
				// by the user
				if (result.isOK() || result.getCode() == IStatus.CANCEL) {
					try {
						project.refreshLocal(IResource.DEPTH_INFINITE, null);
					} catch (CoreException exception) {
						LOG.error(NLS.bind(Messages.Error_CouldNotRefreshResource, project.getName()), exception);
					}
				}
			}
		});
		pubSync.setPriority(Job.LONG);
		pubSync.setUser(true);
		// A temporary fix for the situation on project import where more than one
		// pubspec file could be in the project (see stagehand). To make the jobs
		// cancelable instantly we wait for 1 second before starting them.
		pubSync.schedule(1000);
		pubGetJobs.put(project, pubSync);
	}

	/**
	 * Returns an instance of {@link PubService}
	 * 
	 * If {@link #instance} is null, a new instance is created.
	 * 
	 * @return The instance of {@link PubService} defined in {@link #instance}
	 */
	public static PubService getInstance() {
		// TODO: Turn this class into a @Inject'able service?
		if (instance == null) {
			instance = new PubService();
		}
		return instance;
	}
}
