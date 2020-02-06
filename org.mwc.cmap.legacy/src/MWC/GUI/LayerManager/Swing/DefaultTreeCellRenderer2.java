/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GUI.LayerManager.Swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Displays an entry in a tree. See <a href=
 * "http://java.sun.com/docs/books/tutorial/uiswing/components/tree.html">How to
 * Use Trees</a> in <em>The Java Tutorial</em> for examples of customizing node
 * display using this class.
 * <p>
 *
 * <strong><a name="override">Implementation Note:</a></strong> This class
 * overrides <code>validate</code>, <code>revalidate</code>,
 * <code>repaint</code>, and <code>firePropertyChange</code> solely to improve
 * performance. If not overridden, these frequently called methods would execute
 * code paths that are unnecessary for the default tree cell renderer. If you
 * write your own renderer, take care to weigh the benefits and drawbacks of
 * overriding these methods.
 *
 * <p>
 * <strong>Warning:</strong> Serialized objects of this class will not be
 * compatible with future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Swing. A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.40 02/02/00
 * @author Rob Davis
 * @author Ray Ryan
 * @author Scott Violet
 */
public class DefaultTreeCellRenderer2 extends DefaultTreeCellRenderer {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final boolean drawsFocusBorderAroundIcon;

	// Colors

	/**
	 * Returns a new instance of DefaultTreeCellRenderer. Alignment is set to left
	 * aligned. Icons and text color are determined from the UIManager.
	 */
	public DefaultTreeCellRenderer2() {
		setHorizontalAlignment(SwingConstants.LEFT);

		setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
		setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
		setOpenIcon(UIManager.getIcon("Tree.openIcon"));

		setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
		setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
		setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
		setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
		setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
		final Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
		drawsFocusBorderAroundIcon = (value != null && ((Boolean) value).booleanValue());
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final byte oldValue, final byte newValue) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final char oldValue, final char newValue) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final double oldValue, final double newValue) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final float oldValue, final float newValue) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final int oldValue, final int newValue) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final long oldValue, final long newValue) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		// Strings get interned...
		if (propertyName == "text")
			super.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final short oldValue, final short newValue) {
	}

	/**
	 * Returns the background color to be used for non selected nodes.
	 */
	@Override
	public Color getBackgroundNonSelectionColor() {
		return backgroundNonSelectionColor;
	}

	/**
	 * Returns the color to use for the background if node is selected.
	 */
	@Override
	public Color getBackgroundSelectionColor() {
		return backgroundSelectionColor;
	}

	/**
	 * Returns the color the border is drawn.
	 */
	@Override
	public Color getBorderSelectionColor() {
		return borderSelectionColor;
	}

	/**
	 * Returns the icon used to represent non-leaf nodes that are not expanded.
	 */
	@Override
	public Icon getClosedIcon() {
		return closedIcon;
	}

	/**
	 * Returns the default icon, for the current laf, that is used to represent
	 * non-leaf nodes that are not expanded.
	 */
	@Override
	public Icon getDefaultClosedIcon() {
		return UIManager.getIcon("Tree.closedIcon");
	}

	/**
	 * Returns the default icon, for the current laf, that is used to represent leaf
	 * nodes.
	 */
	@Override
	public Icon getDefaultLeafIcon() {
		return UIManager.getIcon("Tree.leafIcon");
	}

	/**
	 * Returns the default icon, for the current laf, that is used to represent
	 * non-leaf nodes that are expanded.
	 */
	@Override
	public Icon getDefaultOpenIcon() {
		return UIManager.getIcon("Tree.openIcon");
	}

	private int getLabelStart() {
		final Icon currentI = getIcon();
		if (currentI != null && getText() != null) {
			return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
		}
		return 0;
	}

	/**
	 * Returns the icon used to represent leaf nodes.
	 */
	@Override
	public Icon getLeafIcon() {
		return leafIcon;
	}

	/**
	 * Returns the icon used to represent non-leaf nodes that are expanded.
	 */
	@Override
	public Icon getOpenIcon() {
		return openIcon;
	}

	/**
	 * Overrides <code>JComponent.getPreferredSize</code> to return slightly wider
	 * preferred size value.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension retDimension = super.getPreferredSize();

		if (retDimension != null)
			retDimension = new Dimension(retDimension.width + 3, retDimension.height);
		return retDimension;
	}

	/**
	 * Returns the color the text is drawn with when the node isn't selected.
	 */
	@Override
	public Color getTextNonSelectionColor() {
		return textNonSelectionColor;
	}

	/**
	 * Returns the color the text is drawn with when the node is selected.
	 */
	@Override
	public Color getTextSelectionColor() {
		return textSelectionColor;
	}

	/**
	 * Configures the renderer based on the passed in components. The value is set
	 * from messaging the tree with <code>convertValueToText</code>, which
	 * ultimately invokes <code>toString</code> on <code>value</code>. The
	 * foreground color is set based on the selection and the icon is set based on
	 * on leaf and expanded.
	 */
	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus1) {
		final String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus1);

		this.hasFocus = hasFocus1;
		setText(stringValue);
		if (sel)
			setForeground(getTextSelectionColor());
		else
			setForeground(getTextNonSelectionColor());
		// There needs to be a way to specify disabled icons.
		if (!tree.isEnabled()) {
			setEnabled(false);
			if (leaf) {
				setDisabledIcon(getLeafIcon());
			} else if (expanded) {
				setDisabledIcon(getOpenIcon());
			} else {
				setDisabledIcon(getClosedIcon());
			}
		} else {
			setEnabled(true);
			if (leaf) {
				setIcon(getLeafIcon());
			} else if (expanded) {
				setIcon(getOpenIcon());
			} else {
				setIcon(getClosedIcon());
			}
		}
		setComponentOrientation(tree.getComponentOrientation());

		selected = sel;

		return this;
	}

	/**
	 * Paints the value. The background is filled based on selected.
	 */
	@Override
	public void paint(final Graphics g) {
		Color bColor;

		if (selected) {
			bColor = getBackgroundSelectionColor();
		} else {
			bColor = getBackgroundNonSelectionColor();
			if (bColor == null)
				bColor = getBackground();
		}
		int imageOffset = -1;
		if (bColor != null) {
			imageOffset = getLabelStart();
			g.setColor(bColor);
			if (getComponentOrientation().isLeftToRight()) {
				g.fillRect(imageOffset, 0, getWidth() - 1 - imageOffset, getHeight());
			} else {
				g.fillRect(0, 0, getWidth() - 1 - imageOffset, getHeight());
			}
		}

		if (hasFocus) {
			if (drawsFocusBorderAroundIcon) {
				imageOffset = 0;
			} else if (imageOffset == -1) {
				imageOffset = getLabelStart();
			}
			final Color bsColor = getBorderSelectionColor();

			if (bsColor != null) {
				g.setColor(bsColor);
				if (getComponentOrientation().isLeftToRight()) {
					g.drawRect(imageOffset, 0, getWidth() - 1 - imageOffset, getHeight() - 1);
				} else {
					g.drawRect(0, 0, getWidth() - 1 - imageOffset, getHeight() - 1);
				}
			}
		}
		super.paint(g);
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void repaint(final long tm, final int x, final int y, final int width, final int height) {
		System.out.println("repa1");
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void repaint(final Rectangle r) {
		System.out.println("repa2");
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void revalidate() {
		System.out.println("reval");
	}

	/**
	 * Subclassed to map <code>ColorUIResource</code>s to null. If
	 * <code>color</code> is null, or a <code>ColorUIResource</code>, this has the
	 * effect of letting the background color of the JTree show through. On the
	 * other hand, if <code>color</code> is non-null, and not a
	 * <code>ColorUIResource</code>, the background becomes <code>color</code>.
	 */
	@Override
	public void setBackground(final Color color) {
		Color theColor = color;
		if (theColor instanceof ColorUIResource)
			theColor = null;
		super.setBackground(theColor);
	}

	/**
	 * Sets the background color to be used for non selected nodes.
	 */
	@Override
	public void setBackgroundNonSelectionColor(final Color newColor) {
		backgroundNonSelectionColor = newColor;
	}

	/**
	 * Sets the color to use for the background if node is selected.
	 */
	@Override
	public void setBackgroundSelectionColor(final Color newColor) {
		backgroundSelectionColor = newColor;
	}

	/**
	 * Sets the color to use for the border.
	 */
	@Override
	public void setBorderSelectionColor(final Color newColor) {
		borderSelectionColor = newColor;
	}

	/**
	 * Sets the icon used to represent non-leaf nodes that are not expanded.
	 */
	@Override
	public void setClosedIcon(final Icon newIcon) {
		closedIcon = newIcon;
	}

	/**
	 * Subclassed to map <code>FontUIResource</code>s to null. If <code>font</code>
	 * is null, or a <code>FontUIResource</code>, this has the effect of letting the
	 * font of the JTree show through. On the other hand, if <code>font</code> is
	 * non-null, and not a <code>FontUIResource</code>, the font becomes
	 * <code>font</code>.
	 */
	@Override
	public void setFont(final Font fnt) {
		Font theFont = fnt;
		if (theFont instanceof FontUIResource)
			theFont = null;
		super.setFont(theFont);
	}

	/**
	 * Sets the icon used to represent leaf nodes.
	 */
	@Override
	public void setLeafIcon(final Icon newIcon) {
		leafIcon = newIcon;
	}

	/**
	 * Sets the icon used to represent non-leaf nodes that are expanded.
	 */
	@Override
	public void setOpenIcon(final Icon newIcon) {
		openIcon = newIcon;
	}

	/**
	 * Sets the color the text is drawn with when the node isn't selected.
	 */
	@Override
	public void setTextNonSelectionColor(final Color newColor) {
		textNonSelectionColor = newColor;
	}

	/**
	 * Sets the color the text is drawn with when the node is selected.
	 */
	@Override
	public void setTextSelectionColor(final Color newColor) {
		textSelectionColor = newColor;
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void validate() {
		System.out.println("val");
	}

}
