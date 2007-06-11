package MWC.GUI.TabPanel;

import javax.swing.*;
import java.awt.*;

public class SwingTabPanel extends JTabbedPane implements MWC.GUI.CoreTabPanel
{

  public SwingTabPanel()
  {
    // shrink the font a little
    setFont(getFont().deriveFont(10f));
  }

  public void closeMe()
  {
    super.removeAll();
  }

	public int addTabPanel(String sLabel, boolean bEnabled, java.awt.Component panel)
	{
    panel.setName(sLabel);
		this.addTab(sLabel, panel);

		return 0;
	}

  public java.awt.Component add(String title, java.awt.Component comp)
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

  public java.awt.Component add(java.awt.Component comp)
  {
    java.awt.Component res = null;
    String title = comp.getName();
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


