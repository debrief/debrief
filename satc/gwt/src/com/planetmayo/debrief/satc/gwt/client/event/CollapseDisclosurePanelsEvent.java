package com.planetmayo.debrief.satc.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CollapseDisclosurePanelsEvent extends GwtEvent<CollapseDisclosurePanelsHandler> {
	public static Type<CollapseDisclosurePanelsHandler> TYPE = new Type<CollapseDisclosurePanelsHandler>();
	
	public CollapseDisclosurePanelsEvent(){
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CollapseDisclosurePanelsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CollapseDisclosurePanelsHandler handler) {
		handler.close(this);
	}


}
