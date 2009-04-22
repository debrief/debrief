package atomExporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.geo.GeoHelper;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

public class ExportDatabase
{

	private static Connection _conn;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
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
		exportCore("SELECT * from datasetsview where datasetid <= 10 order by datasetid asc;", "datasets_filter1");
		exportCore("SELECT * from datasetsview where datasetid > 10 order by datasetid asc;", "datasets_filter2");

		exportDetail();
		
		exportCategory("Platforms","PlatformId", "PlatformName", "Platforms");
		exportCategory("Exercises","ExerciseId", "ExerciseName", "Exercises");
		exportCategory("Formats","FormatId", "FormatName", "Formats");
		
	}

	private static void exportCategory(String table, String idField,
			String nameField, String outputFile)
	{
		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		ResultSet rsf;
		Statement st;
		Categories theseCats = factory.newCategories();
		theseCats.setFixed(true);
		try
		{
			st = _conn.createStatement();

			// get the list of datasets
			rsf = st.executeQuery("SELECT * from " + table + " ORDER BY " + idField + " ASC ;");
			
			// loop through them
			while(rsf.next())
			{
				Category thisC = factory.newCategory();
				thisC.setTerm(rsf.getString(1));
				thisC.setLabel(rsf.getString(2));
				thisC.setScheme(table);
				theseCats.addCategory(thisC);				
			}
			// and output the file
			File oFile = new File("c:\\tmp\\atomOutput\\cats" );
			oFile.mkdir();
			theseCats.writeTo("prettyxml", new FileOutputStream(
					"c:\\tmp\\atomOutput\\cats\\" + table + ".xml"));
			theseCats.writeTo("json", new FileOutputStream(
					"c:\\tmp\\atomOutput\\cats\\" + table + ".json"));
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void exportDetail()
	{
		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		ResultSet rsf, rse;
		Statement st, st2;
		try
		{
			st = _conn.createStatement();
			st2 = _conn.createStatement();

			// get the list of datasets
			rsf = st.executeQuery("SELECT * from datasetsview;");

			// loop through datasets
			while (rsf.next())
			{
				String thisId = rsf.getString(1);

				// create this feed
				Feed feed = abdera.newFeed();
				feed.setId(thisId);
				feed.addAuthor("Ian Mayo");
				feed.setUpdated(new Date());
				feed.setTitle("List of all datasets");

				Category platCat = factory.newCategory();
				platCat.setScheme("platforms");
				Category formatCat = factory.newCategory();
				formatCat.setScheme("formats");
				Category exCat = factory.newCategory();
				exCat.setScheme("exercises");
				platCat.setTerm(rsf.getString(11));
				platCat.setLabel(rsf.getString(12));
				formatCat.setTerm(rsf.getString(6));
				formatCat.setLabel(rsf.getString(7));
				exCat.setTerm(rsf.getString(4));
				exCat.setLabel(rsf.getString(5));
				feed.addCategory(platCat);
				feed.addCategory(formatCat);
				feed.addCategory(exCat);

				// now loop through the entries in this dataset
				// get the list of datasets
				rse = st2.executeQuery("SELECT * from dataitems where datasetid = "
						+ thisId + " limit 100;");

				// loop through dataitems
				while (rse.next())
				{
					// first the short entry (for insertion into the feed)
					Entry thisE = feed.addEntry();
					thisE.setId(rse.getString(1));
					thisE.setUpdated(rse.getString(3));
					thisE.addLink("/detail/" + rse.getString(1) + "/" + rse.getString(1) + ".json", "self",
							"application/atom+json", null, null, 0);
					thisE.addLink("/detail/" + rse.getString(1) + "/" + rse.getString(1) +  ".xml", "self",
							"application/atom+xml", null, null, 0);
					// check we have content
					String theContent = rse.getString(6);
					if(theContent != null)
					 thisE.setContent(rse.getString(6), rse.getString(5));
					// see if we have a summary
					String theSumm = rse.getString(4);
					if (theSumm != null)
						thisE.setSummary(theSumm);
					// see if we have a position
					Object thePos = rse.getObject(7);
					if(thePos != null)
					{
						PGgeometry obj = (PGgeometry) thePos;
						Geometry geo = obj.getGeometry();
						org.postgis.Point pt = (Point) geo;
						org.apache.abdera.ext.geo.Position pos = new org.apache.abdera.ext.geo.Point(pt.y, pt.x);
						GeoHelper.addPosition(thisE, pos);
					}
					
				}

				rse.close();

				// and output the file
				feed.writeTo("prettyxml", new FileOutputStream(
						"c:\\tmp\\atomOutput\\detail\\" + thisId + ".xml"));
				feed.writeTo("json", new FileOutputStream(
						"c:\\tmp\\atomOutput\\detail\\" + thisId + ".json"));
			}
			rsf.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void exportCore(String thisDatasetQuery, String thisDatasetName)
	{
		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		Feed feed = abdera.newFeed();
		ResultSet rs;
		Statement st;
		try
		{
			st = _conn.createStatement();

			// create feed
			feed.setId("datasets");
			feed.addAuthor("Ian Mayo");
			feed.setUpdated(new Date());
			feed.setTitle("List of all datasets");

			// get the list of datasets
			rs = st.executeQuery(thisDatasetQuery);

			// loop through datasets
			while (rs.next())
			{
				// create this entry
				Entry ent = feed.addEntry();
				ent.setId(rs.getString(1));
				ent.setTitle("d" + rs.getString(1));
				ent.setUpdated(rs.getString(3));
				ent.setSummary(rs.getString(2));
				ent.addLink("detail/" + rs.getString(1) + ".xml", "alternate",
						"application/atom+xml", null, null, 0);
				ent.addLink("detail/" + rs.getString(1) + ".json", "alternate",
						"application/atom+json", null, null, 0);
				ent.addLink("/wms/" + rs.getString(1), "alternate", "application/wms",
						null, null, 0);

				Category platCat = factory.newCategory();
				platCat.setScheme("platforms");
				Category formatCat = factory.newCategory();
				formatCat.setScheme("formats");
				Category exCat = factory.newCategory();
				exCat.setScheme("exercises");
				platCat.setTerm(rs.getString(11));
				platCat.setLabel(rs.getString(12));
				formatCat.setTerm(rs.getString(6));
				formatCat.setLabel(rs.getString(7));
				exCat.setTerm(rs.getString(4));
				exCat.setLabel(rs.getString(5));
				ent.addCategory(platCat);
				ent.addCategory(formatCat);
				ent.addCategory(exCat);
			}
			rs.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		// ok, now output it.
		try
		{
			File tgtDir = new File("c:\\tmp\\atomOutput\\detail");
			tgtDir.mkdir();
			feed.writeTo("prettyxml", new FileOutputStream("c:\\tmp\\atomOutput\\"
					+ thisDatasetName + ".xml"));
			feed.writeTo("json", new FileOutputStream("c:\\tmp\\atomOutput\\"
					+ thisDatasetName + ".json"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static void connectToDatabase()
	{
		try
		{
			String url = "jdbc:postgresql://localhost:5432/GND";
			_conn = DriverManager.getConnection(url, "postgres", "4pfonmr");

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
