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
package MWC.GUI.Tools.Operations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import MWC.GUI.Plottable;

/**
 * @author Ayesha
 *
 */
public class CloneUtil {
	//////////////////////////////////////////////
	// clone items, using "Serializable" interface
	/////////////////////////////////////////////////
	static public Plottable cloneThis(final Plottable item) {
		Plottable res = null;
		try {
			final ByteArrayOutputStream bas = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(bas);
			oos.writeObject(item);
			// get closure
			oos.close();
			bas.close();

			// now get the item
			final byte[] bt = bas.toByteArray();

			// and read it back in as a new item
			final ByteArrayInputStream bis = new ByteArrayInputStream(bt);

			// create the reader with class loader from original ClassLoader
			final ObjectInputStream iis = new ObjectInputStream(bis) {

				@Override
				protected Class<?> resolveClass(final ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					return item.getClass().getClassLoader().loadClass(desc.getName());
				}
			};

			// and read it in
			final Object oj = iis.readObject();

			// get more closure
			bis.close();
			iis.close();

			if (oj instanceof Plottable) {
				res = (Plottable) oj;
			}
		} catch (final Exception e) {
			MWC.Utilities.Errors.Trace.trace(e);
		}
		return res;
	}

}
