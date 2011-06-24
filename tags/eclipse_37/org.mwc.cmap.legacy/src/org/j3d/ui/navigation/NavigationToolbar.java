/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package org.j3d.ui.navigation;

// Standard imports
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.j3d.util.ImageLoader;

/**
 * A toolbar for all navigation commands.
 * <p>
 *
 * The toolbar offers three buttons representing the navigation states. As this
 * panel also implements the state listener, these will change in response to
 * the mouse changing. Clicking these buttons will send the appropriate event
 * back to it's registered listener. By default, the navigation toolbar does
 * not have any state pre-set.
 * <p>
 *
 * Using the default images, the toolbar looks like this:
 * <p>
 * <img src="doc-files/nav_toolbar.png" width="410" height="55">
 * </p>
 *
 * @author <a href="http://www.ife.no/vr/">Halden VR Centre, Institute for Energy Technology</a><br>
 *    Updated for j3d.org by Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class NavigationToolbar extends JPanel
    implements ActionListener, NavigationStateListener
{
    // Constants for images

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** The name of the file for the pan cursor image */
    private static final String PAN_BUTTON = "images/navigation/ButtonPan.gif";

    /** The name of the file for the tilt cursor image */
    private static final String TILT_BUTTON = "images/navigation/ButtonTilt.gif";

    /** The name of the file for the fly cursor image */
    private static final String FLY_BUTTON = "images/navigation/ButtonFly.gif";

    /** The name of the file for the fly cursor image */
    private static final String WALK_BUTTON = "images/navigation/ButtonWalk.gif";

    /** The name of the file for the fly cursor image */
    private static final String EXAMINE_BUTTON = "images/navigation/ButtonExamine.gif";

    // Local variables

    /** The current navigation state either set from us or externally */
    private int navigationState = FLY_STATE;

    /** An observer for navigation state change information */
    private NavigationStateListener navigationListener;

    /** Button group holding the navigation state buttons */
    private ButtonGroup navStateGroup;

    /** Button representing the fly navigation state */
    private JToggleButton flyButton;

    /** Button representing the tilt navigation state */
    private JToggleButton tiltButton;

    /** Button representing the pan navigation state */
    private JToggleButton panButton;

    /** Button representing the walk navigation state */
    private JToggleButton walkButton;

    /** Button representing the examine navigation state */
    private JToggleButton examineButton;

    /** The last selected button */
    private JToggleButton lastButton;

    /** Flag to indicate if user state selection is allowed */
    private boolean allowUserSelect;

    /**
     * Create a new horizontal navigation toolbar with an empty list of
     * viewpoints and disabled user selection of state.
     */
    public NavigationToolbar()
    {
        this(true);
    }

    /**
     * Create a new navigation toolbar with an empty list of viewpoints but
     * controllable direction for the buttons. The user selection is disabled.
     *
     * @param horizontal True to lay out the buttons horizontally
     */
    public NavigationToolbar(boolean horizontal)
    {
        if(horizontal)
            setLayout(new GridLayout(1, 3));
        else
            setLayout(new GridLayout(3, 1));

        navStateGroup = new ButtonGroup();

        Icon icon = ImageLoader.loadIcon(FLY_BUTTON);
        flyButton = new JToggleButton(icon, false);
        flyButton.setMargin(new Insets(0,0,0,0));
        flyButton.setToolTipText("Fly");
        flyButton.addActionListener(this);
        navStateGroup.add(flyButton);
        add(flyButton);

        icon = ImageLoader.loadIcon(TILT_BUTTON);
        tiltButton = new JToggleButton(icon, false);
        tiltButton.setMargin(new Insets(0,0,0,0));
        tiltButton.setToolTipText("Tilt");
        tiltButton.addActionListener(this);
        navStateGroup.add(tiltButton);
        add(tiltButton);

        icon = ImageLoader.loadIcon(PAN_BUTTON);
        panButton = new JToggleButton(icon, false);
        panButton.setMargin(new Insets(0,0,0,0));
        panButton.setToolTipText("Pan");
        panButton.addActionListener(this);
        navStateGroup.add(panButton);
        add(panButton);

        icon = ImageLoader.loadIcon(WALK_BUTTON);
        walkButton = new JToggleButton(icon, false);
        walkButton.setMargin(new Insets(0,0,0,0));
        walkButton.setToolTipText("Walk");
        walkButton.addActionListener(this);
        navStateGroup.add(walkButton);
        add(walkButton);

        icon = ImageLoader.loadIcon(EXAMINE_BUTTON);
        examineButton = new JToggleButton(icon, false);
        examineButton.setMargin(new Insets(0,0,0,0));
        examineButton.setToolTipText("Examine");
        examineButton.addActionListener(this);
        navStateGroup.add(examineButton);
        add(examineButton);

        allowUserSelect = false;
        setEnabled(false);
    }

    //----------------------------------------------------------
    // Local public methods
    //----------------------------------------------------------

    /**
     * Set the listener for navigation state change notifications. By setting
     * a value of null it will clear the currently set instance
     *
     * @param l The listener to use for change updates
     */
    public void setNavigationStateListener(NavigationStateListener l)
    {
        navigationListener = l;
    }

    /**
     * Toggle whether the UI will allow the user to change the state selected
     * for navigation.
     *
     * @param allow True if the user can change the navigation state
     */
    public void setAllowUserStateChange(boolean allow)
    {
        allowUserSelect = allow;
        setEnabled(allowUserSelect);
    }

    //----------------------------------------------------------
    // Methods required by the ActionListener
    //----------------------------------------------------------

    /**
     * Process an action event on one of the buttons.
     *
     * @param evt The event that caused this method to be called
     */
    public void actionPerformed(ActionEvent evt)
    {
        if(!allowUserSelect)
            return;

        Object src = evt.getSource();

        if(src == flyButton)
        {
            navigationState = FLY_STATE;
            if(navigationListener != null)
                navigationListener.setNavigationState(navigationState);
        }
        else if(src == tiltButton)
        {
            navigationState = TILT_STATE;
            if(navigationListener != null)
                navigationListener.setNavigationState(navigationState);
        }
        else if(src == panButton)
        {
            navigationState = PAN_STATE;
            if(navigationListener != null)
                navigationListener.setNavigationState(navigationState);
        }
        else if(src == walkButton)
        {
            navigationState = WALK_STATE;
            if(navigationListener != null)
                navigationListener.setNavigationState(navigationState);
        }
        else if(src == examineButton)
        {
            navigationState = EXAMINE_STATE;
            if(navigationListener != null)
                navigationListener.setNavigationState(navigationState);
        }
    }

    //----------------------------------------------------------
    // Methods required by the NavigationStateListener
    //----------------------------------------------------------

    /**
     * Notification that the panning state has changed to the new state.
     *
     * @param state One of the state values declared here
     */
    public void setNavigationState(int state)
    {
        navigationState = state;

        switch(navigationState)
        {
            case NavigationStateListener.FLY_STATE:
                flyButton.setSelected(true);
                lastButton = flyButton;
                break;

            case NavigationStateListener.PAN_STATE:
                panButton.setSelected(true);
                lastButton = panButton;
                break;

            case NavigationStateListener.TILT_STATE:
                tiltButton.setSelected(true);
                lastButton = tiltButton;
                break;

            case NavigationStateListener.WALK_STATE:
                walkButton.setSelected(true);
                lastButton = walkButton;
                break;

            case NavigationStateListener.EXAMINE_STATE:
                examineButton.setSelected(true);
                lastButton = examineButton;
                break;

            case NavigationStateListener.NO_STATE:
                if(lastButton != null)
                    lastButton.setSelected(false);
                break;
        }
    }

    /**
     * Callback to ask the listener what navigation state it thinks it is
     * in.
     *
     * @return The state that the listener thinks it is in
     */
    public int getNavigationState()
    {
        return navigationState;
    }

    //----------------------------------------------------------
    // Methods Overriding Component
    //----------------------------------------------------------

    /**
     * Set the panel enabled or disabled. Overridden to make sure the base
     * components are properly handled.
     *
     * @param enabled true if this component is enabled
     */
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        examineButton.setEnabled(enabled);
        flyButton.setEnabled(enabled);
        walkButton.setEnabled(enabled);
        panButton.setEnabled(enabled);
        tiltButton.setEnabled(enabled);
    }
}
