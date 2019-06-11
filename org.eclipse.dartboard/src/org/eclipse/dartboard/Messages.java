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

	static {
		NLS.initializeMessages("assets.messages", Messages.class);
	}
}
