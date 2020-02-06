
package org.mwc.cmap.naturalearth.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.wrapper.NELayer;

/**
 * @author ian.mayo
 */
public class CreateNELayer extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// check if we have a data path, and check it exists
		if (!NELayer.hasGoodPath())
		{
			Activator.logError(IStatus.WARNING, "Don't have good path assigned", null);
		}
		else
		{
				return new NELayer(Activator.getDefault().getDefaultStyleSet());
		}

		return null;
	}

}
