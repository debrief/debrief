package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

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

    stateListeners = new ArrayList<>(Arrays.asList(
        enableDisableButtonsListener));

    setState(INACTIVE_STATE);
  }

  private void init()
  {
    final JSelectTrackFilter selectTrack = new JSelectTrackFilter(_model);

    final JComboBox<String> tracksFilterLabel = new JComboBox<>(new String[]
    {"Sources"});
    tracksFilterLabel.setEnabled(true);
    tracksFilterLabel.addMouseListener(new MouseListener()
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
        if (tracksFilterLabel.isEnabled())
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

    JSelectTypeFilter typeFilter = new JSelectTypeFilter(_model);
    final JComboBox<String> typeFilterLabel = new JComboBox<>(new String[]
    {"Types"});
    typeFilterLabel.setEnabled(true);
    typeFilterLabel.addMouseListener(new MouseListener()
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
        if (typeFilterLabel.isEnabled())
        {
          // Get the event source
          final Component component = (Component) e.getSource();

          typeFilter.show(component, 0, 0);

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
    
    final JButton wrapTextButton = createCommandButton(
        "Wrap Text",
        "icons/16/wrap.png");
    wrapTextButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        System.out.println("Wrap Text not implemented");
      }
    });
    
    final JButton copyButton = createCommandButton(
        "Copy Selected Entrey",
        "icons/16/copy_to_clipboard.png");
    copyButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        System.out.println("Copy selected entry not implemented");
      }
    });
    
    final JButton addBulkEntriesButton = createCommandButton(
        "Add Bulk Entries",
        "icons/16/list.png");
    addBulkEntriesButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        System.out.println("Add Bulk Entries not implemented");
      }
    });
    
    final JButton addSingleEntryButton = createCommandButton(
        "Add Single Entry",
        "icons/16/add.png");
    addBulkEntriesButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        System.out.println("Add single entry not implemented");
      }
    });

    add(tracksFilterLabel);
    add(typeFilterLabel);
    add(wrapTextButton);
    add(copyButton);
    add(addBulkEntriesButton);
    add(addSingleEntryButton);

    componentsToDisable.addAll(Arrays.asList(new JComponent[]
    {tracksFilterLabel, typeFilterLabel, wrapTextButton, copyButton,
        addBulkEntriesButton, addSingleEntryButton}));
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

  private JButton createCommandButton(final String command, final String image)
  {
    final ImageIcon icon = getIcon(image);
    final JButton button = new JButton(icon);
    button.setToolTipText(command);
    return button;
  }
  
  private ImageIcon getIcon(final String image)
  {
    final URL imageIcon = getClass().getClassLoader().getResource(image);
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(imageIcon);
    }
    catch (final Exception e)
    {
      throw new IllegalArgumentException("Icon missing:" + image);
    }
    return icon;
  }
}
