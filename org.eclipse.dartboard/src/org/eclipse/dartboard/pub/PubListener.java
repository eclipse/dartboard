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
package org.eclipse.dartboard.pub;

import java.util.LinkedList;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dartboard.util.Constants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class PubListener implements EventHandler {

	private static final String EXTENSION_CLASS = "class"; //$NON-NLS-1$
	private static final String EXTENSION_PRIORITY = "priority"; //$NON-NLS-1$

	@Override
	public void handleEvent(Event event) {
		LinkedList<IPubService> pubServices = getPubServices();
		PubspecChangeListener pubspecChangeListener = new PubspecChangeListener(pubServices);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(pubspecChangeListener);
	}

	/**
	 * Retrieves all registered PubService extensions
	 * 
	 * All services that have the priority attribute set to true, are at the
	 * beginning of the resulting list.
	 * 
	 * @return a List of all PubService extensions
	 */
	public static LinkedList<IPubService> getPubServices() {
		LinkedList<IPubService> pubServices = new LinkedList<IPubService>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		for (IConfigurationElement e : extensionRegistry.getExtensionPoint(Constants.PUB_SERVICE_EXTENSION_POINT)
				.getConfigurationElements()) {
			try {
				IPubService pubService = (IPubService) e.createExecutableExtension(EXTENSION_CLASS);
				boolean priority = Boolean.valueOf(e.getAttribute(EXTENSION_PRIORITY));
				if (priority) {
					pubServices.add(0, pubService);
				} else {
					pubServices.add(pubService);
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
		return pubServices;
	}
}
