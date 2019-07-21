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
	public static String Preference_SDKVersion;
	public static String Launch_SDKLocation_Message;
	public static String Launch_MainClass;
	public static String Launch_MainClass_Message;
	public static String Launch_PageTitle;
	public static String Preference_SDKNotFound_Title;
	public static String Preference_SDKNotFound_Body;
	public static String Preference_SDKNotFound_Message;
	public static String Preference_RestartRequired_Title;
	public static String Preference_RestartRequired_Message;
	public static String Launch_ConfigurationRequired_Title;
	public static String Launch_ConfigurationRequired_Body;

	public static String NewProject_WindowTitle;
	public static String NewProject_Title;
	public static String NewProject_Description;
	public static String NewProject_ErrorMessage;
	public static String NewProject_InternalError;
	public static String NewProject_CaseVariantExistsError;

	public static String NewProject_Group_Label;
	public static String NewProject_SDK_Not_Found;

	public static String NewFile_WindowTitle;
	public static String NewFile_Title;
	public static String NewFile_Description;
	public static String NewFile_Creating;
	public static String NewFile_Container_Doesnot_Exist;
	public static String NewFile_OpeningFile;

	static {
		NLS.initializeMessages("assets.messages", Messages.class);
	}
}
