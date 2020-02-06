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
 *     Andrew Bowley
 *******************************************************************************/
package org.eclipse.dartboard.preferences;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.logging.DartLog;
import org.eclipse.dartboard.messages.Messages;
import org.eclipse.dartboard.util.DartSdkChecker;
import org.eclipse.dartboard.util.GlobalConstants;
import org.eclipse.dartboard.util.PlatformUtil;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Field editor to configure Flutter SDK. Combines a text control with a browse
 * button to pop up a directory selection dialog. The user can type or paste
 * into the text control as an alternative to browsing to the directory.
 *
 * @author jonas
 * @author Andrew Bowley
 */
public class FlutterSDKLocationFieldEditor extends DirectoryFieldEditor {

	private static final ILog LOG = Platform.getLog(FlutterSDKLocationFieldEditor.class);

	/** Dart SDK location checker */
	private final DartSdkChecker dartSdkChecker;
	/** Flag to disable validation while directory browsing in progress */
	private volatile boolean inChangeDirectory;
	/** Semaphore used to prevent validation reentry */
	private Semaphore validationGuard;

	/**
	 * Valid status flag replaces same flag in super class so enable/disable 'Apply'
	 * buttons work correctly
	 */
	private boolean isValid;
	/**
	 * Directory Dialog returned value. The 'inChangeDirectory' flag is cleared when
	 * this variable is set .
	 */
	private String newDirectory;

	/**
	 * Construct FlutterSDKLocationFieldEditor object
	 *
	 * @param preferencesKey Storage key
	 * @param labelText      the label text of the field editor
	 * @param parent         the parent of the field editor's control
	 */
	public FlutterSDKLocationFieldEditor(String preferencesKey, String label, Composite parent) {
		super(preferencesKey, label, parent);
		Shell shell = parent.getShell();
		dartSdkChecker = PlatformUtil.getInstance().getDartSdkChecker(shell, true);
		setValidateStrategy(VALIDATE_ON_KEY_STROKE);
		// Automatically invalidate an empty value
		setEmptyStringAllowed(false);
		inChangeDirectory = false;
		// Semaphore used to prevent validation reentry, which may costly as it may
		// include executing a shell command
		validationGuard = new Semaphore(1);
	}

	public void setEnabled(boolean enabled) {
		getTextControl().setEnabled(enabled);
	}

	/**
	 * Returns valid flag. Super isValid() has too much latency to be useable.
	 *
	 * @return boolean
	 */
	@Override
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Sets the preference store used by this field editor.
	 *
	 * @param store the preference store, or <code>null</code> if none
	 * @see #getPreferenceStore
	 */
	@Override
	public void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
		if (store != null)
			// Check if default exists
			isValid = doCheckState();
		else
			isValid = true;
	}

	/**
	 * Handle Browse button pressed
	 *
	 * @return Directory Dialog, whiech may be null if the user cancels
	 */
	@Override
	protected String changePressed() {
		// Flag Directory Dialog being displayed for doCheckState() logic.
		// The focus change triggered by dismissing the dialog can preempt the update
		// of the text field
		newDirectory = null;
		inChangeDirectory = true;
		newDirectory = super.changePressed();
		if (newDirectory == null)
			inChangeDirectory = false;
		return newDirectory;
	}

	/**
	 * Checks Flutter SDK location selected/entered by user and returns valid flag
	 *
	 * @return boolean
	 */
	@Override
	protected boolean doCheckState() {
		if (!getPreferenceStore().getBoolean(GlobalConstants.P_FLUTTER_ENABLED)) {
			return true;
		}
		if (inChangeDirectory) {
			// Waiting for user to select directory
			if (newDirectory != null) {
				inChangeDirectory = false;
				isValid = !newDirectory.isEmpty() && isValidFlutterSDK(newDirectory);
			} else
				// Do not display error message while waiting for result
				return false;
		} else {
			String location = getTextControl().getText();
			isValid = !location.isEmpty() && isValidFlutterSDK(location);
		}
		if (isValid) {
			setErrorMessage(null);
			showMessage(null);
		} else {
			setErrorMessage(Messages.Preference_SDKNotFound_Message);
			showErrorMessage();
		}
		return isValid;
	}

	/**
	 * Adds text control text modification listener
	 *
	 * @param listener ModifyListener
	 */
	protected void addModifyListener(ModifyListener listener) {
		// Filter modifications made from using the Browse button.
		ModifyListener modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (!inChangeDirectory)
					listener.modifyText(e);
			}
		};
		getTextControl().addModifyListener(modifyListener);
	}

	/**
	 * Checks if a given path is the root directory of a Dart SDK installation.
	 *
	 * Returns false if the path does not exist or the given location can not be
	 * converted to a {@link Path}.
	 *
	 * Similarly if the Path is not a directory, false is returned.
	 *
	 * If the location is a symbolic link but it can not be resolved, false is
	 * returned.
	 *
	 * If the process to test the version string returned by the Dart executable can
	 * not be executed, false is returned.
	 *
	 * Finally, if the returned version string does not start with "Dart VM
	 * version", false is returned.
	 *
	 * @param location - A {@link String} that should be checked to be a Dart SDK
	 *                 root directory.
	 * @return <code>false</code> if the location is not a Dart SDK root directory,
	 *         <code>true</code> otherwise.
	 */
	private boolean isValidFlutterSDK(String location) {
		try {
			if (!validationGuard.tryAcquire())
				// Validation already in progress so just return an optimistic interim result
				return true;
			return dartSdkChecker.isValidDartSDK(location);
		} catch (ExecutionException e) {
			LOG.log(DartLog.createError("Error verifying Dart SDK", e)); //$NON-NLS-1$
			return false;
		} finally {
			validationGuard.release();
		}
	}

}
