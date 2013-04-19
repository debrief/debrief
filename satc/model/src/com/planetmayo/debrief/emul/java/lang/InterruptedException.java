package com.planetmayo.debrief.emul.java.lang;

public class InterruptedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public InterruptedException()
	{
		super();
	}

	public InterruptedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InterruptedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InterruptedException(String message)
	{
		super(message);
	}

	public InterruptedException(Throwable cause)
	{
		super(cause);
	}
}
