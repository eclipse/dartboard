package org.eclipse.dartboard.stagehand;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.input.NullInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.dartboard.Constants;
import org.eclipse.dartboard.Messages;
import org.eclipse.dartboard.pub.PubUtil;
import org.eclipse.dartboard.util.PlatformUIUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StagehandGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(StagehandGenerator.class);

	public static void generate(StagehandTemplate generator, IProject project) {
		if (project == null) {
			Objects.requireNonNull(project);
		}

		if (generator == null) {
			IFile pubspecFile = project.getFile(Constants.PUBSPEC);
			if (!pubspecFile.exists()) {
				try {
					pubspecFile.create(new NullInputStream(0), true, null);
				} catch (CoreException e) {
					LOG.error("Could not create pubspec.yaml file", e); //$NON-NLS-1$
				}
			}
		} else {

			Job stagehandJob = Job.create(
					NLS.bind(Messages.Stagehand_GeneratorJob_Name, generator.getName(), project.getName()), monitor -> {
						ProcessBuilder builder = PubUtil
								.getPubProcessBuilder("global", "run", "stagehand", generator.getName()) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								.directory(project.getLocation().toFile());
						try {
							Process process = builder.start();
							int exit = process.waitFor();
							if (exit != 0) {
								Display.getDefault().asyncExec(() -> {
									MessageDialog.openError(PlatformUIUtil.getActiveShell(),
											Messages.Stagehand_GeneratorJobFail_Title,
											NLS.bind(Messages.Stagehand_GeneratorJobFail_Title, generator.getName(),
													project.getLocation()));
								});
							}

						} catch (IOException | InterruptedException e) {
							LOG.error("Could not generate stagehand template", e); //$NON-NLS-1$
						}
					});

			stagehandJob.schedule();

			stagehandJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					try {
						project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					} catch (CoreException e) {
						LOG.error("Could not refresh project", e); //$NON-NLS-1$
					}
				}
			});
		}

	}
}
