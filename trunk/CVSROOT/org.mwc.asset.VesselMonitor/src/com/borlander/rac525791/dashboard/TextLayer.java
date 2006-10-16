package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardFonts;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.text.AbstractTextLayer;
import com.borlander.rac525791.dashboard.text.CenteredText;
import com.borlander.rac525791.dashboard.text.TextDrawer;
import com.borlander.rac525791.dashboard.text.TwoLinesText;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;

public class TextLayer extends AbstractTextLayer {
	private static final Color TEXT_COLOR = new Color(null, 33, 255, 22);

	private final TextDrawer[] myTextDrawers;
	
	private TwoLinesText myLeftText;
	private TwoLinesText myRightText;
	private CenteredText myCenterText;
	
	private ShadowRectangle myLeftPanel;
	private ShadowRectangle myRightPanel;

	public TextLayer(DashboardUIModel uiModel){
		setForegroundColor(TEXT_COLOR);
		setLayoutManager(new Layout(uiModel));

		myLeftText = new TwoLinesText();
		myRightText = new TwoLinesText();
		myCenterText = new CenteredText(); 
		
		myTextDrawers = new TextDrawer[] {myRightText, myLeftText, myCenterText};
		
		myLeftPanel = new ShadowRectangle();
		myRightPanel = new ShadowRectangle();
		
		this.add(myLeftPanel);
		this.add(myRightPanel);
		
		setLeftText("");
		setRightText("");
		setCenterText("");
	}
	
	@Override
	protected TextDrawer[] getTextDrawers() {
		return myTextDrawers;
	}

	public void setLeftText(String text) {
		myLeftText.setText(safeText(text));
		invalidate();
	}

	public void setRightText(String text) {
		myRightText.setText(safeText(text));
		invalidate();
	}
	
	public void setCenterText(String text){
		myCenterText.setText(safeText(text));
		invalidate();
	}
	
	private static String safeText(String text){
		return text == null ? "" : text.trim();
	}
	
	private class Layout extends BaseDashboardLayout {
		private final Rectangle TOP = new Rectangle();
		private final Rectangle BOTTOM = new Rectangle();
		private final Rectangle UNION = new Rectangle();
	
		public Layout(DashboardUIModel uiModel){
			super(uiModel);
		}
		
		public void layout(IFigure container) {
			ControlUISuite suite = getSuite(container);
			DashboardFonts fonts = suite.getFonts();
			
			myLeftText.setFont(fonts.getTextFont());
			myRightText.setFont(fonts.getTextFont());
			myCenterText.setFont(fonts.getValueFont());
			
			layoutTwoLinesText(container, myLeftText, myLeftPanel, suite.getVesselNameBounds());
			layoutTwoLinesText(container, myRightText, myRightPanel, suite.getVesselStatusBounds());

			placeAtTopLeft(container, TOP);
			Rectangle courseBounds = suite.getCourseValueBounds(); 
			TOP.setSize(courseBounds.width, courseBounds.height);
			TOP.translate(courseBounds.x, courseBounds.y);
			
			myCenterText.setBounds(TOP);
		}
		
		private void layoutTwoLinesText(IFigure container, TwoLinesText twoLinesText, IFigure panel, Rectangle name){
			placeAtTopLeft(container, UNION);
			
			UNION.translate(name.x, name.y);
			UNION.setSize(name.width, name.height);
			UNION.shrink(2, 2);
			
			TOP.setLocation(UNION.x, UNION.y);
			TOP.setSize(UNION.width, UNION.height / 2);
			
			BOTTOM.setLocation(UNION.x, UNION.y);
			BOTTOM.setSize(UNION.width, UNION.height - UNION.height / 2);
			BOTTOM.translate(0, UNION.height / 2);

			twoLinesText.setBounds(TOP, BOTTOM);
			if (panel != null){
				panel.setBounds(UNION);
			}
		}
	}
	
	private static class ShadowRectangle extends InvisibleRectangle {
		private static final Color SHADOW = new Color(null, 196, 196, 196);
		private static final Color FORE = ColorConstants.black;
		private static final Color TOP = new Color(null, 51, 51, 51);
		private static final Color BOTTOM = new Color(null, 115, 115, 115);
		
		private final Color myShadowColor;
		private final Color myForeColor;
		private final Color myTopColor;
		private final Color myBottomColor;
		private final int myShadowX;
		private final int myShadowY;
		
		public ShadowRectangle(){
			this(1, 1, FORE, SHADOW, TOP, BOTTOM);
		}

		public ShadowRectangle(int shadowX, int shadowY, Color foreColor, Color shadowColor, Color topColor, Color bottomColor){
			myShadowX = shadowX;
			myShadowY = shadowY;
			myForeColor = foreColor;
			myShadowColor = shadowColor;
			myTopColor = topColor;
			myBottomColor = bottomColor;
		}
		
		protected void outlineShape(Graphics g) {
			Rectangle b = getBounds();
			
			int x = b.x;
			int y = b.y;
			int width = b.width;
			int height = b.height;
			
			g.pushState();
			g.setForegroundColor(myShadowColor);
			g.drawRectangle(x + myShadowX, y + myShadowY, width - myShadowX, height - myShadowY);
			
			g.setForegroundColor(myForeColor);
			g.drawRectangle(x, y, width - myShadowX, height - myShadowY);
			g.popState();
		}
		
		protected void fillShape(Graphics g) {
			Rectangle b = getBounds();
			
			int x = b.x;
			int y = b.y;
			int width = b.width;
			int height = b.height;

			g.pushState();
			g.setForegroundColor(myTopColor);
			g.setBackgroundColor(myBottomColor);
			g.fillGradient(x + 1, y + 1, width - myShadowX - 2, height - myShadowY - 2, true);
			g.popState();
		}
	}
}
