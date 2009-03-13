package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

class DefaultSliderPointer extends PointerDrawerBase implements PointerDrawer
{
	private static final Point LABEL_GAP = new Point(5, 0);

	private static final int[] TEMPLATE = new int[] { 0, -1, -7, -8, -7, -16, 7, -16, 7,
			-8, 0, -1, };

	private final int[] myPathArray;

	private final AreaGateImpl myAreaGate;

	private boolean myLabelAtLeftNotAtRight;

	private boolean myBelowNotAbove;

	private Color myBackground;

	private Color myForeground;

	private Color myLabelColor;

	private Font myBoldFont;

	public DefaultSliderPointer()
	{
		this(false, false);
	}

	public DefaultSliderPointer(boolean belowNotAbove, boolean labelAtLeftNotAtRight)
	{
		myAreaGate = new AreaGateImpl();
		myPathArray = new int[TEMPLATE.length];
		setBelowThePoint(false);
		myBackground = ColorManager.getInstance().getColor(127, 127, 127);
		myForeground = ColorManager.getInstance().getColor(0, 0, 0);
		myLabelColor = ColorManager.getInstance().getColor(0, 0, 255);
		setBelowThePoint(belowNotAbove);
		setLabelAtLeft(labelAtLeftNotAtRight);
	}

	public void freeResources()
	{
		if (myBackground != null)
		{
			ColorManager.getInstance().releaseColor(myBackground);
			myBackground = null;
		}
		if (myForeground != null)
		{
			ColorManager.getInstance().releaseColor(myForeground);
			myForeground = null;
		}
		if (myLabelColor != null)
		{
			ColorManager.getInstance().releaseColor(myLabelColor);
			myLabelColor = null;
		}
		if (myBoldFont != null)
		{
			myBoldFont.dispose();
			myBoldFont = null;
		}
	}

	public Rectangle getSize()
	{
		return myBelowNotAbove ? new Rectangle(-7, 1, 14, 15)
				: new Rectangle(-7, -16, 14, 15);
	}

	private void setLabelAtLeft(boolean labelAtLeftNotAtRight)
	{
		myLabelAtLeftNotAtRight = labelAtLeftNotAtRight;
	}

	public void setBelowThePoint(boolean belowNotAbove)
	{
		myBelowNotAbove = belowNotAbove;
	}

	public AreaGate getAreaGate()
	{
		return myAreaGate;
	}

	public void paintPointer(GC gc, int x, int y)
	{
		paintPointer(gc, x, y, null);
	}

	public void paintPointer(GC gc, int x, int y, String optionalLabel)
	{

		for (int i = 0; i < TEMPLATE.length; i += 2)
		{
			myPathArray[i] = TEMPLATE[i] + x;
			myPathArray[i + 1] = (!myBelowNotAbove) ? y - TEMPLATE[i + 1] : y + TEMPLATE[i + 1];
		}
		gc.setBackground(myBackground);
		gc.setForeground(myForeground);
		gc.fillPolygon(myPathArray);
		gc.drawPolygon(myPathArray);
		if (!myBelowNotAbove)
		{
			myAreaGate.update(x - 7, y + 1, 14, 15);
		}
		else
		{
			myAreaGate.update(x - 7, y - 16, 14, 15);
		}

		paintLabel(gc, optionalLabel);
	}

	private void paintLabel(GC gc, String label)
	{
		if (label == null)
		{
			return;
		}
		label = label.trim();
		if (label.length() == 0)
		{
			return;
		}
		int labelPointX;
		int labelPointY;
		Rectangle pointerArea = myAreaGate.getArea();
		Font oldFont = gc.getFont();
		gc.setFont(getLabelFont(gc));
		Point textSize = gc.textExtent(label);
		if (myLabelAtLeftNotAtRight)
		{
			labelPointX = pointerArea.x - LABEL_GAP.x - textSize.x;
		}
		else
		{
			labelPointX = pointerArea.x + pointerArea.width + LABEL_GAP.x;
		}
		if (myBelowNotAbove)
		{
			labelPointY = pointerArea.y + pointerArea.height - LABEL_GAP.y - textSize.y;
		}
		else
		{
			labelPointY = pointerArea.y + pointerArea.height - LABEL_GAP.y - textSize.y;
		}
		gc.setForeground(myLabelColor);
		gc.drawText(label, labelPointX, labelPointY, true);
		gc.setFont(oldFont);
	}

	private Font getLabelFont(GC gc)
	{
		if (myBoldFont == null)
		{
			myBoldFont = Util.deriveBold(gc.getFont());
		}
		return myBoldFont;
	}

	private static class AreaGateImpl implements AreaGate
	{
		private final Rectangle myArea;

		public AreaGateImpl()
		{
			myArea = new Rectangle(0, 0, 0, 0);
		}

		public void update(int x, int y, int width, int height)
		{
			myArea.x = x;
			myArea.y = y;
			myArea.width = width;
			myArea.height = height;
		}

		public boolean isInsideArea(int x, int y)
		{
			return myArea.contains(x, y);
		}

		public Rectangle getArea()
		{
			return myArea;
		}
	}
}
