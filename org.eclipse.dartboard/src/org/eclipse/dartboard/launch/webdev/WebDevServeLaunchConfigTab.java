package org.eclipse.dartboard.launch.webdev;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dartboard.launch.BaseLaunchConfigTab;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TableFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class WebDevServeLaunchConfigTab extends BaseLaunchConfigTab {

	private static final Logger LOG = LoggerFactory.getLogger(WebDevServeLaunchConfigTab.class);

//	private Text outputText;
	private Button verboseCheckbox;
	private Button autoPerformCheckbox;
	private Combo autoPerformCombo;
	private Text hostText;
	private Text portText;
	private Button injectClientCheckbox;

	public WebDevServeLaunchConfigTab() {
		super("/icons/dart.png");
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
//		configuration.setAttribute(WebDevServeLaunchConfig.OUTPUT_DIRECTORY,
//				WebDevServeLaunchConfig.DEFAULT_OUTPUT_DIRECTORY);
		configuration.setAttribute(WebDevServeLaunchConfig.VERBOSE_OUTPUT,
				WebDevServeLaunchConfig.DEFAULT_VERBOSE_OUTPUT);
		configuration.setAttribute(WebDevServeLaunchConfig.HOSTNAME, WebDevServeLaunchConfig.DEFAULT_HOSTNAME);
		configuration.setAttribute(WebDevServeLaunchConfig.PORT, WebDevServeLaunchConfig.DEFAULT_PORT);
		configuration.setAttribute(WebDevServeLaunchConfig.AUTO_PERFORM, WebDevServeLaunchConfig.DEFAULT_AUTO_PERFORM);
		configuration.setAttribute(WebDevServeLaunchConfig.INJECT_CLIENT,
				WebDevServeLaunchConfig.DEFAULT_INJECT_CLIENT);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
//			outputText.setText(configuration.getAttribute(WebDevServeLaunchConfig.OUTPUT_DIRECTORY, ""));
			verboseCheckbox.setSelection(configuration.getAttribute(WebDevServeLaunchConfig.VERBOSE_OUTPUT, false));
			hostText.setText(configuration.getAttribute(WebDevServeLaunchConfig.HOSTNAME, ""));
//			portText.setText(configuration.getAttribute(WebDevServeLaunchConfig.PORT, ""));
			injectClientCheckbox.setSelection(configuration.getAttribute(WebDevServeLaunchConfig.INJECT_CLIENT, true));

			String autoPerformValue = configuration.getAttribute(WebDevServeLaunchConfig.AUTO_PERFORM, "");
			if (!autoPerformValue.equalsIgnoreCase("")) {
				autoPerformCheckbox.setSelection(true);
				autoPerformCombo.setText(autoPerformValue);
			} else {
				autoPerformCheckbox.setSelection(false);
				autoPerformCombo.setEnabled(false);
			}
		} catch (CoreException e) {
			LOG.error("Could not initialize from launch configuration", e);
		}
	}

	@Override
	public String getName() {
		return "Dart web configuration";
	}

	@Override
	public void createExtraControls(Composite parent) {
		Group webdevGroup = new Group(parent, SWT.NONE);
		webdevGroup.setText("Web development");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(webdevGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(webdevGroup);

//		LabelFactory.newLabel(SWT.NONE).text("Output directory (--output)").layoutData(new GridData())
//				.create(webdevGroup);
//
//		outputText = TextFactory.newText(SWT.BORDER).message("A directory to write the result of a build to")
//				.layoutData(GridDataFactory.fillDefaults().grab(true, false).create())
//				.onModify(event -> updateLaunchConfigurationDialog()).create(webdevGroup);

		autoPerformCheckbox = ButtonFactory.newButton(SWT.CHECK).text("Automatically perform").onSelect(event -> {
			autoPerformCombo.setEnabled(autoPerformCheckbox.getSelection());
		}).create(webdevGroup);

		autoPerformCombo = new Combo(webdevGroup, SWT.BORDER | SWT.READ_ONLY);
		autoPerformCombo.setItems(new String[] { "Restart", "Refresh" });
		autoPerformCombo.setEnabled(false);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(autoPerformCombo);

		LabelFactory.newLabel(SWT.NONE).text("Server host (--hostname)").layoutData(new GridData()).create(webdevGroup);

		hostText = TextFactory.newText(SWT.BORDER).message("Hostname to serve on")
				.layoutData(GridDataFactory.fillDefaults().grab(true, false).create()).create(webdevGroup);

//		LabelFactory.newLabel(SWT.NONE).text("Server port (:<port>)").layoutData(new GridData()).create(webdevGroup);
//		portText = TextFactory.newText(SWT.BORDER).message("Port to serve on")
//				.layoutData(GridDataFactory.fillDefaults().grab(true, false).create()).create(webdevGroup);

		injectClientCheckbox = ButtonFactory.newButton(SWT.CHECK).text("Inject client.js (--[no-]injected-client)")
				.layoutData(GridDataFactory.fillDefaults().span(2, 1).create()).create(webdevGroup);

		verboseCheckbox = ButtonFactory.newButton(SWT.CHECK).text("Enable verbose logging (--verbose)")
				.layoutData(GridDataFactory.fillDefaults().span(2, 1).create()).create(webdevGroup);

//		TableViewer tableViewer = new TableViewer(webdevGroup,
//				SWT.MULTI | SWT.BORDER);
//		tableViewer.getTable().setHeaderVisible(true);
//		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
//		tableViewer.setInput(Lists.newArrayList("asd", "asdasd123", "asdasd"));
//		TableViewerColumn directoryColumn = new TableViewerColumn(tableViewer, SWT.NONE);
//		directoryColumn.getColumn().setWidth(300);
//		directoryColumn.getColumn().setText("Directory");
//		TableViewerColumn portColumn = new TableViewerColumn(tableViewer, SWT.NONE);
//		directoryColumn.getColumn().setWidth(300);
//		directoryColumn.getColumn().setText("Port");
//		
		Table directoryPortTable = TableFactory.newTable(SWT.BORDER).headerVisible(true).itemCount(5)
				.layoutData(GridDataFactory.fillDefaults().span(2, 1).create()).create(webdevGroup);
		directoryPortTable.setData(Lists.newArrayList("asdasd", "asd", "asd"));

	}

	@Override
	public void saveExtraAttributes(ILaunchConfigurationWorkingCopy configuration) {
//		configuration.setAttribute(WebDevServeLaunchConfig.OUTPUT_DIRECTORY, outputText.getText());
		configuration.setAttribute(WebDevServeLaunchConfig.VERBOSE_OUTPUT, verboseCheckbox.getSelection());
		if (autoPerformCheckbox.getSelection()) {
			configuration.setAttribute(WebDevServeLaunchConfig.AUTO_PERFORM, autoPerformCombo.getText());
		} else {
			configuration.removeAttribute(WebDevServeLaunchConfig.AUTO_PERFORM);
		}
		configuration.setAttribute(WebDevServeLaunchConfig.HOSTNAME, hostText.getText());
//		configuration.setAttribute(WebDevServeLaunchConfig.PORT, portText.getText());
		configuration.setAttribute(WebDevServeLaunchConfig.INJECT_CLIENT, injectClientCheckbox.getSelection());
	}

}
