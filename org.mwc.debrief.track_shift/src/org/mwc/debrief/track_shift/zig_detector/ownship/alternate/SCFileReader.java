package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SCFileReader - Steady course file reader. Collection of methods and tools 
 * for reading Debrief file and populating corresponding data structures.
 * @see rep replay file format: http://www.debrief.info/tutorial/index.html#reference.html#replay_track_format
 */
public class SCFileReader {

    public static final Charset ENCODING = StandardCharsets.UTF_8;
    
    //-------------------------------------------------------------------------
    /**
     * Reads Debrief file and populates corresponding data structure
     * @param  path a path to the (.rep) file which should be read and processed
     * @param num_lines 
     * @return ArrayList<Tote> arrayl_totes - array list of created Totes
     */
    public List<Tote> process(String path) {
        File file = new File(path);
        List<String> lines = readFile(file);
        ArrayList<Tote> arrayl_totes = splitLines(lines);
        
        return arrayl_totes;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates list of strings (lines in a file) from a given file
     * @param  file given file
     * @return List<String> lines - list of string lines in the file
     * @see rep replay file format: http://www.debrief.info/tutorial/index.html#reference.html#replay_track_format
     */
    public List<String> readFile(File file) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(file.toPath(), ENCODING);
        } 
        
        catch (IOException e) {
            System.out.println("Exception while reading file!?");
        }
        
        return lines;
    }

    //-------------------------------------------------------------------------
    /**
     * Create ArrayList of Tote objects from a list of strings (string lines in a Debrief replay format file)
     * @param  lines List of strings
     * @return ArrayList<Tote> arrayl_totes - array list of created Totes
    */
    public ArrayList<Tote> splitLines(List<String> lines) {
        ArrayList<Tote> arrayl_totes = new ArrayList<>();
        for (String line : lines) {
            String sline = line.trim();
            if (sline.isEmpty() || sline.startsWith(";;")) continue;
            arrayl_totes.add(createTote(sline));
        }
        return arrayl_totes;
    }
    
    static int global_index = 0;

    //-------------------------------------------------------------------------
    /**
     * Create Tote structure from a string line (a line in a Debrief replay format file)
     * @param  line string line in .rep file
     * @return Tote tote - the tote created from a line in the file
    */
    double previousHeading = 180.0; // suitable initial value
    public Tote createTote(String line) {
        Tote tote = new Tote();
        try {
            String[] parts = line.split("\\s+"); //split na white space
            if (parts.length >= 14) { // for this particular format, there are 16 substrings in a string line. But, we don't need the last two.
                tote.index = global_index++;
                tote.sdate = parts[0].trim();
                tote.stime = parts[1].trim();
                tote.dheading = Double.parseDouble(parts[12].trim());
                if(previousHeading - tote.dheading > 180.0)
                    tote.dheading += 360.0;
                else if(tote.dheading - previousHeading > 180.0)
                    tote.dheading -= 360.0;
                previousHeading = tote.dheading;
                tote.dspeed = Double.parseDouble(parts[13].trim());
                tote.dabsolute_time = toAbsoluteTime(tote.sdate, tote.stime);
            }
        }
        
        catch (NumberFormatException e) {
            System.out.println("Reading specified file caused " + e);
        }
        
        return tote;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns elapsed seconds from midnight 1970-01-01 
     * @param  sdate date in YYYYMMDD or YYMMDD format
     * @param stime time in HHMMSS.SSS or HMMSS.SSS format
     * @return double desconds - seconds from midnight 1970-01-01
     */
    public double toAbsoluteTime(String sdate, String stime) {
        if(sdate.length() == 6) {
            if(sdate.startsWith("0") || sdate.startsWith("1"))
                sdate = "20" + sdate;
            else
                sdate = "19" + sdate;
        }
        LocalDate date = LocalDate.parse(sdate, DateTimeFormatter.BASIC_ISO_DATE);
        long iepoch_days = date.toEpochDay();
        double dseconds = 24 * 3600 * iepoch_days;
        
        String shours, smins, ssecs;
        int pospoint = stime.indexOf('.');
        String sinttime = pospoint != -1 ? stime.substring(0, pospoint) : stime;
        if(sinttime.length() == 6) {
            shours = stime.substring(0, 2);
            smins = stime.substring(2, 4);
            ssecs = stime.substring(4);
        }
        else {
            shours = "0" + stime.substring(0, 1);
            smins = stime.substring(1, 3);
            ssecs = stime.substring(3);
        }
        String stime_iso = shours + ":" + smins + ":" + ssecs;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
        LocalTime time = LocalTime.parse(stime_iso, dateTimeFormatter);
        dseconds += (double)(time.toNanoOfDay()) * 1e-9;
        
        return dseconds;
    }
}
