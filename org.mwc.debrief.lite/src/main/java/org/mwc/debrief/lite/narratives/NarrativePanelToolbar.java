package org.mwc.debrief.lite.narratives;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.mwc.debrief.lite.gui.custom.AbstractNarrativeConfiguration;
import org.mwc.debrief.lite.gui.custom.JSelectTrackFilter;

public class NarrativePanelToolbar extends JPanel
{

  /**
   * Generated Serial Version ID.
   */
  private static final long serialVersionUID = 349058868680231476L;

  public static final String ACTIVE_STATE = "ACTIVE";

  public static final String INACTIVE_STATE = "INACTIVE";

  public static final String STATE_PROPERTY = "STATE";

  public static final String NARRATIVES_PROPERTY = "NARRATIVES";

  private String _state = INACTIVE_STATE;
  
  private final List<JComponent> componentsToDisable = new ArrayList<>();
  
  private final AbstractNarrativeConfiguration _model;
  
  private final PropertyChangeListener enableDisableButtonsListener =
      new PropertyChangeListener()
      {

        @Override
        public void propertyChange(final PropertyChangeEvent event)
        {
          if (STATE_PROPERTY.equals(event.getPropertyName()))
          {
            final boolean isActive = ACTIVE_STATE.equals(event.getNewValue());
            for (final JComponent component : componentsToDisable)
            {
              component.setEnabled(isActive);
            }
          }
        }
      };
  
  public NarrativePanelToolbar(final AbstractNarrativeConfiguration model)
  {
    super(new FlowLayout(FlowLayout.LEFT));
    
    this._model = model;
    init();

    stateListeners = new ArrayList<>(Arrays.asList(enableDisableButtonsListener));

    setState(INACTIVE_STATE);
  }

  private void init()
  {
    final JSelectTrackFilter selectTrack = new JSelectTrackFilter(_model);
    
    final JComboBox<String> selectTracksLabel = new JComboBox<>(new String[]
      {"Select Tracks"});
      selectTracksLabel.setEnabled(true);
      selectTracksLabel.addMouseListener(new MouseListener()
      {

        @Override
        public void mouseClicked(final MouseEvent e)
        {

        }

        @Override
        public void mouseEntered(final MouseEvent e)
        {

        }

        @Override
        public void mouseExited(final MouseEvent e)
        {

        }

        @Override
        public void mousePressed(final MouseEvent e)
        {
          if (selectTracksLabel.isEnabled())
          {
            // Get the event source
            final Component component = (Component) e.getSource();

            selectTrack.show(component, 0, 0);

            // Get the location of the point 'on the screen'
            final Point p = component.getLocationOnScreen();

            selectTrack.setLocation(p.x, p.y + component.getHeight());
          }
        }

        @Override
        public void mouseReleased(final MouseEvent e)
        {

        }
      });
      
  }

  private final ArrayList<PropertyChangeListener> stateListeners;

  public void setState(final String newState)
  {
    final String oldState = _state;
    this._state = newState;

    notifyListenersStateChanged(this, STATE_PROPERTY, oldState, newState);
  }


  private void notifyListenersStateChanged(final Object source,
      final String property, final Object oldValue, final Object newValue)
  {
    for (final PropertyChangeListener event : stateListeners)
    {
      event.propertyChange(new PropertyChangeEvent(source, property, oldValue,
          newValue));
    }
  }
}
