/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.TabPanel;

import javax.swing.JTabbedPane;

public class SwingTabPanel extends JTabbedPane implements MWC.GUI.CoreTabPanel
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SwingTabPanel()
  {
    // shrink the font a little
    setFont(getFont().deriveFont(10f));
  }

  public void closeMe()
  {
    super.removeAll();
  }

	public int addTabPanel(final String sLabel, final boolean bEnabled, final java.awt.Component panel)
	{
    panel.setName(sLabel);
		this.addTab(sLabel, panel);

		return 0;
	}

  public java.awt.Component add(final String title, final java.awt.Component comp)
  {
    java.awt.Component res = null;
    if(title.equals("North"))
    {
      res =  super.add(comp.getName(), comp);
    }
    else
    {
      res =  super.add(title, comp);
    }
    return res;

  }

  public java.awt.Component add(final java.awt.Component comp)
  {
    java.awt.Component res = null;
    final String title = comp.getName();
    if(title.equals("North"))
    {
      res =  super.add(comp.getName(), comp);
    }
    else
    {
      res =  super.add(title, comp);
    }

    super.setSelectedComponent(res);

    return res;

  }



}


