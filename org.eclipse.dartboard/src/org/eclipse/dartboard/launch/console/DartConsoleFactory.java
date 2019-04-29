package org.eclipse.dartboard.launch.console;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.dartboard.Constants;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IOConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DartConsoleFactory implements IConsoleFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DartConsoleFactory.class);
	
	private InputStream inputStream;

	public DartConsoleFactory(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void openConsole() {
		var consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		var console = new IOConsole(Constants.CONSOLE_NAME, null);
		var outputSteam = console.newOutputStream();

		try {
			inputStream.transferTo(outputSteam);
		} catch (IOException ioException) {
			LOG.error(ioException.getMessage());
		}

		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
	}

}
