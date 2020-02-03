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
package org.eclipse.dartboard.messages;

import org.eclipse.osgi.util.NLS;

@SuppressWarnings("nls")
public class Messages extends NLS {

	public static String Console_Name;
	public static String Launch_DebugNotSupported_Title;
	public static String Launch_DebugNotSupported_Body;
	public static String Launch_NoProjectSelected_Title;
	public static String Launch_NoProjectSelected_Body;
	public static String Launch_Project;
	public static String Preference_SDKLocation_Dart;
	public static String Preference_SDKVersion_Dart;
	public static String Preference_SDKLocation_Flutter;
	public static String Preference_SDKVersion_Flutter;
	public static String Preference_PubAutoSync_Label;
	public static String Preference_PubOffline_Label;
	public static String Launch_SDKLocation_Message;
	public static String Launch_MainClass;
	public static String Launch_MainClass_Message;
	public static String Launch_PageTitle;
	public static String Preference_SDKNotFound_Title;
	public static String Preference_SDKNotFound_Body;
	public static String Preference_SDKNotFound_Message;
	public static String Preference_RestartRequired_Title;
	public static String Preference_RestartRequired_Message;
	public static String Preference_PluginMode_Label;
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
	public static String NewProject_Stagehand_Title;
	public static String NewProject_Stagehand_UseStagehandButtonText;
	public static String NewProject_Stagehand_FetchStagehand;

	public static String NewFile_WindowTitle;
	public static String NewFile_Title;
	public static String NewFile_Description;
	public static String NewFile_Creating;
	public static String NewFile_Container_Doesnot_Exist;
	public static String NewFile_OpeningFile;
	public static String PubSync_Job_Name;
	public static String PubSync_Task_ResolvingDependencies;
	public static String PubSync_Task_PrecompilingExecutables;
	public static String PubSync_CouldNotDeterminePath;
	public static String PubSync_CouldNotStartProcess;
	public static String Error_CouldNotRefreshResource;

	public static String Stagehand_GeneratorJob_Name;
	public static String Stagehand_GeneratorJobFail_Title;
	public static String Stagehand_GeneratorJobFail_Body;

	static {
		NLS.initializeMessages("assets.messages", Messages.class);
	}
}
