package org.eclipse.dartboard.dart.pub;

import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.dartboard.pub.IPubService;
import org.eclipse.dartboard.pub.PubListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class PubGetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LinkedList<IPubService> pubServices = PubListener.getPubServices();
		IStructuredSelection sel = HandlerUtil.getCurrentStructuredSelection(event);
		Object firstElement = sel.getFirstElement();
		if (firstElement instanceof IResource) {
			for (IPubService abstractPubService : pubServices) {
				// At this point, resource is the pubspec.yaml
				IProject project = ((IResource) firstElement).getProject();
				if (abstractPubService.test(project)) {
					abstractPubService.get(project);
					// We only want the first pubservice that is responsible
					break;
				}
			}
		}
		return null;
	}

}
