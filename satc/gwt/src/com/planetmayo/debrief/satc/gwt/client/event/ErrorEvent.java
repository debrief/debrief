package com.planetmayo.debrief.satc.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ErrorEvent extends GwtEvent<ErrorEventHandler> {
	public static Type<ErrorEventHandler> TYPE = new Type<ErrorEventHandler>();
	private String[] errors;
	
	public ErrorEvent(String...errors){
		this.setErrors(errors);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ErrorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ErrorEventHandler handler) {
		handler.error(this);
	}

	public String[] getErrors() {
		return errors;
	}

	public void setErrors(String[] errors) {
		this.errors = errors;
	}

}
