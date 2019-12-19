package org.mwc.debrief.lite.map;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.geotools.swing.AbstractMapPane;
import org.geotools.swing.MapPane;
import org.geotools.swing.action.MapAction;
import org.geotools.swing.tool.ZoomInTool;
import org.opengis.referencing.operation.MathTransform;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import MWC.GenericData.WorldDistance;

/**
 * An action for connect a control (probably a JButton) to the Range Bearing tool - for measuring
 * range and bearing
 *
 * @author Ian Mayo, from Michael Bedward's Zoom In Action
 * @since 2.6
 */
public class RangeBearingAction extends MapAction implements CommandAction
{

  /**
  *
  */
  private static final long serialVersionUID = 1L;
  private final JLabel _statusBar;
  private final MathTransform _transform;

  /**
   * Constructor. The associated control will be labelled with an icon and, optionally, the tool
   * name.
   *
   * @param mapPane
   *          the map pane being serviced by this action
   * @param showToolName
   *          set to true for the control to display the tool name
   * @param transform
   */
  public RangeBearingAction(final MapPane mapPane, final boolean showToolName,
      final JLabel statusBar, final MathTransform transform)
  {
    final String toolName = showToolName ? ZoomInTool.TOOL_NAME : null;
    super.init(mapPane, toolName, ZoomInTool.TOOL_TIP, ZoomInTool.ICON_IMAGE);
    _statusBar = statusBar;
    _transform = transform;
  }

  /**
   * Called when the associated control is activated. Leads to the map pane's cursor tool being set
   * to a new ZoomInTool object
   *
   * @param ev
   *          the event (not used)
   */
  @Override
  public void actionPerformed(final ActionEvent ev)
  {
    // Not an amazing cast, but it is ok since AbstractMapPane is the only implementation of MapPane
    final RangeBearingTool rangeBearingTool = new RangeBearingTool(_statusBar,
        _transform, (AbstractMapPane) getMapPane());
    getMapPane().setCursorTool(rangeBearingTool);

    final JPopupMenu menu = new JPopupMenu();
    // ButtonGroup for radio buttons
    final ButtonGroup unitsGroup = new ButtonGroup();

    final ActionListener changeUnits = new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final String unit = e.getActionCommand();
        rangeBearingTool.setBearingUnit(WorldDistance.getUnitIndexFor(unit));
        ((AbstractMapPane) getMapPane()).repaint();
      }
    };

    for (int i = 0; i < WorldDistance.UnitLabels.length; i++)
    {
      final JRadioButtonMenuItem unitRadioButton = new JRadioButtonMenuItem(
          WorldDistance.UnitLabels[i]);
      unitRadioButton.setSelected(RangeBearingTool.getBearingUnit() == i);
      unitRadioButton.addActionListener(changeUnits);
      menu.add(unitRadioButton);
      unitsGroup.add(unitRadioButton);
    }

    final Component component = (Component) ev.getSource();

    menu.show(component, 0, 0);

    // Get the location of the point 'on the screen'
    final Point p = component.getLocationOnScreen();

    menu.setLocation(p.x, p.y + component.getHeight());
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
    actionPerformed(e);
  }

}
