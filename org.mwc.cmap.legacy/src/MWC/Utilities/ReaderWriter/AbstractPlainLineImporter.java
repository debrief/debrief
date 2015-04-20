package MWC.Utilities.ReaderWriter;


public abstract class AbstractPlainLineImporter implements PlainLineImporter
{
	protected String symbology;

	@Override
	public String getSymbology()
	{
		return symbology;
	}
}
