package Debrief.ReaderWriter.powerPoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class FindMap
{

  /**
   * Now we need to check for every slide if it contains a rectangle named map
   * 
   * @param slides_path
   *          slides` path
   * @return shape of the rectangles (Map details)
   * @throws DebriefException
   *           In case we have a corrupted XML
   */
  private static HashMap<String, String> checkForMap(
      final ArrayList<String> slides_path) throws DebriefException
  {
    HashMap<String, String> mapDetails = new HashMap<>();

    for (final String slidePath : slides_path)
    {
      int flag = 0;

      try
      {
        final byte[] encoded = Files.readAllBytes(Paths.get(slidePath));
        final Document soup = Jsoup.parse(new String(encoded), "", Parser
            .xmlParser());

        final Elements shapes = soup.select("p|sp");
        final String cnvpr = "p|cNvPr";

        for (final Element shape : shapes)
        {
          final HashMap<String, String> shapeDetails = new HashMap<>();
          shapeDetails.put("name", shape.select(cnvpr).attr("name"));
          if ("map".equals(shapeDetails.get("name")))
          {
            shapeDetails.put("x", shape.select("a|off").get(0).attr("x"));
            shapeDetails.put("y", shape.select("a|off").get(0).attr("y"));
            shapeDetails.put("cx", shape.select("a|ext").get(0).attr("cx"));
            shapeDetails.put("cy", shape.select("a|ext").get(0).attr("cy"));
            mapDetails = shapeDetails;
            System.out.println("mapDetails - " + Arrays.toString(mapDetails
                .entrySet().toArray()));
            flag = 1;
            break;
          }
        }

        if (flag == 1)
        {
          break;
        }
      }
      catch (final IOException e)
      {
        throw new DebriefException("Corrupted xml file " + slidePath);
      }
    }

    return mapDetails;
  }

  public static HashMap<String, String> getMapDetails(final String unpack_path)
      throws DebriefException
  {
    final String slides_base_path = unpack_path + "/ppt/slides";
    final ArrayList<String> slides_path = getSlides(slides_base_path);
    final HashMap<String, String> mapDetail = checkForMap(slides_path);
    return mapDetail;
  }

  /**
   * Slides_path contains path to all the slide.xml files
   * 
   * @param slides_base_path
   *          slide's path
   * @return list of the slides` paths
   */
  public static ArrayList<String> getSlides(final String slides_base_path)
  {
    final ArrayList<String> slides_path = new ArrayList<>();
    for (final File slide : new File(slides_base_path).listFiles())
    {
      if (slide.getName().endsWith(".xml"))
      {
        slides_path.add(slides_base_path + "/" + slide.getName());
      }
    }
    return slides_path;
  }
}
