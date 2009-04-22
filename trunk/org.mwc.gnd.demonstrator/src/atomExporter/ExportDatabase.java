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
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.writer.WriterOptions;

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

		exportCore();

		 exportDetail();
	}

	private static void exportDetail()
	{
		// get the list of dataset

		// loop through datasets

		// create this feed

		// loop through dataitems

		// create this entry
	}

	private static void exportCore()
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
			rs = st.executeQuery("SELECT * from datasetsview;");

			
			// loop through datasets
			while (rs.next())
			{
				// create this entry
				Entry ent = feed.addEntry();
				ent.setId(rs.getString(1));
				ent.setTitle("d"+rs.getString(1));
				ent.setUpdated(rs.getString(3));
				ent.setSummary(rs.getString(2));
				ent.addLink("/detail/" + rs.getString(1), "alternate", "application/atom+xml", null,null, 0);
				ent.addLink("/wms/" + rs.getString(1), "alternate", "application/wms", null,null, 0);

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
			feed.writeTo("prettyxml", new FileOutputStream("c:\\tmp\\atomOutput\\detail.xml"));
			feed.writeTo("json", new FileOutputStream("c:\\tmp\\atomOutput\\detail.json"));
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
