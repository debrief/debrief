package org.mwc.debrief.dis.diagnostics.file;

/** utility class that is capable of writing logging messages
 * The class has been created in order that we can overwrite the
 * default behaviour of CoreFileListener
 * 
 * @author ian
 *
 */
public interface LoggingFileWriter
{
  public void writeThis(final String dType, final String header, final String output);
}
