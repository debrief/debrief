/**
 * 
 */
package MWC.Utilities.ReaderWriter.XML;

/** for classes that can send an item to XML file
 * 
 * @author ianmayo
 *
 */
public interface PlottableExporter
{
	public void exportThisPlottable(MWC.GUI.Plottable plottable,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc);
}