package org.eclipse.dartboard.project.flutter;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.util.StatusUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

@SuppressWarnings({ "restriction", "nls" })
public class FlutterProjectPage extends WizardNewProjectCreationPage {

	private static final ILog LOG = Platform.getLog(FlutterProjectPage.class);

	private Button templateApp;
	private Button templatePackage;
	private Button templatePlugin;
	private Text description;
	private Text organization;
	private Button iosLanguageObjc;
	private Button iosLanguageSwift;
	private Button androidLanguageKotlin;
	private Button androidLanguageJava;

	public FlutterProjectPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		createAdditionalControls((Composite) getControl());
	}

	public void createAdditionalControls(Composite parent) {
		Group flutterGroup = new Group(parent, SWT.NONE);
		flutterGroup.setFont(parent.getFont());
		flutterGroup.setText("General");
		flutterGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		flutterGroup.setLayout(new GridLayout(2, false));

		LabelFactory.newLabel(SWT.NONE).text("Template").create(flutterGroup);

		Composite templates = new Composite(flutterGroup, SWT.NONE);
		templates.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		templates.setLayout(new RowLayout(SWT.HORIZONTAL));

		templateApp = ButtonFactory.newButton(SWT.RADIO).text("App").create(templates);
		templateApp.setSelection(true);
		templatePackage = ButtonFactory.newButton(SWT.RADIO).text("Package").create(templates);
		templatePlugin = ButtonFactory.newButton(SWT.RADIO).text("Plugin").create(templates);

		LabelFactory.newLabel(SWT.NONE).text("Description").create(flutterGroup);

		description = TextFactory.newText(SWT.BORDER).text("A new Flutter project")
				.layoutData(GridDataFactory.fillDefaults().grab(true, false).create()).create(flutterGroup);

		LabelFactory.newLabel(SWT.NONE).text("Organization").create(flutterGroup);

		organization = TextFactory.newText(SWT.BORDER).text("com.example")
				.layoutData(GridDataFactory.fillDefaults().grab(true, false).create()).create(flutterGroup);

		Group advancedGroup = new Group(parent, SWT.NONE);
		advancedGroup.setFont(parent.getFont());
		advancedGroup.setText("Advanced");
		advancedGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		advancedGroup.setLayout(new GridLayout(2, false));

		LabelFactory.newLabel(SWT.NONE).text("iOS language").create(advancedGroup);
		Composite iosLanguageGroup = new Composite(advancedGroup, SWT.NONE);
		iosLanguageGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		iosLanguageGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		iosLanguageObjc = ButtonFactory.newButton(SWT.RADIO).text("Objective C").create(iosLanguageGroup);
		iosLanguageObjc.setSelection(true);
		iosLanguageSwift = ButtonFactory.newButton(SWT.RADIO).text("Swift").create(iosLanguageGroup);

		LabelFactory.newLabel(SWT.NONE).text("Android language").create(advancedGroup);
		Composite androidLanguageGroup = new Composite(advancedGroup, SWT.NONE);
		androidLanguageGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		androidLanguageGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		androidLanguageJava = ButtonFactory.newButton(SWT.RADIO).text("Java").create(androidLanguageGroup);
		androidLanguageJava.setSelection(true);
		androidLanguageKotlin = ButtonFactory.newButton(SWT.RADIO).text("Kotlin").create(androidLanguageGroup);
	}

	public String getTemplate() {
		if (templateApp.getSelection()) {
			return "app";
		}
		if (templatePackage.getSelection()) {
			return "package";
		}
		if (templatePlugin.getSelection()) {
			return "plugin";
		}
		LOG.log(StatusUtil.createWarning("Could not determine project template, defaulting to 'app'"));
		return "app";
	}

	public String getDescription() {
		return description.getText();
	}

	public String getOrganization() {
		return organization.getText();
	}

	public String getIosLanguage() {
		if (iosLanguageObjc.getSelection()) {
			return "objc";
		}
		if (iosLanguageSwift.getSelection()) {
			return "swift";
		}
		LOG.log(StatusUtil.createWarning("Could not determine ios language, defaulting to 'objc'"));
		return "objc";
	}

	public String getAndroidLanguage() {
		if (androidLanguageJava.getSelection()) {
			return "java";
		}
		if (androidLanguageKotlin.getSelection()) {
			return "kotlin";
		}
		LOG.log(StatusUtil.createWarning("Could not determine android language, defaulting to 'java'"));
		return "java";
	}
}
