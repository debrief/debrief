package edu.nps.moves.examples;

import edu.nps.moves.dis.Pdu;

/**
 * Uses the comparator interface to allow sorting PDUs in a collection by
 * timestamp. An example of use:
 *
 * Colletions.sort(pduList, new TimestampComparator());
 *
 * @author DMcG
 */
public class TimestampComparator {
	/**
	 * Returns a number less than, equal to, or greater than zero, depending on
	 * whether the object's timestamp less than, equal to, or greater than the other
	 * object. The objects passed in must inherit from PDU.
	 */
	public int compare(final Object object1, final Object object2) {
		// Should exception throw here...
		if ((!(object1 instanceof Pdu)) || (!(object2 instanceof Pdu))) {
			throw new RuntimeException("TimestampComparator: attempting to sort objects that are not Pdus.");
		}

		final Pdu pdu1 = (Pdu) object1;
		final Pdu pdu2 = (Pdu) object2;

		return (int) (pdu1.getTimestamp() - pdu2.getTimestamp());
	}

	/**
	 * Returns true if this comparator is the same class as the comparator passed
	 * in.
	 *
	 * @param obj
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj.getClass().equals(this.getClass());
	}

}
