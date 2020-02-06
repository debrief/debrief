
package org.mwc.debrief.help.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

/**
 *
 */
public class AcknowledgementHandler extends AbstractHandler
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException
	{
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.displayHelpResource(
						"/org.mwc.debrief.help/html/legacy/index.html#acknowledgements");
		return null;
	}

}
