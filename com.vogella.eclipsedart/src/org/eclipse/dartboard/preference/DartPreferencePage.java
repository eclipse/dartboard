package org.eclipse.dartboard.preference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DartPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final Logger LOG = LoggerFactory.getLogger(DartPreferencePage.class);
	
	private Text sdkLocationTextField;
	private Label versionLabel;

	private IEclipsePreferences preferences;

	@Override
	public void init(IWorkbench workbench) {
		preferences = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEY);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 2;
		composite.setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Label label = new Label(composite, SWT.NONE);
		label.setText("Dart SDK Location");

		sdkLocationTextField = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		sdkLocationTextField.setLayoutData(gridData);

		String dartLocation = preferences.get(Constants.PREFERENCES_SDK_LOCATION, "");
		sdkLocationTextField.setText(dartLocation);

		versionLabel = new Label(composite, SWT.NONE);
		getVersion(dartLocation).ifPresent((version) -> {
			versionLabel.setText(version);
		});
		return composite;
	}

	@Override
	public boolean performOk() {
		String sdkLocation = sdkLocationTextField.getText();
		var optionalVersion = getVersion(sdkLocation);

		optionalVersion.ifPresentOrElse((version) -> {
			preferences.put(Constants.PREFERENCES_SDK_LOCATION, sdkLocationTextField.getText());
			versionLabel.setText(version);
		}, () -> {
			MessageDialog.openError(null, "Not a valid SDK location",
					"The given location \"" + sdkLocation + "\" is not a valid dart SDK location.");
			String oldLocation = preferences.get(Constants.PREFERENCES_SDK_LOCATION, "");
			sdkLocationTextField.setText(oldLocation);
		});

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			LOG.error(e.getMessage());
		}
		return super.performOk();
	}

	private Optional<String> getVersion(String location) {
		var builder = new ProcessBuilder(location + "/bin/dart", "--version");

		builder.redirectErrorStream(true);

		try (var reader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()))) {
			return Optional.ofNullable(reader.readLine());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return Optional.empty();
	}
}
