package Debrief.ReaderWriter.GeoPDF;

import java.lang.reflect.Field;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import junit.framework.TestCase;

public class GeoPDFCompiler {
	public static void compile(final GeoPDF geoPDF) {

	}

	public static class GeoPDFCompilerTest extends TestCase {

		public void testCompile1() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
			//System.setProperty("java.library.path", "C:\\Users\\saulh\\eclipse-workspace\\debrief\\org.mwc.debrief.legacy\\native");
			//Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			//fieldSysPath.setAccessible( true );
			//fieldSysPath.set( null, null );
			//System.load("C:\\Users\\saulh\\eclipse-workspace\\debrief\\org.mwc.debrief.legacy\\native\\gdal\\java\\gdalalljni.dll");
			gdal.GetDriverByName("PDF").Create("/home/saul/Documents/tmpcomposition.pdf", 0, 0, 0,
					gdalconst.GDT_Unknown, new String[] {"COMPOSITION_FILE=" + "/home/saul/Documents/tmp/composition.xml"});
		}
	}
}
