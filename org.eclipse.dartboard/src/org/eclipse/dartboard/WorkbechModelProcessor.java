package org.eclipse.dartboard;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dartboard.pub.PubspecChangeListener;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class WorkbechModelProcessor {
	@Execute
	void process(IEventBroker broker) {
		broker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				ResourcesPlugin.getWorkspace().addResourceChangeListener(new PubspecChangeListener());
			}
		});
	}
}
