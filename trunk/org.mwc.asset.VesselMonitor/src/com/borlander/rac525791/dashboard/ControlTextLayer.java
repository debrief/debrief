package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardFonts;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.text.AbstractTextLayer;
import com.borlander.rac525791.dashboard.text.CenteredText;
import com.borlander.rac525791.dashboard.text.TextDrawer;

public class ControlTextLayer extends AbstractTextLayer {
	
	private static final Color LIGHT_PINK = new Color(null, 255, 190, 190);
	private static final Color DARK_PINK = new Color(null, 255, 92, 92);
	private static final Color LIGHT_GREEN = new Color(null, 191, 255, 180);
	private static final Color DARK_GREEN = new Color(null, 92, 255, 64);
	
	GradientRoundedRectangle mySpeed;
	GradientRoundedRectangle myDepth;
	
	final CenteredText mySpeedText;
	final CenteredText myDepthText;
	
	private final TextDrawer[] myTextDrawers;
	
	public ControlTextLayer(DashboardUIModel uiModel){
		setLayoutManager(new Layout(uiModel));
		setForegroundColor(ColorConstants.black);
		
		mySpeed = new GradientRoundedRectangle();
		setGradient(mySpeed, true);
		
		myDepth = new GradientRoundedRectangle();
		setGradient(myDepth, true);
		
		this.add(mySpeed);
		this.add(myDepth);
		
		mySpeedText = new CenteredText();
		myDepthText = new CenteredText();
		
		myTextDrawers = new TextDrawer[] {mySpeedText, myDepthText};
	}
	
	public void setDepth(int depth){
		myDepthText.setText(formatValue(depth));
		invalidate();
	}
	
	public void setSpeed(int speed){
		mySpeedText.setText(formatValue(speed));
		invalidate();
	}
	
	public void updateDepthGradient(boolean isOnThreshold){
		setGradient(myDepth, isOnThreshold);
	}
	
	public void updateSpeedGradient(boolean isOnThreshold){
		setGradient(mySpeed, isOnThreshold);
	}
	
	@Override
	protected TextDrawer[] getTextDrawers() {
		return myTextDrawers;
	}
	
	private static void setGradient(GradientRoundedRectangle rect, boolean isOK){
		if (isOK){
			rect.setTopColor(LIGHT_GREEN);
			rect.setBottomColor(DARK_GREEN);
		} else {
			rect.setTopColor(LIGHT_PINK);
			rect.setBottomColor(DARK_PINK);
		}
		rect.invalidate();
	}
	
	private static String formatValue(int value){
		if (value < 0){
			throw new IllegalArgumentException("Expected not negative integer: " + value);
		}
		if (value < 10){
			return "." + value;
		}
		return String.valueOf(value / 10);
	}
	
	private static class GradientRoundedRectangle extends RoundedRectangle {
		private Color myTopColor = ColorConstants.red;
		private Color myBottomColor = ColorConstants.red;
		private Pattern myPattern;
		private final Rectangle CACHED_BOUNDS = new Rectangle();
		private final Rectangle TEMP = new Rectangle();
		
		public GradientRoundedRectangle(){
			setForegroundColor(ColorConstants.black);
			setFill(true);
			setOutline(true);
		}

		public void setTopColor(Color topColor){
			myTopColor = topColor;
			if (myPattern != null){
				myPattern.dispose();
			}
		}
		
		public void setBottomColor(Color bottomColor){
			myBottomColor = bottomColor;
			if (myPattern != null){
				myPattern.dispose();
			}
		}
		
		protected void fillShape(Graphics g) {
			g.pushState();
			g.setBackgroundPattern(getPattern(getBounds()));
			TEMP.setBounds(getBounds());
			TEMP.shrink(1, 1);
			g.fillRoundRectangle(TEMP, corner.width, corner.height);
			g.popState();
		}
		
		private Pattern getPattern(Rectangle localBounds){
			if (myPattern != null && !myPattern.isDisposed() && CACHED_BOUNDS.equals(localBounds)){
				return myPattern;
			}
			CACHED_BOUNDS.setBounds(localBounds);
			if (myPattern != null){
				myPattern.dispose();
			}
			myPattern = createPattern(localBounds);
			return myPattern;
		}
		
		private Pattern createPattern(Rectangle localBounds){
			int left = localBounds.x;
			int right = localBounds.x + localBounds.width;
			int top = localBounds.y;
			int bottom = localBounds.y + localBounds.height;
			return new Pattern(null, right, top, left, bottom, myTopColor, myBottomColor);
		}

	}
	
	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();
		
		public Layout(DashboardUIModel uiModel){
			super(uiModel);
		}
		
		public void layout(IFigure container) {
			assert container == ControlTextLayer.this;
			//System.out.println("ControlTextLayer.Layout.layout()");
			
			ControlUISuite suite = getSuite(container);
			DashboardFonts fonts = suite.getFonts();
			mySpeedText.setFont(fonts.getValueFont());
			myDepthText.setFont(fonts.getValueFont());
			
			layoutControlValue(suite.getSpeed(), mySpeed, mySpeedText, container);
			layoutControlValue(suite.getDepth(), myDepth, myDepthText, container);
		}
		
		private void layoutControlValue(ControlUIModel positions, GradientRoundedRectangle back, CenteredText text, IFigure container){
			placeAtTopLeft(container, RECT);
			RECT.translate(positions.getControlCenter());
			RECT.translate(positions.getValueTextPosition());
			RECT.setSize(positions.getValueTextSize());
			back.setBounds(RECT);
			
			RECT.shrink(1, 1);
			text.setBounds(RECT);
		}
	}
	
}
