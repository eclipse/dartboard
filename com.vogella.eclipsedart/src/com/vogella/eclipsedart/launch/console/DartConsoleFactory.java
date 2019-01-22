package com.vogella.eclipsedart.launch.console;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IOConsole;

import com.vogella.eclipsedart.Constants;

public class DartConsoleFactory implements IConsoleFactory {

	private InputStream inputStream;
	
	public DartConsoleFactory(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public void openConsole() {
		var consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		var console = new IOConsole(Constants.CONSOLE_NAME, null);
		var outputSteam = console.newOutputStream();

		//TODO: Check if this is efficient or if there's a better way
		int value;
		try {
			while((value = inputStream.read()) != -1) {
				outputSteam.write(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
	}
	
}
