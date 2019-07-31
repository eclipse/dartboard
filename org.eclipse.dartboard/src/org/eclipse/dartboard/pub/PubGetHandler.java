package org.eclipse.dartboard.pub;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dartboard.Constants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PubGetHandler extends AbstractHandler {

	private PubService pub;

	private ScopedPreferenceStore preferences;

	public PubGetHandler() {
		pub = PubService.getInstance();
		preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection sel = HandlerUtil.getCurrentStructuredSelection(event);
		Object firstElement = sel.getFirstElement();
		if (firstElement instanceof IResource) {
			pub.get(((IResource) firstElement).getProject(), preferences.getBoolean(Constants.PREFERENCES_OFFLINE_PUB));
		}
		return null;
	}

}
