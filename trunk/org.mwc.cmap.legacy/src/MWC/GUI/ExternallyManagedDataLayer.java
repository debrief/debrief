package MWC.GUI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * class that represents an external datafile
 * 
 * @author ian
 * 
 */
public class ExternallyManagedDataLayer extends BaseLayer
{

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class ExternalLayerInfo extends Editable.EditorType
	{

		public ExternalLayerInfo(ExternallyManagedDataLayer data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{ prop("Visible", "the Layer visibility", VISIBILITY),
						prop("Name", "the name of the Layer", FORMAT) };

				return res;

			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * filename where data is loaded from
	 * 
	 */
	private final String _fileName;

	/**
	 * the type of this data (used to decide which decoder/manager to use)
	 * 
	 */
	private final String _dataType;

	public ExternallyManagedDataLayer(String dataType, String layerName,
			String fileName)
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
	

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new ExternalLayerInfo(this);

		return _myEditor;
	}
}
