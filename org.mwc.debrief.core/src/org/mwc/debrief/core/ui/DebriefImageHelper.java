
package org.mwc.debrief.core.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.debrief.core.DebriefPlugin;

import MWC.GUI.Editable;

public class DebriefImageHelper implements ViewLabelImageHelper
{

  public ImageDescriptor getImageFor(final Editable editable)
  {
    ImageDescriptor res = null;
    Debrief.GUI.DebriefImageHelper helper = new Debrief.GUI.DebriefImageHelper();
    String icon = helper.getImageFor(editable);
    if(icon!=null) {
      res = DebriefPlugin.getImageDescriptor(icon);
    }
    return res;
  }

}
