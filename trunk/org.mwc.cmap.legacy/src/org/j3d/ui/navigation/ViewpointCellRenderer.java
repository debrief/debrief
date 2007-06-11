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
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

// Application specific imports
// none

/**
 * An implementation of a ListCellRenderer specific for viewpoint data
 * in the viewpoint selection toolbar.
 * <p>
 *
 * The cell renderer is based on a simple JLabel that takes the description
 * string from the ViewpointData and renders that.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
class ViewpointCellRenderer extends JLabel implements ListCellRenderer
{

    /** The selected border when the cell has focus */
    private Border focusBorder;

    /** The empty border when there is no focus */
    private Border noFocusBorder;

    /**
     * Create a new default cell renderer.
     */
    public ViewpointCellRenderer()
    {
        setOpaque(true);
        noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        focusBorder = UIManager.getBorder("List.focusCellHighlightBorder");

        setBorder(noFocusBorder);
    }

    /**
     * Create a new renderer for the given component information and the
     * setup values
     *
     * @param list The list this item came from
     * @param value The value to be rendered (ViewpointData instance)
     * @param index The index in the list
     * @param isSelected true if this component is selected
     * @param cellHasFocus true if the cell is currently focussed
     * @return The Renderer for the values given
     */
    public Component getListCellRendererComponent(JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                           boolean cellHasFocus)
    {
        setComponentOrientation(list.getComponentOrientation());

        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if(value == null)
            setText("");
        else
            setText(((ViewpointData)value).name);

        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setBorder((cellHasFocus) ? focusBorder : noFocusBorder);

        return this;
    }

    /**
     * Overridden for performance reasons to provide an empty method when no
     * processing really needs to be done.
     */
    public void validate()
    {
    }

    /**
     * Overridden for performance reasons to provide an empty method when no
     * processing really needs to be done.
     */
    public void revalidate()
    {
    }

    /**
     * Overridden for performance reasons to provide an empty method when no
     * processing really needs to be done.
     */
    public void repaint(long tm, int x, int y, int width, int height)
    {
    }

    /**
     * Overridden for performance reasons to provide an empty method when no
     * processing really needs to be done.
     */
    public void repaint(Rectangle r)
    {
    }
 }