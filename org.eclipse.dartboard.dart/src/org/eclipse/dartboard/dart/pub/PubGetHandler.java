package org.eclipse.dartboard.dart.pub;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.dartboard.dart.Constants;
import org.eclipse.dartboard.preferences.DartPreferences;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PubGetHandler extends AbstractHandler {

	private ScopedPreferenceStore preferences = DartPreferences.getPreferenceStore(Constants.PLUGIN_ID);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection sel = HandlerUtil.getCurrentStructuredSelection(event);
		IWorkbench workbench = HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench();
		PubService pub = workbench.getService(PubService.class);
		Object firstElement = sel.getFirstElement();
		if (firstElement instanceof IResource) {
			pub.get(((IResource) firstElement).getProject(), preferences.getBoolean(Constants.PREFERENCES_OFFLINE_PUB));
		}
		return null;
	}

}
