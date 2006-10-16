package com.borlander.rac525791.dashboard;

import java.text.NumberFormat;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.StackLayout;

import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;

public class DashboardFigure extends InvisibleRectangle {
	private static final String DEGREE = "\u00B0";
	private static NumberFormat ourFormatter;
	private PointersLayer myPointersLayer;
	private ControlTextLayer myControlTextLayer;
	private ControlUnitsLayer myControlUnitsLayer;
	private TextLayer myTextLayer;
	private final DashboardUIModel myUiModel;
	
	public DashboardFigure(){
		myUiModel = new DashboardUIModel();
		setLayoutManager(new StackLayout());
		
		this.add(createBackgroundLayer());
		this.add(createPointersLayer());
		this.add(createControlTextLayer());
		this.add(createTextLayer());
		this.add(createUnitsLayer());
	}
	
	public void dispose() {
		myUiModel.dispose();
	}
	
	public void setSpeed(int value){
		getSpeedArrows().setActualValue(value);
		myControlTextLayer.setSpeed(value);
	}
	
	public void setDepth(int value){
		getDepthArrows().setActualValue(value);
		myControlTextLayer.setDepth(value);
	}
	
	public void setDemandedSpeed(int value){
		getSpeedArrows().setDemandedValue(value);
	}	
	
	public void setDemandedDepth(int value){
		getDepthArrows().setDemandedValue(value);
	}
	
	public void setDirection(int value){
		getDirectionArrows().setActualValue(value);
		myTextLayer.setCenterText(formatCourse(value));
	}
	
	public void setDemandedDirection(int value){
		getDirectionArrows().setDemandedValue(value);
	}
	
	public void updateSpeedOnThreshold(boolean isOnThreshold){
		getSpeedArrows().updateShowRedSector(isOnThreshold);
		myControlTextLayer.updateSpeedGradient(isOnThreshold);
	}
	
	public void updateDepthOnThreshold(boolean isOnThreshold){
		getDepthArrows().updateShowRedSector(isOnThreshold);
		myControlTextLayer.updateDepthGradient(isOnThreshold);
	}
	
	public void updateDirectionOnThreshold(boolean isOnThreshold){
		getDirectionArrows().updateShowRedSector(isOnThreshold);
	}
	
	public void setSpeedUnits(String units){
		myControlUnitsLayer.setSpeedUnits(units);
	}
	
	public void setSpeedMultiplier(int multiplier){
		myControlUnitsLayer.setSpeedMultiplier(multiplier);
	}
	
	public void setDepthMultiplier(int multiplier){
		myControlUnitsLayer.setDepthMultiplier(multiplier);
	}
	
	public void setDepthUnits(String units){
		myControlUnitsLayer.setDepthUnits(units);
	}
	
	public void setVesselName(String name){
		myTextLayer.setLeftText(name);
	}
	
	public void setVesselStatus(String status){
		myTextLayer.setRightText(status);
	}
	
	private IFigure createBackgroundLayer(){
		return new BackgroundLayer(myUiModel);
	}
	
	private IFigure createPointersLayer() {
		myPointersLayer = new PointersLayer(myUiModel);
		return myPointersLayer;
	}
	
	private IFigure createControlTextLayer(){
		myControlTextLayer = new ControlTextLayer(myUiModel);
		return myControlTextLayer;
	}
	
	private IFigure createUnitsLayer(){
		myControlUnitsLayer = new ControlUnitsLayer(myUiModel);
		return myControlUnitsLayer;
	}
	
	private IFigure createTextLayer(){
		myTextLayer = new TextLayer(myUiModel);
		return myTextLayer;
	}
	
	private String formatCourse(int course){
		return getCourseFormatter().format(course) + DEGREE;
	}
	
	private ControlPointersLayer getSpeedArrows(){
		return myPointersLayer.getSpeedArrows();
	}
	
	private ControlPointersLayer getDepthArrows(){
		return myPointersLayer.getDepthArrows();
	}
	
	private ControlPointersLayer getDirectionArrows(){
		return myPointersLayer.getDirectionArrows();
	}

	private static NumberFormat getCourseFormatter(){
		if (ourFormatter == null){
			ourFormatter = NumberFormat.getInstance();
			ourFormatter.setMinimumIntegerDigits(3);
		}
		return ourFormatter;
	}

}
