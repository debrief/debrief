package com.borlander.ianmayo.nviewer.actions;

import org.eclipse.jface.action.Action;

public abstract class AbstractDynamicAction extends Action {
	public abstract void refresh(/* ActionContext context */);
}
