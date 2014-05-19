package com.borlander.rac525791.dashboard.layout;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public abstract class SelectableImageFigure extends ImageFigure {
	private final DashboardUIModel mySelector;

	public SelectableImageFigure(DashboardUIModel selector){
		super();
		mySelector = selector;
	}
	
	@Override
	public void setBounds(Rectangle rect) {
		ControlUISuite suite = mySelector.getUISuite(rect.width, rect.height);
		Image image = selectImage(suite.getImages());
		setImage(image);
		super.setBounds(rect);
	}
	
	protected abstract Image selectImage(DashboardImages images);

}
