package atomExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

public class ExportDatabase
{

	private static final String DATABASE_PASSWORD = "PASSWORD";
	private static final String DATABASE_ROOT = "jdbc:postgresql://86.134.91.5:5432/gnd";
//	private static final String GEOSERVER_ROOT = "http://86.134.91.5:8080/geoserver/wms/";
//	private static final String DATA_ROOT = "c:\\tmp\\atomData\\";
	private static Connection _conn;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("started export of database to Atom");
		Date startD = new Date();
		// check we have the driver
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Failed to load database driver");
			e.printStackTrace();
		}

		connectToDatabase();

		exportCore("SELECT * from datasetsview order by datasetid asc;", "datasets");
		exportCore(
				"SELECT * from datasetsview where datasetid <= 10 order by datasetid asc;",
				"datasets_filter1");
		exportCore(
				"SELECT * from datasetsview where datasetid > 10 order by datasetid asc;",
				"datasets_filter2");

		// first the detailed versions
		exportDetail(true);
		
		// now the bare minimum version
		exportDetail(false);

		exportCategory("Platforms", "PlatformId", "PlatformName", "Platforms");
		exportCategory("Exercises", "ExerciseId", "ExerciseName", "Exercises");
		exportCategory("Formats", "FormatId", "FormatName", "Formats");

		exportServiceDoc();

		Date endD = new Date();
		System.out.println("took " + (endD.getTime() - startD.getTime()) / 1000
				+ " secs");

	}

	private static void exportServiceDoc()
	{
//		Factory factory = Abdera.getNewFactory();
//		Service service = factory.newService();
//		// first the core
//		Workspace core = service.addWorkspace("Core");
//		Collection coreData = factory.newCollection();
//		coreData.setTitle("Core");
//		coreData.setHref("datasets.xml");
//		core.addCollection(coreData);
//		Categories tgtCats = factory.newCategories();
//		loadCategories("platforms", "platformId", tgtCats);
//		loadCategories("formats", "formatId", tgtCats);
//		loadCategories("exercises", "exerciseId", tgtCats);
//		coreData.addCategories(tgtCats);
//
//		// now the workspace
//		Workspace detail = service.addWorkspace("detail");
//		detail
//				.addComment("We don't include detail collections, there are too many");
//		// and output it
//		try
//		{
//			service.writeTo("prettyxml", new FileOutputStream(DATA_ROOT
//					+ "service.xml"));
//			service.writeTo("json", new FileOutputStream(DATA_ROOT + "servce.json"));
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}

	}

	private static void exportCategory(String table, String idField,
			String nameField, String outputFile)
	{
//		Categories theseCats = loadCategories(table, idField, null);
//		// and output the file
//		File oFile = new File(DATA_ROOT + "cats");
//		oFile.mkdir();
//		try
//		{
//			theseCats.writeTo("prettyxml", new FileOutputStream(DATA_ROOT + "cats\\"
//					+ table + ".xml"));
//			theseCats.writeTo("json", new FileOutputStream(DATA_ROOT + "cats\\"
//					+ table + ".json"));
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
	}

//	private static Categories loadCategories(String table, String idField,
//			Categories theseCats)
//	{
//		Abdera abdera = new Abdera();
//		Factory factory = abdera.getFactory();
//		ResultSet rsf;
//		Statement st;
//		if (theseCats == null)
//		{
//			theseCats = factory.newCategories();
//			theseCats.setFixed(true);
//		}
//		try
//		{
//			st = _conn.createStatement();
//
//			// get the list of datasets
//			rsf = st.executeQuery("SELECT * from " + table + " ORDER BY " + idField
//					+ " ASC ;");
//
//			// loop through them
//			while (rsf.next())
//			{
//				Category thisC = factory.newCategory();
//				thisC.setScheme("cats/" + table + ".xml");
//				thisC.setTerm(rsf.getString(1));
//				thisC.setLabel(rsf.getString(2));
//				theseCats.addCategory(thisC);
//			}
//
//			rsf.close();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		return theseCats;
//	}

	private static void exportDetail(boolean longVersion)
	{
//		Abdera abdera = new Abdera();
//		Factory factory = abdera.getFactory();
//		ResultSet rsf, rse;
//		Statement st, st2;
//		try
//		{
//			st = _conn.createStatement();
//			st2 = _conn.createStatement();
//
//			// get the list of datasets
//			rsf = st.executeQuery("SELECT * from datasetsview;");
//
//			// loop through datasets
//			while (rsf.next())
//			{
//				String thisId = rsf.getString(1);
//
//				// create this feed
//				Feed feed = abdera.newFeed();
//				feed.setId(thisId);
//				feed.addAuthor("Ian Mayo");
//				// sort out the date
//				feed.setUpdated(rsf.getTimestamp("endtime"));
//				feed.setTitle("Dataset id:" + thisId);
//
//				Category platCat = factory.newCategory();
//				platCat.setScheme("cats/platforms.xml");
//				Category formatCat = factory.newCategory();
//				formatCat.setScheme("cats/formats.xml");
//				Category exCat = factory.newCategory();
//				exCat.setScheme("cats/exercises.xml");
//				platCat.setTerm(rsf.getString("platformid"));
//				platCat.setLabel(rsf.getString("platformname"));
//				formatCat.setTerm(rsf.getString("formatid"));
//				formatCat.setLabel(rsf.getString("formatname"));
//				exCat.setTerm(rsf.getString("exerciseid"));
//				exCat.setLabel(rsf.getString("exercisename"));
//				feed.addCategory(platCat);
//				feed.addCategory(formatCat);
//				feed.addCategory(exCat);
//				String colVal = rsf.getString("color");
//				Category platformColor = factory.newCategory();
//				platformColor.setScheme("cats/platformColor");
//				platformColor.setTerm(colVal);
//				feed.addCategory(platformColor);
//
//				// now loop through the entries in this dataset
//				// get the list of datasets
//				rse = st2.executeQuery("SELECT * from dataitems where datasetid = "
//						+ thisId + " ;");
//
//				// loop through dataitems
//				while (rse.next())
//				{
//					// first the short entry (for insertion into the feed)
//					Entry thisE = feed.addEntry();
//					thisE.setId(rse.getString("itemid"));
//					Date eDate = rse.getTimestamp("dtg");
//					thisE.setUpdated(eDate);
//					thisE.setTitle("Observation:" + thisE.getId());
//
//					if (longVersion)
//					{
//						// do the detail links
//						thisE.addLink("/detail/" + rse.getString("itemid") + "/"
//								+ rse.getString(1) + ".json", "self", "application/atom+json",
//								null, null, 0);
//						thisE.addLink("/detail/" + rse.getString("itemid") + "/"
//								+ rse.getString(1) + ".xml", "self", "application/atom+xml",
//								null, null, 0);
//						
//						// check we have content
//						String theContent = rse.getString("content");
//						if (theContent != null)
//							thisE.setContent(rse.getString("content"), rse
//									.getString("contenttype"));
//						// see if we have a summary
//						String theSumm = rse.getString("summary");
//						if (theSumm != null)
//							thisE.setSummary(theSumm);
//					}
//					
//					// see if we have a position
//					Object thePos = rse.getObject("location");
//					if (thePos != null)
//					{
//						PGgeometry obj = (PGgeometry) thePos;
//						Geometry geo = obj.getGeometry();
//						org.postgis.Point pt = (Point) geo;
//						org.apache.abdera.ext.geo.Position pos = new org.apache.abdera.ext.geo.Point(
//								pt.y, pt.x);
//						GeoHelper.addPosition(thisE, pos);
//					}
//
//				}
//
//				rse.close();
//
//				// and output the file
//				String fName;
//				if(longVersion)
//					fName = "";
//				else
//					fName = "_short";
//					
//				feed.writeTo("prettyxml", new FileOutputStream(DATA_ROOT + "detail\\"
//						+ thisId + fName + ".xml"));
//				feed.writeTo("json", new FileOutputStream(DATA_ROOT + "detail\\"
//						+ thisId + fName + ".json"));
//			}
//			rsf.close();
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
	}

	private static void exportCore(String thisDatasetQuery, String thisDatasetName)
	{
//		Abdera abdera = new Abdera();
//		Factory factory = abdera.getFactory();
//		Feed feed = abdera.newFeed();
//		ResultSet rs;
//		Statement st;
//
//		try
//		{
//			st = _conn.createStatement();
//
//			// create feed
//			feed.setId("Core");
//			feed.addAuthor("Ian Mayo");
//			feed.setUpdated(new Date());
//			feed.setTitle("List of all datasets");
//
//			// get the list of datasets
//			rs = st.executeQuery(thisDatasetQuery);
//
//			String wmsStr = GEOSERVER_ROOT + "service=WMS&" + "srs=EPSG:4326&"
//					+ "format=image/png&" + "version=1.1.1&";
//
//			// loop through datasets
//			while (rs.next())
//			{
//				// create this entry
//				Entry ent = feed.addEntry();
//				ent.setId(rs.getString("datasetid"));
//				ent.setTitle("d" + rs.getString("datasetid"));
//				ent.setPublished(rs.getTimestamp("starttime"));
//				ent.setUpdated(rs.getTimestamp("endtime"));
//				ent.setSummary(rs.getString("datasetname"));
//				ent.addLink("detail/" + rs.getString("datasetid") + ".xml",
//						"alternate", "application/atom+xml", null, null, 0);
//				ent.addLink("detail/" + rs.getString("datasetid") + ".json",
//						"alternate", "application/atom+json", null, null, 0);
//
//				// first the 'big' categories
//				Category platCat = factory.newCategory();
//				platCat.setScheme("cats/platforms.xml");
//				Category formatCat = factory.newCategory();
//				formatCat.setScheme("cats/formats.xml");
//				Category exCat = factory.newCategory();
//				exCat.setScheme("cats/exercises.xml");
//				platCat.setTerm(rs.getString("platformid"));
//				platCat.setLabel(rs.getString("platformname"));
//				formatCat.setTerm(rs.getString("formatid"));
//				formatCat.setLabel(rs.getString("formatname"));
//				exCat.setTerm(rs.getString("exerciseid"));
//				exCat.setLabel(rs.getString("exercisename"));
//				ent.addCategory(platCat);
//				ent.addCategory(formatCat);
//				ent.addCategory(exCat);
//
//				// now the presentational categories
//				String colVal = rs.getString("color");
//				Category platformColor = factory.newCategory();
//				platformColor.setScheme("cats/platformColor");
//				platformColor.setTerm(colVal);
//				ent.addCategory(platformColor);
//				Boolean hasSummary = rs.getBoolean("hasSummary");
//				Category withSummary = factory.newCategory();
//				withSummary.setScheme("cats/hasSummary");
//				withSummary.setTerm(hasSummary.toString());
//				ent.addCategory(withSummary);
//				Boolean hasPos = rs.getBoolean("hasLocation");
//				Category withPos = factory.newCategory();
//				withPos.setScheme("cats/hasPosition");
//				withPos.setTerm(new Boolean(hasPos).toString());
//				ent.addCategory(withPos);
//				if (hasPos)
//				{
//					ent.addLink(wmsStr + "CQL=datasetid=" + rs.getString("datasetid")
//							+ "", "alternate", "application/wms", null, null, 0);
//				}
//			}
//			rs.close();
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//
//		// ok, now output it.
//		try
//		{
//			File tgtDir = new File(DATA_ROOT + "detail");
//			tgtDir.mkdir();
//			feed.writeTo("prettyxml", new FileOutputStream(DATA_ROOT
//					+ thisDatasetName + ".xml"));
//			feed.writeTo("json", new FileOutputStream(DATA_ROOT + thisDatasetName
//					+ ".json"));
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}

	}

	private static void connectToDatabase()
	{
		try
		{
			String url = DATABASE_ROOT;
			_conn = DriverManager.getConnection(url, "dev", DATABASE_PASSWORD);

			// also tell the connection about our new custom data types
			((org.postgresql.PGConnection) _conn).addDataType("geometry",
					org.postgis.PGgeometry.class);
			((org.postgresql.PGConnection) _conn).addDataType("box3d",
					org.postgis.PGbox3d.class);
		}
		catch (SQLException e)
		{
			System.err.println("failed to create connection");
			e.printStackTrace();
		}

	}

}
