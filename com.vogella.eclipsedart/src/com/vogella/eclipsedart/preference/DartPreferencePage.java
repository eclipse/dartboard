package com.vogella.eclipsedart.preference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.vogella.eclipsedart.CommandLineTools;
import com.vogella.eclipsedart.Constants;

public class DartPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text sdkLocationTextField;
	
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
		
		sdkLocationTextField  = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		sdkLocationTextField.setLayoutData(gridData);
		sdkLocationTextField.setText(preferences.get(Constants.PREFERENCES_SDK_LOCATION, ""));
		return composite;
	}
	
	@Override
	public boolean performOk() {

		//TODO: Add a check if the entered location is a valid dart SDK
		
//		String sdkLocation = sdkLocationTextField.getText();
//		String command = "/usr/lib/dart-sdk/bin/dart";
//
//		Process process;
//		try {
//			process = new ProcessBuilder("/bin/sh", "-c", "'dart --version'").start();
//			var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			
//			String line;
//			while((line = reader.readLine()) != null) {
//				System.out.println(line);
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		
//		CommandLineTools.execute("/bin/env")
//			.ifPresentOrElse(System.out::println, () -> System.out.println("#### empty"));

		preferences.put(Constants.PREFERENCES_SDK_LOCATION, sdkLocationTextField.getText());
		
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			//TODO: Add logging
			e.printStackTrace();
		}
		return super.performOk();
	}
	
}
