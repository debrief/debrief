package MWC.GUI;


/** class that represents an external datafile
 * 
 * @author ian
 *
 */
public class ExternallyManagedDataLayer extends BaseLayer
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** filename where data is loaded from
	 * 
	 */
	private final String _fileName;

	/** the type of this data (used to decide which decoder/manager to use)
	 * 
	 */
	private final String _dataType;

	public ExternallyManagedDataLayer(String dataType, String layerName, String fileName)
	{
		setName(layerName);
		_dataType = dataType;
		_fileName = fileName;
	}
	
	public final String getDataType()
	{
		return _dataType;
	}
	
	public final String getFilename()
	{
		return _fileName;
	}
}
