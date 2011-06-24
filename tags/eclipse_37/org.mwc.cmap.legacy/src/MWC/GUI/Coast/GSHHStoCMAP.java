package MWC.GUI.Coast;

import java.io.*;
/**
 * GSHHStoCMAP
 * A crude utility that converts an GSHHS data base into a Debrief CMAP-readable .dat file.
 * TODO: Add a switch to set a filtering level (to exclude lakes, for instance).
 *
 * The GSHHS files are:
 *
 * gshhs       Global Self-consistent Hierarchical High-resolution Shorelines (including lakes)
 * wdb_borders WDB political borders
 * wdb_rivers  WDB rivers
 * _f.b   Full resolution data
 * _h.b   High resolution data (half of full along each axis)
 * _i.b   Intermediate resolution data (half of high along each axis)
 * _l.b   Low resolution data (half of intermediate along each axis)
 * _c.b   Crude resolution data (half of low along each axis)
 *
 * The GSHHS file structure (since version 4) is a series of polygons, each polygon
 * consisting of a header followed by a series of points.  The header structure is:
 *
 * int id;      Unique polygon id number, starting at 0
 * int n;       Number of points in this polygon
 * int flag;    level + (version << 8) + (greenwich << 16) + (source << 24)
 *              level:     1 land, 2 lake, 3 island_in_lake, 4 pond_in_island_in_lake
 *              version:   Set to 6 for GSHHS version 1.6 (released in March 2008)
 *              greenwich: 1 if "Greenwich is crossed" (meaning the polygon straddles Greenwich)
 *              source:    0 = CIA WDBII, 1 = WVS
 * int west,    Western bound, in micro-degrees
 *     east,    Eastern bound
 *     south,   Southern bound
 *     north;   Northern bound
 * int area;    Area of polygon in kiloares (0.1 km^2)
 *
 * The point structure is:
 *
 * int lon;     Longitude east, in micro-degrees (0..360)
 * int lat;     Latitude north (-90..+90)
 *
 * All the ints are four-byte Java integers, MSB.
 *
 * The polygons are sorted by polygon length (not area), and the w/e/s/n values for each
 * polygon are those based on the full resolution (they are copied to the other resolutions).
 * The decimated polygons will in general have a different w/e/s/n set which you can find
 * if you calculate the actual w/e/s/n values.
 */
public class GSHHStoCMAP {
   static String           CRLF = new String(new byte[] {13, 10});
   static int              count_polygons = 0;
// static int              count_skipped_polygons = 0;
   static int              count_points = 0;   // Full data set is a "mere" 10,340,934 points
   static int              blinkcount = 0;
   static int              nbytes, npoints;
// static int              flag;
   static int              west, east;
   static boolean          isStraddling180; // Whether the meridian is straddled
   static boolean          inhibit;
   static double           lon, lat;
   static double           lon_previous, lat_previous;
   static byte[]           gshhs_header = new byte[4*8];
   static byte[]           points;
   static FileInputStream  fisGSHHS;
   static FileWriter       fwCMAP, fwOutputLog;

   public static void main(String[] args) {
      try {
         System.out.println("GSHHStoCMAP");
         // Print usage guide if need be
         if ((args.length < 2) || (args.length > 3)) {
            System.out.println("+++ GSHHStoCMAP - Usage is 'GSHHStoCMAP Path\\gshhs_f.b Path2\\gshhsWorld.dat [Path3\\Output.log]'");
            System.out.println("+++ GSHHStoCMAP - default output log is 'GSHHStoCMAP.log'");
            System.exit(0);
         } //if

         // Open input
         fisGSHHS = new FileInputStream(args[0]);
         try {
            // Open output
            fwCMAP  = new FileWriter(args[1], false);
            try {
               // Open log
               if (args.length == 3) {
                  fwOutputLog = new FileWriter(args[2], false); //do not append
               } else {
                  fwOutputLog = new FileWriter("GSHHStoCMAP.log", false); //do not append
               } //if

               // Start the work
               try {
                  fwOutputLog.write("GSHHStoCMAP Output Log of:" + CRLF);
                  fwOutputLog.write("'" + args[0] + "'" + CRLF);
                  fwOutputLog.write(" to" + CRLF);
                  fwOutputLog.write("'" + args[1] + "'" + CRLF);
                  // Each input polygon starts with a header
                  while (32 == fisGSHHS.read(gshhs_header)) {
                     count_polygons++;
                     npoints = byteArrayToInt(gshhs_header, 4); //Offset 4 to reach the "n" field
                     nbytes = 8*npoints; //Each point is two four-byte integers
                     System.out.println("about to request:" + nbytes);
                     points = new byte[nbytes];
//                   flag = byteArrayToInt(gshhs_header, 8); //Offset 8 to reach the "flag" field
                     west = byteArrayToInt(gshhs_header, 12); //Offset 12 to reach the "west" field
                     east = byteArrayToInt(gshhs_header, 16); //Offset 16 to reach the "east" field
                     isStraddling180 = (west < 180000000) && (east > 180000000); // in micro-degrees

                     // Read the input polygon's points
                     
                     int tmpNumPoints = fisGSHHS.read(points);
										if (nbytes != tmpNumPoints)
                     {
                        throw new IOException("Could not read the required bytes. Expected:" + nbytes + " was:" + tmpNumPoints);
                     }

                     // Skip anything but sea shore (level 2 would filter islands in lakes and ponds in islands in lakes, etc.)
//                   if ((flag & 0x000000FF) > 1) {
//                      count_skipped_polygons++;
                        // Update System.out progress
//                      blinkcount += npoints;
//                      while (blinkcount >= 10000) {
//                         blinkcount -= 10000;
//                         System.out.print(".");
//                      } //while
//                      continue;
//                   } //if

                     count_points += npoints;
                     // Each output polygon starts with a break mark
                     write_break(fwCMAP);
                     // Treat the points
                     for (int pt = 0; pt < npoints; pt++) {
                        // Update System.out progress
                        blinkcount += 1;
                        if (blinkcount == 10000) {
                           blinkcount = 0;
                           System.out.print(".");
                        } //if

                        // To resolve the 180 meridian crossing, keep track of previous point
                        if (isStraddling180 && (pt > 0)) {
                           lon_previous = lon;
                           lat_previous = lat;
                        } //if

                        // Read lon and lat
                        lon = byteArrayToInt(points, 8*pt)/1E6;
                        lat = byteArrayToInt(points, 4 + 8*pt)/1E6;
                        // Snip if need be
                        inhibit = false;
                        if (isStraddling180 && (pt > 0))
                           inhibit = snip(fwCMAP, lon_previous, lat_previous, lon, lat, (pt == (npoints-1)));
                        if (!inhibit) write_lonlat(fwCMAP, lon, lat);
                     } //for
                  } //while
                  // All done
                  fwOutputLog.write("Counts are:" + CRLF);
                  fwOutputLog.write(Integer.toString(count_polygons) + " polygons" + CRLF);
                  fwOutputLog.write(Integer.toString(count_points)   + " points"   + CRLF);
//                fwOutputLog.write(Integer.toString(count_skipped_polygons) + " skipped polygons" + CRLF);
               } finally {
                  fwOutputLog.close();
                  System.out.println("");
                  System.out.println("+++ GSHHStoCMAP done +++");
               } //try fwOutputLog
            } finally {
               fwCMAP.close();
            } //try fwCMAP
         } finally {
            fisGSHHS.close();
         } //try fisGSHHS
      } catch (IOException ioe) {
         System.err.println(ioe);
      } //try
      System.exit(0);
   } //main

   /**
    * Writes a segment header/break.
    * @param fwCMAP The FileWriter
    */
   public static void write_break(FileWriter fwCMAP) throws IOException {
      fwCMAP.write("# -b" + CRLF);
   } //write_break

   /**
    * Writes the current point, converting the longitude to the -180..+180 range.
    * @param fwCMAP The FileWriter
    * @param lon longitude (0..360)
    * @param lat latitude
    */
   public static void write_lonlat(FileWriter fwCMAP, double lon, double lat) throws IOException {
      fwCMAP.write(Double.toString((lon > 180.0) ? (lon - 360.0) : lon) + " " + Double.toString(lat) + CRLF);
   } //write_lonlat

   /**
    * Snips the current segment over the 180 degree meridian.
    * The "previous" pair has already been written to fwCMAP.
    * The "current" pair will be written to fwCMAP after snip concludes.
    * All longitudes are in the 0..360 eastern span.
    * @param fwCMAP       the FileWriter
    * @param lon_previous previous longitude
    * @param lat_previous previous latitude
    * @param lon          upcoming longitude
    * @param lat          upcoming latitude
    * @param isLast       whether this is the last point in the polygon
    * @return             whether to inhibit the next point
    */
   public static boolean snip(FileWriter fwCMAP, 
                              double lon_previous, 
                              double lat_previous, 
                              double lon, 
                              double lat, 
                              boolean isLast) throws IOException {
      double lon_interp = 180.0;
      double lat_interp;

      // Is an intervention required?
      if ((lon_previous < 180.0) && (lon <= 180.0)) return false;
      if ((lon_previous < 180.0) && (lon > 180.0)) {
         if ((lon_previous < 170.0) || (lon > 190.0)) return false; // False alarm due to Greenwich (jump between ~0 and ~360)
         lat_interp = lat_previous + (lat - lat_previous)*(lon_interp - lon_previous)/(lon - lon_previous);
         write_lonlat(fwCMAP, lon_interp, lat_interp);
         write_break(fwCMAP);
         write_lonlat(fwCMAP, lon_interp - 360.0, lat_interp);
      } //if
         
      if ((lon_previous == 180.0) && (lon <= 180.0)) return false;
      if ((lon_previous == 180.0) && (lon > 180.0)) {
         // Force -180.0 restart
         write_break(fwCMAP);
         write_lonlat(fwCMAP, lon_previous - 360.0, lat_previous);
      } //if

      if ((lon_previous > 180.0) && (lon > 180.0)) return false;
      if ((lon_previous > 180.0) && (lon == 180.0)) {
         write_lonlat(fwCMAP, lon - 360.0, lat);
         if (!isLast) {
            write_break(fwCMAP);
         } else {
            return true;
         } //if
      } //if
      if ((lon_previous > 180.0) && (lon < 180.0)) {
         if ((lon_previous > 190.0) || (lon < 170.0)) return false; // False alarm due to Greenwich (jump between ~0 and ~360)
         lat_interp = lat_previous + (lat - lat_previous)*(lon_interp - lon_previous)/(lon - lon_previous);
         write_lonlat(fwCMAP, lon_interp - 360.0, lat_interp);
         write_break(fwCMAP);
         write_lonlat(fwCMAP, lon_interp, lat_interp);
      } //if
      return false;
   } //snip

   /**
    * Convert (the first four bytes of) a byte array to an int.
    * The conversion assumes MSB byte order.
    *
    * @param b The byte array
    * @return The integer
    */
   public static int byteArrayToInt(byte[] b) {
      return byteArrayToInt(b, 0);
   } //byteArrayToInt

   /**
    * Convert a byte array to an int starting from the given offset.
    * The conversion assumes MSB byte order.
    *
    * @param b The byte array
    * @param offset The array offset
    * @return The integer
    */
   public static int byteArrayToInt(byte[] b,
                                    int offset) {
      int value = 0;
      for (int i = 0; i < 4; i++) {
         int shift = ((4 - 1) - i)*8;
         value += (b[i + offset] & 0x000000FF) << shift;
      } //for
      return value;
   } //byteArrayToInt
} //GSHHStoCMAP
