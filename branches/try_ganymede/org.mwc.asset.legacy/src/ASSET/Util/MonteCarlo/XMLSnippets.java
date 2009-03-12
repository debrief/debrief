package ASSET.Util.MonteCarlo;

import ASSET.Util.SupportTesting;
import org.apache.xerces.dom.DeferredTextImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 24-Sep-2003
 * Time: 13:51:49
 * To change this template use Options | File Templates.
 */
public final class XMLSnippets
{
  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  final Vector<Node> _mySnippets;

  private static final String XML_SNIPPET = "XMLSnippet";

  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public XMLSnippets(final Element theContainer)
  {
    this();

    // retrieve the list of snippets
    final NodeList theSnippets = theContainer.getElementsByTagName(XML_SNIPPET);

    // let's step through them
    final int len = theSnippets.getLength();

    for (int i = 0; i < len; i++)
    {
      // ok, get the next snippet
      final Element thisNode = (Element) theSnippets.item(i);

      // and extract it's contents
      final DeferredTextImpl snippet = (DeferredTextImpl) thisNode.getFirstChild();

      // and now for the sibling (which contains our data)
      final Node nextS = snippet.getNextSibling();

      // now add them to our choices
      _mySnippets.add(nextS);
    }

  }

  XMLSnippets()
  {
    _mySnippets = new Vector<Node>(0, 1);
  }


  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////

  /**
   * get a new (random) permutation
   */
  public final Element getInstance()
  {
    final int len = _mySnippets.size();
    final int rndIndex = (int) (ASSET.Util.RandomGenerator.nextRandom() * len);

    final Element res = (Element) _mySnippets.get(rndIndex);

    return res;
  }

  /**
   * how many snippets are there?
   */
  public final int size()
  {
    return _mySnippets.size();
  }

  /**
   * retrieve a specific snippet
   */
  public final Node get(final int val)
  {
    return (Node) _mySnippets.get(val);
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static final class XMLSnippetTest extends SupportTesting
  {

    public XMLSnippetTest(final String val)
    {
      super(val);
    }

    public final void testRandom()
    {
      final XMLSnippets snips = new XMLSnippets();

      Document theD = null;
      try
      {
        final DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        theD = builder.newDocument();
      }
      catch (FactoryConfigurationError factoryConfigurationError)
      {
        factoryConfigurationError.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
      catch (ParserConfigurationException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }

      // check it worked
      assertNotNull("created document", theD);

      for (int i = 0; i < 4; i++)
      {
        final Element thisE = theD.createElement("a" + (i + 1));
        snips._mySnippets.add(thisE);
      }

      int num1 = 0;
      int num2 = 0;
      int num3 = 0;
      int num4 = 0;

      final int len = 10000;
      for (int i = 0; i < len; i++)
      {
        final Element newE = snips.getInstance();
        final String nm = newE.getNodeName();
        final int val = Integer.parseInt(new String("" + nm.charAt(1)));

        if (val == 1)
        {
          num1++;
        }
        if (val == 2)
        {
          num2++;
        }
        if (val == 3)
        {
          num3++;
        }
        if (val == 4)
        {
          num4++;
        }

      }

      assertEquals("correct 1s", num1, 2500, 200);
      assertEquals("correct 2s", num2, 2500, 200);
      assertEquals("correct 3s", num3, 2500, 200);
      assertEquals("correct 4s", num4, 2500, 200);

    }
  }


}
