/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package info.limpet;

/**
 * parent for document builder classes
 *
 * @author Ian
 *
 */
public interface IDocumentBuilder<T extends Object> {
	/**
	 * add an indexed value to this builder
	 *
	 * @param index
	 * @param value
	 */
	public void add(double index, T value);

	/**
	 * add a value to this builder
	 *
	 * @param value
	 */
	public void add(T value);

	/**
	 * convert one-self into a document
	 *
	 * @return
	 */
	IDocument<T> toDocument();

}
