package org.mwc.cmap.NarrativeViewer.actions;

import org.eclipse.jface.action.Action;

public abstract class AbstractDynamicAction extends Action {
	public abstract void refresh(/* ActionContext context */);
}
