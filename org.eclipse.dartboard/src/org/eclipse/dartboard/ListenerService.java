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
 *     Jonas Hungershausen - initial API and implementation
 *******************************************************************************/
package org.eclipse.dartboard;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dartboard.pub.PubService;
import org.eclipse.dartboard.pub.PubspecChangeListener;
import org.eclipse.dartboard.webdev.WebDevService;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class ListenerService implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		PubService pubService = PlatformUI.getWorkbench().getService(PubService.class);
		PubspecChangeListener pubspecChangeListener = new PubspecChangeListener(pubService);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(pubspecChangeListener);

		// Startup for WebDev
		WebDevService.init();
	}
}
