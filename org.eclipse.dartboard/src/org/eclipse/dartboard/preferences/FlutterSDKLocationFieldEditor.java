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
package org.eclipse.dartboard.preferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.dartboard.util.PlatformUIUtil;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author jonas
 *
 */
public class FlutterSDKLocationFieldEditor extends DirectoryFieldEditor {

	private static final ILog LOG = Platform.getLog(FlutterSDKLocationFieldEditor.class);

	public FlutterSDKLocationFieldEditor(String preferencesKey, String label, Composite parent) {
		super(preferencesKey, label, parent);
		setValidateStrategy(VALIDATE_ON_KEY_STROKE);
	}

	@Override
	protected boolean doCheckState() {
		if (!getPreferenceStore().getBoolean(GlobalConstants.P_FLUTTER_ENABLED)) {
			return true;
		}
		String location = getTextControl().getText();
		Optional<Path> optionalPath = getPath(location);
		if (!optionalPath.isPresent()) {
			setErrorMessage(Messages.Preference_SDKNotFound_Message);
			showErrorMessage();
			return false;
		}
		return true;
	}

	private Optional<Path> getPath(String location) {

		if (location.isEmpty()) {
			return Optional.empty();
		}

		Path path = null;
		// On Windows if a certain wrong combination of characters are entered a
		// InvalidPathException is thrown. In that case we can assume that the location
		// entered is not a valid Dart SDK directory either.
		try {
			path = Paths.get(location)
					.resolve("bin" + File.separator + (PlatformUIUtil.IS_WINDOWS ? "flutter.bat" : "flutter")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (InvalidPathException e) {
			return Optional.empty();
		}

		// See https://github.com/eclipse/dartboard/issues/103
		// List<String> blacklist = Lists.newArrayList("/bin/dart", "/usr/bin/dart");
		// If the entered file doesn't exist, there is no need to run it
		// Similarly if the file is a directory it can't be the dart executable
		if (!Files.exists(path) || Files.isDirectory(path)) {
			return Optional.empty();
		}
		// Follow symbolic links
		try {
			path = path.toRealPath();
		} catch (IOException e1) {
			LOG.log(DartLog.createError("Couldn't follow symlink", e1)); //$NON-NLS-1$
			return Optional.empty();
		}
		return Optional.of(path);
	}

	public void setEnabled(boolean enabled) {
		getTextControl().setEnabled(enabled);
	}

	protected void addModifyListener(ModifyListener listener) {
		getTextControl().addModifyListener(listener);
	}

}
