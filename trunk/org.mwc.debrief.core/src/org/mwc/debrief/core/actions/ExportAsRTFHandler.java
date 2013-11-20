package org.mwc.debrief.core.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.mwc.cmap.plotViewer.actions.ExportRTF;

public class ExportAsRTFHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		final ExportRTF exportRTF = new ExportRTF(true, false);
		exportRTF.run(null);
		return null;
	}

}
