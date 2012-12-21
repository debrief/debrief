package MWC.GUI;


/**
 * logging service that accomodates a surrogate - so the fancy RCP gui can
 * register as a logger with the legacy ASSET code
 * 
 * @author ian
 * 
 */
public class LoggingService implements ErrorLogger
{

	static ErrorLogger _substituteParent;
	static LoggingService _singleton;

	public static void initialise(ErrorLogger logger)
	{
		_substituteParent = logger;
	}

	@Override
	public void logError(int status, String text, Exception e)
	{
		if (_substituteParent != null)
			_substituteParent.logError(status, text, e);
		else
		{
			System.err.println("Error:" + text);
			e.printStackTrace();
		}

	}

	public static LoggingService INSTANCE()
	{
		if (_singleton != null)
			_singleton = new LoggingService();

		return _singleton;
	}

}
