
package org.mwc.cmap.naturalearth;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.cmap.naturalearth.wrapper.NELayer;

import MWC.GUI.Editable;

public class NE_ImageHelper implements ViewLabelImageHelper
{

	public ImageDescriptor getImageFor(final Editable editable)
	{
		ImageDescriptor res = null;

		if (editable instanceof NELayer)
			res = Activator.getImageDescriptor("icons/16/NaturalEarth.png");
		
		return res;
	}

}
