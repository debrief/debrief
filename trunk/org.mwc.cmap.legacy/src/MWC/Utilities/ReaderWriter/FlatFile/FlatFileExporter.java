package MWC.Utilities.ReaderWriter.FlatFile;

import java.io.File;


public class FlatFileExporter
{
	public String exportThis()
	{
		String res = "na";
		return res;
	}
	
	
	static public final class testMe extends junit.framework.TestCase
	{

		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}
		
		
		private File getDataFile()
		{
			File res = new File("src/MWC/Utilities/ReaderWriter/FlatFile/fakedata.txt");
			return res;
		}
		
		private String getTestData()
		{
			String res = "";
			return res;
		}
		
		public void testExport()
		{
			FlatFileExporter fa = new FlatFileExporter();
			String res = fa.exportThis();
			
			assertEquals("correct string", "na", res);
			
			File iFile = getDataFile();
			assertTrue("file exists", iFile.exists());
			
			assertEquals("has data", 2367, iFile.length());
		}
	}

	
}
