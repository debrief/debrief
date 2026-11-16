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

package ASSET.Models.Movement;

import static ASSET.Models.Movement.ThreeDimMovementCharacteristics.*;
import ASSET.Models.Movement.ThreeDimMovementCharacteristics.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////
// property testing
//////////////////////////////////////////////////
public class ThreeDimMovementCharacteristicsTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new ThreeDimMovementCharacteristics("", null, null, 12, null, null, null, null, null, null) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * get the turning circle diameter (m) at this speed (in m/sec)
			 */
			@Override
			public double getTurningCircleDiameter(final double m_sec) {
				return 12;
			}
		};
	}
}
