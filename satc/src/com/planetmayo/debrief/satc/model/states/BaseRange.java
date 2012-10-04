package com.planetmayo.debrief.satc.model.states;

public class BaseRange
{
	/** exception for when constraints end up in an incompatible state.  We immediately 
	 * terminate processing constraints when this happens, to we're going to throw the exception
	 * 
	 * @author ian
	 *
	 */
	public static class IncompatibleStateException extends RuntimeException
	{

		private final String _message;
		private final BaseRange _existingRange;
		private final BaseRange _newRange;

		public IncompatibleStateException(String message, BaseRange existingRange,
				BaseRange newRange)
		{
			_message = message;
			_existingRange = existingRange;
			_newRange = newRange;
		}
		
		public BaseRange getExistingRange()
		{
			return _existingRange;
		}
		public BaseRange getNewRange()
		{
			return _newRange;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
}
