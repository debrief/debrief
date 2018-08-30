package Debrief.ReaderWriter.powerPoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import Debrief.ReaderWriter.powerPoint.model.TrackData;
import net.lingala.zip4j.exception.ZipException;

public class PlotGpx
{

  public static void main(final String[] args)
  {
    final PlotGpx plotGpx = new PlotGpx();
    final Options arguments = new Options();
    arguments.addOption("donor", true, "Path to donor pptx file");
    arguments.addOption("tracks_path", true, "Path to gpx tracks file");
    arguments.addOption("check_donor", true,
        "Path to the donor file to be checked");
    arguments.addOption("retrieve_map_dimensions", true, "Map Dimensions");

    try
    {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine commandLine = parser.parse(arguments, args);

      if (commandLine.hasOption("check_donor"))
      {
        final PlotTracks plotter = new PlotTracks();
        String answer = plotter.validateDonorFile(commandLine.getOptionValue(
            "check_donor"));
        System.out.println(answer);
        System.exit(0);
      }
      else if (commandLine.hasOption("retrieve_map_dimensions"))
      {
        final PlotTracks plotter = new PlotTracks();
        HashMap<String, String> answer = plotter.retrieveMapProperties(
            commandLine.getOptionValue("retrieve_map_dimensions"));
        System.out.println(Arrays.toString(answer.entrySet().toArray()));
        System.exit(0);
      }
      else if (!commandLine.hasOption("donor") || !commandLine.hasOption(
          "tracks_path"))
      {
        plotGpx.printHelp(arguments);
      }

      final String donor = commandLine.getOptionValue("donor");
      final String tracks_path = commandLine.getOptionValue("tracks_path");

      final byte[] encoded = Files.readAllBytes(Paths.get(tracks_path));
      final String trackXml = new String(encoded);

      final TrackData trackData = TrackParser.getInstance().parse(trackXml);

      final PlotTracks plotter = new PlotTracks();

      plotter.export(trackData, donor, "outputs.pptx");
    }
    catch (final ParseException e)
    {
      plotGpx.printHelp(arguments);
    }
    catch (final ZipException e)
    {
      e.printStackTrace();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
    catch (DebriefException e)
    {
      e.printStackTrace();
    }
  }

  private void printHelp(final Options arguments)
  {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("Script to plot gpx data on pptx", arguments);
    System.exit(1);
  }
}
