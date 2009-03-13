package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.text.AbstractTextLayer;
import com.borlander.rac525791.dashboard.text.CenteredText;
import com.borlander.rac525791.dashboard.text.TextDrawer;

public class ControlUnitsLayer extends AbstractTextLayer {
	private TextDrawer[] myDrawers;
	private static final Color GRAY = new Color(null, 196, 196, 196);
	
	CenteredText mySpeedUnits;
	CenteredText mySpeedMultiplier;
	
	CenteredText myDepthUnits;
	CenteredText myDepthMultiplier;
	CenteredText myDepthXOnly;
	
	public ControlUnitsLayer(DashboardUIModel uiModel){
		setLayoutManager(new Layout(uiModel));
		setForegroundColor(GRAY);
		
		mySpeedUnits = new CenteredText();
		mySpeedMultiplier = new CenteredText();
		myDepthUnits = new CenteredText();
		myDepthMultiplier = new CenteredText();
		myDepthXOnly = new CenteredText();
		myDrawers = new TextDrawer[] {mySpeedUnits, mySpeedMultiplier, myDepthUnits, myDepthMultiplier, myDepthXOnly};
	}
	
	public void setSpeedUnits(String units){
		mySpeedUnits.setText(units);
	}
	
	public void setSpeedMultiplier(int multiplier){
		mySpeedMultiplier.setText("x" + String.valueOf(multiplier));
	}
	
	public void setDepthUnits(String units){
		myDepthUnits.setText(units);
	}
	
	public void setDepthMultiplier(int multiplier){
		myDepthXOnly.setText("x");
		myDepthMultiplier.setText(String.valueOf(multiplier));
	}
	
	@Override
	protected TextDrawer[] getTextDrawers() {
		return myDrawers;
	}
	
	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();
		
		public Layout(DashboardUIModel uiModel){
			super(uiModel);
		}
		
		public void layout(IFigure container) {
			assert container == ControlUnitsLayer.this;
			//System.out.println("ControlUnitsLayer.Layout.layout()");
			
			ControlUISuite suite = getSuite(container);
			Font unitsFont = suite.getFonts().getUnitsFont();
			for (TextDrawer next : getTextDrawers()){
				next.setFont(unitsFont);
			}
			
			layoutUnitsAndMultipliers(suite.getSpeed(), mySpeedUnits, mySpeedMultiplier, container); 
			layoutUnitsAndMultipliers(suite.getDepth(), myDepthUnits, myDepthMultiplier, container);
			
			//now depth is layouted, we can place "x" using just set block positions
			ControlUIModel depth = suite.getDepth(); 
			final int X_WIDTH = 4;
			RECT.setSize(X_WIDTH, suite.getDepth().getUnitsAndMultipliersSize().height);

			placeAtTopLeft(container, RECT);
			RECT.translate(depth.getControlCenter());
			RECT.translate(depth.getUnitsPosition()); //at the top-left corner of "units"
			RECT.translate(-X_WIDTH, depth.getUnitsAndMultipliersSize().height / 2);
			myDepthXOnly.setBounds(RECT);
		}
		
		private void layoutUnitsAndMultipliers(ControlUIModel positions, CenteredText units, CenteredText multiplier, IFigure container){
			placeAtTopLeft(container, RECT);
			RECT.translate(positions.getControlCenter());
			RECT.translate(positions.getUnitsPosition());
			RECT.setSize(positions.getUnitsAndMultipliersSize());
			units.setBounds(RECT);
			
			RECT.translate(0, positions.getUnitsAndMultipliersSize().height);
			multiplier.setBounds(RECT);
		}
	}
}
