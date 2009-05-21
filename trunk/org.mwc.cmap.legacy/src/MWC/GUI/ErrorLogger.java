package MWC.GUI;

/** interface for classes that are able to fire/throw error messages
 * 
 * @author Ian Mayo
 *
 */
public interface ErrorLogger {
	/** log an error, somehow
	 * 
	 * @param status the status code (error, warning, etc)
	 * @param text the error message to display/record
	 * @param e any relevant exceptin
	 */
	  public void logError(int status, String text, Exception e);
}
