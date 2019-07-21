package org.eclipse.dartboard.pub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.util.DartUtil;
import org.eclipse.osgi.util.NLS;

public class Pub {

	private Map<IProject, Job> pubSyncJobs;
	private static Pub instance;

	public Pub() {
		pubSyncJobs = new HashMap<>();
	}

	public void get(IProject project) {
		// Sometimes the same project has 2 or more pubspec.yaml files (see stagehand
		// project).
		// This leads to the same job being executed once for every pubspec file.
		// The result is a huge performance hit.
		// Instead we store the active jobs for any project and cancel already running
		// jobs
		Job active = pubSyncJobs.get(project);
		if (active != null) {
			active.cancel();
			pubSyncJobs.remove(project);
		}

		Job pubSync = Job.create(NLS.bind(Messages.PubSync_Job_Name, project.getName()), (monitor) -> {
			IPath location = project.getLocation();
//			if (location == null) {
//				monitor.setCanceled(true);
//			} TODO: Check how to handle this case?

			String[] commands = { DartUtil.getExecutable("pub"), "get" }; //$NON-NLS-1$ //$NON-NLS-2$
			ProcessBuilder builder = new ProcessBuilder(commands);
			builder.directory(location.makeAbsolute().toFile());
			Process process = null;
			try {
				process = builder.start();
			} catch (IOException e1) {
				// TODO: Check how to handle this case?
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

				SubMonitor mo = SubMonitor.convert(monitor, 100);

				String taskName = reader.readLine();
				while (taskName != null) {
					if (taskName.equalsIgnoreCase(Messages.PubSync_Task_ResolvingDependencies)
							|| taskName.equalsIgnoreCase(Messages.PubSync_Task_PrecompilingExecutables)) {
						mo.split(50);
						mo.setTaskName(taskName);
					}
					if (mo.isCanceled() && process != null) {
						reader.close();
						process.destroy();
					}
					taskName = reader.readLine();
				}
			} catch (IOException e) {
				// TODO: Check how to handle this case?
			}
		});
		pubSync.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				pubSyncJobs.remove(project);
				try {
					project.refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException e) {
					// Ignore here?
				}
			}
		});
		pubSync.setPriority(Job.LONG);
		pubSync.schedule(1000);
		pubSyncJobs.put(project, pubSync);
	}

	public static Pub getInstance() {
		if (instance == null) {
			instance = new Pub();
		}
		return instance;
	}
}
