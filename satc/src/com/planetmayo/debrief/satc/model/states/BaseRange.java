package com.planetmayo.debrief.satc.model.states;

public class BaseRange
{

	/**
	 * exception for when constraints end up in an incompatible state. We
	 * immediately terminate processing constraints when this happens, so we're
	 * going to throw the exception and let it propagate back up the call chain
	 * 
	 * @author ian
	 * 
	 */
	public static class IncompatibleStateException extends RuntimeException
	{

		private final String _message;
		private final BaseRange _existingRange;
		private final BaseRange _newRange;

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public IncompatibleStateException(String message, BaseRange existingRange,
				BaseRange newRange)
		{
			_message = message;
			_existingRange = existingRange;
			_newRange = newRange;
		}

		/**
		 * get the existing (old) constraint
		 * 
		 * @return
		 */
		public BaseRange getExistingRange()
		{
			return _existingRange;
		}

		@Override
		public String getMessage()
		{
			return _message;
		}

		/**
		 * get the new constraint, the one we're trying to apply
		 * 
		 * @return
		 */
		public BaseRange getNewRange()
		{
			return _newRange;
		}

	}
}
