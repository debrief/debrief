/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI;

/**
 * @author Ayesha
 *
 */
public class RestrictedBaseLayer<T extends Editable> extends BaseLayer {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RestrictedBaseLayer(boolean orderedChildren){
		super(orderedChildren);
	}
	public void addElement(T item) {
		super.add(item);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Editable thePlottable) {
		addElement((T)thePlottable);
	}

	
}