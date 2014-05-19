package ASSET.Util.XML.Control.Observers;
import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Recording.RecordStatusToCloudObserverType;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

/**
 * read in a debrief replay observer from file
 */
abstract class CloudObserverHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private final static String type = "CloudObserver";

	boolean _recordDetections = false;
	boolean _recordPositions = false;
	boolean _recordDecisions = false;
	TargetType _targetType = null;

	private static final String RECORD_DETECTIONS = "record_detections";
	private static final String RECORD_DECISIONS = "record_decisions";
	private static final String RECORD_POSITIONS = "record_positions";
	private static final String TARGET_TYPE = "SubjectToTrack";

	private static final String DB_URL = "hostURL";

	private final static String ACTIVE = "Active";

	private static final String DATABASE = "database";

	private String _url;
	private String _database;
	private boolean _active;
	protected String _name = "Proximity Observer";

	public CloudObserverHandler(String type)
	{
		super(type);

		addAttributeHandler(new HandleBooleanAttribute(RECORD_DETECTIONS)
		{
			public void setValue(String name, final boolean val)
			{
				_recordDetections = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(RECORD_DECISIONS)
		{
			public void setValue(String name, final boolean val)
			{
				_recordDecisions = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(RECORD_POSITIONS)
		{
			public void setValue(String name, final boolean val)
			{
				_recordPositions = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
		{
			public void setValue(String name, final boolean val)
			{
				_active = val;
			}
		});

		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, final String val)
			{
				_name = val;
			}
		});
		addAttributeHandler(new HandleAttribute(DB_URL)
		{
			public void setValue(String name, final String val)
			{
				_url = val;
			}
		});
		addAttributeHandler(new HandleAttribute(DATABASE)
		{
			public void setValue(String name, final String val)
			{
				_database = val;
			}
		});

		addHandler(new TargetTypeHandler(TARGET_TYPE)
		{
			public void setTargetType(TargetType type1)
			{
				_targetType = type1;
			}
		});

	}

	public CloudObserverHandler()
	{
		this(type);
	}

	public void elementClosed()
	{
		// create ourselves
		final ScenarioObserver debriefObserver = getObserver(_name, _active,
				_recordDetections, _recordDecisions, _recordPositions, _targetType,
				_url, _database);

		setObserver(debriefObserver);

		// close the parenet
		super.elementClosed();

		// and clear the data
		_recordDetections = false;
		_recordDecisions = false;
		_recordPositions = true;
		_targetType = null;

	}

	protected ScenarioObserver getObserver(String name, boolean isActive,
			boolean recordDetections, boolean recordDecisions,
			boolean recordPositions, TargetType subject, String _url2,
			String _database2)
	{
		return new RecordStatusToCloudObserverType(recordDetections,
				recordDecisions, recordPositions, subject, name, isActive, _url2,
				_database2);
	}

	abstract public void setObserver(ScenarioObserver obs);

	static public void exportThis(final Object toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		throw new RuntimeException("NOT IMPLEMENTED");

	}

}