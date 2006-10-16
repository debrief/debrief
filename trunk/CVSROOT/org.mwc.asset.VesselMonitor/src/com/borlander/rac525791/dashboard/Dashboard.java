package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.widgets.Composite;

import com.borlander.rac525791.dashboard.data.DashboardDataModel;

public class Dashboard extends FigureCanvas {
	private final DashboardFigure myRootFigure;
	private final DashboardDataModel myDataModel;
	
	public Dashboard(Composite parent){
		super(parent);
		setScrollBarVisibility(NEVER);
		myDataModel = new DashboardDataModel();
		myRootFigure = new DashboardFigure();
		new DashboardUpdater(myDataModel, myRootFigure, this);
		
		getViewport().setContentsTracksWidth(true);
		getViewport().setContentsTracksHeight(true);
		
		getViewport().setContents(myRootFigure);
	}
	
	public DashboardDataModel getDataModel() {
		return myDataModel;
	}
	
	public void dispose(){
		myRootFigure.dispose();
	}

}
