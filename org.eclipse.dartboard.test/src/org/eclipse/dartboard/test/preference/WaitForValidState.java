package org.eclipse.dartboard.test.preference;

import org.eclipse.reddeer.common.condition.WaitCondition;

/**
 * RedDeer WaitCondition implementation for wait for preference dialog to
 * display it's valid state. This normally means the dialog header displays a
 * title rather than an error message.
 * 
 * @author Andrew Bowley
 *
 */
public class WaitForValidState implements WaitCondition {

	private final String title;
	private final ValidPreferenceState preferencePage;
	private boolean isValid;

	/**
	 * Construct WaitForValidState object
	 * 
	 * @param preferencePage Preference page implementing ValidPreferenceState
	 *                       interface
	 * @param title          Title displayed in dialog header
	 */
	public WaitForValidState(ValidPreferenceState preferencePage, String title) {
		this.preferencePage = preferencePage;
		this.title = title;
	}

	@Override
	public boolean test() {
		isValid = preferencePage.isValid();
		return isValid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean getResult() {
		return isValid;
	}

	@Override
	public String description() {
		return title + " preference dialog transition to valid state";
	}

	@Override
	public String errorMessageWhile() {
		return "Waiting for " + description();
	}

	@Override
	public String errorMessageUntil() {
		return "Until " + description();
	}

}
