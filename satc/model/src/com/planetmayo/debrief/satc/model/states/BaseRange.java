package com.planetmayo.debrief.satc.model.states;

public abstract class BaseRange<T extends BaseRange<?>>
{

	/**
	 * exception for when constraints end up in an incompatible state. We
	 * immediately terminate processing constraints when this happens, so we're
	 * going to throw the exception and let it propagate back up the call chain
	 * 
	 * @author ian
	 * 
	 */
	public static class IncompatibleStateException extends Exception
	{

		private final String _message;
		private final BaseRange<?> _existingRange;
		private final BaseRange<?> _newRange;

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public IncompatibleStateException(String message,
				BaseRange<?> existingRange, BaseRange<?> newRange)
		{
			super(message);
			_message = message;
			_existingRange = existingRange;
			_newRange = newRange;
		}

		/**
		 * get the existing (old) constraint
		 * 
		 * @return
		 */
		public BaseRange<?> getExistingRange()
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
		public BaseRange<?> getNewRange()
		{
			return _newRange;
		}

	}

	/**
	 * apply another ranged constraint to this one
	 * 
	 * @param other
	 *          the other ranged constraint (of the same type as this)
	 * @throws IncompatibleStateException
	 *           if the contraints are mutually exclusive
	 */
	abstract public void constrainTo(T other) throws IncompatibleStateException;

	/**
	 * provide a textual summary of this constraint
	 * 
	 * @return summary of this constraint - hopefully under about 20 chars
	 */
	abstract public String getConstraintSummary();
}
