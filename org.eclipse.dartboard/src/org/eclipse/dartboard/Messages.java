package org.eclipse.dartboard;

import org.eclipse.osgi.util.NLS;

@SuppressWarnings("nls")
public class Messages extends NLS {

	public static String Console_Name;
	public static String Launch_DebugNotSupported_Title;
	public static String Launch_DebugNotSupported_Body;
	public static String Launch_NoProjectSelected_Title;
	public static String Launch_NoProjectSelected_Body;
	public static String Launch_Project;
	public static String Preference_SDKLocation;
	public static String Launch_SDKLocation_Message;
	public static String Launch_MainClass;
	public static String Launch_MainClass_Message;
	public static String Launch_PageTitle;
	public static String Preference_SDKNotFound_Title;
	public static String Preference_SDKNotFound_Body;
	public static String Preference_SDKNotFound_Message;
	public static String ProjectNature_DeletePubspec_Title;
	public static String ProjectNature_DeletePubspec_Message;
	public static String Launch_NoConfigurationFound_Title;
	public static String Launch_NoConfigurationFound_Body;

	public static String NewProject_windowTitle;
	public static String NewProject_title;
	public static String NewProject_description;
	public static String NewProject_errorMessage;
	public static String NewProject_internalError;
	public static String NewProject_caseVariantExistsError;

	public static String NewProject_group_label;
	public static String NewProject_sdk_version_label;

	public static String NewFile_windowTitle;
	public static String NewFile_title;
	public static String NewFile_description;
	public static String NewFile_creating;
	public static String NewFile_container_doesnot_exist;
	public static String NewFile_opening_file;

	static {
		NLS.initializeMessages("assets.messages", Messages.class);
	}
}
