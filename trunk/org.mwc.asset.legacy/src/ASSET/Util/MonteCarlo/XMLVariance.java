/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:22:15 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ASSET.Util.RandomGenerator;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public final class XMLVariance
{
	public static final String NAMESPACE_PREFIX = "as";

	/**
	 * the name of this variance
	 */
	private String _myName = null;

	/**
	 * the xpath id we use
	 */
	private String _myId = null;

	/**
	 * the attribute or node we are editing
	 */
	private XMLObject _myObject = null;

	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String ATTRIBUTE = "Attribute";
	private static final String UNITS = "Units";
	private static final String VALUE = "Value";

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public XMLVariance(final Element myElement)
	{
		try
		{
			// read in the data from this element
			_myName = myElement.getAttribute(NAME);

			// store the id
			_myId = myElement.getAttribute(ID);

			// do we have an attribute to edit
			final NodeList atts = myElement.getElementsByTagName(ATTRIBUTE);

			final int len = atts.getLength();
			if (len > 0)
			{
				// yup, it's an attribute

				final Element myNode = (Element) atts.item(0);
				// good, lets store this attribute
				_myObject = new XMLAttribute(myNode);
			}

			// did it work?
			if (_myObject == null)
			{
				// do we have an attribute to edit
				final NodeList nodes = myElement.getElementsByTagName("Node");

				final Element myNode = (Element) nodes.item(0);

				if (myNode != null)
				{
					// good, let's store this node
					_myObject = new XMLNode(myNode);
				}
			}

			// carry on trying if we've still not found ourselves
			// did it work?
			if (_myObject == null)
			{
				// do we have an attribute to edit
				final NodeList nodes = myElement.getElementsByTagName("LocationArea");

				final Element myNode = (Element) nodes.item(0);

				if (myNode != null)
				{
					// good, let's store this node
					_myObject = new LocationArea(myNode);
				}
			}
			// carry on trying if we've still not found ourselves
			// did it work?
			if (_myObject == null)
			{
				// do we have an attribute to edit
				final NodeList nodes = myElement.getElementsByTagName("LocationOffset");

				final Element myNode = (Element) nodes.item(0);

				if (myNode != null)
				{
					// good, let's store this node
					_myObject = new LocationOffset(myNode);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */
	public final XMLObject getObject()
	{
		return _myObject;
	}

	/**
	 * return the name of this object
	 */
	public final String getName()
	{
		return _myName;
	}

	/**
	 * get the id of this obkect
	 */
	public final String getId()
	{
		return _myId;
	}

	/**
	 * return the last value of this variance
	 */
	public final String getValue()
	{
		return _myObject.getCurValue();
	}

	private String insertPrefixesTo(final String theXPath)
	{

		String theRes = new String(theXPath);
		String[] items = theRes.split("/");
		for (int i = 0; i < items.length; i++)
		{
			String thisA = items[i];
			if (thisA.length() > 2)
			{
				if (Character.isLetter(thisA.charAt(0)))
				{
					// right, we don't just to a straight-forward replace, since our
					// phrase may appear more
					// than once in the expression, or within another string.
					// so, we prepend the search with a slash, and include that slash
					// with the replacement string
					theRes = theRes.replace("/" + thisA, "/" + NAMESPACE_PREFIX + ":"
							+ thisA);
				}
			}
		}

		return theRes;
	}

	/**
	 * modify the supplied document with our operation
	 */
	public final String permutate(final String parentXPath,
			final Document document) throws MatchingException,
			IllegalExpressionException
	{

		// did we get a parent?
		String theXPath;
		if (parentXPath != null)
		{
			// yes, we know our parent - append ourselves to it
			theXPath = parentXPath + "/" + _myId;
		}
		else
		{
			// nope, no parent - just get on with it
			theXPath = _myId;
		}

		// keep track of the values we use - to become a hashmap
		String resStr = "";

		List<?> results = null;
		try
		{
			String myPath = insertPrefixesTo(theXPath);
			DOMXPath expression = new org.jaxen.dom.DOMXPath(myPath);
			expression.addNamespace(NAMESPACE_PREFIX, "http://www.mwc.org/asset");
			results = expression.selectNodes(document);

		}
		catch (JaxenException e)
		{
			e.printStackTrace();
		}

		// did we find it?
		boolean foundHim = false;

		if (results != null)
		{
			if (results.size() > 0)
			{
				// yup, found hime. we don't need to throw the exception now
				foundHim = true;

				Iterator<?> iter = results.iterator();
				while (iter.hasNext())
				{
					Element ele = (Element) iter.next();
					resStr += _myObject.execute(ele, document);
				}
			}
		}
		// did we find him?
		if (!foundHim)
		{
			// nope, throw the exception
			throw new MatchingException(theXPath, null);
		}

		return resStr;
	}

	/**
	 * create a random instance of our variance
	 */
	public final void randomise()
	{
		_myObject.randomise();
	}

	public Object clone()
	{
		System.err.println("IMPLEMENT PROPER CLONE METHOD");
		// todo:IMPLEMENT PROPER CLONE METHOD
		return this.clone();
	}

	/**
	 * equals operator
	 */
	public final boolean equals(final XMLVariance other)
	{
		boolean res = true;

		if (!_myName.equals(other._myName))
			res = false;

		if (!_myObject.getCurValue().equals(other._myObject.getCurValue()))
			res = false;

		return res;
	}

	/**
	 * merge our data with the supplied one
	 */
	public final void merge(final XMLVariance other)
	{
		_myObject.merge(other._myObject);
	}

	/**
	 * output this variance as a string
	 */
	public final String toString()
	{
		String res = "";
		res += "Name:" + getName();
		res += " Val:" + getValue();
		return res;
	}

	/**
	 * utility method to read in a world area from within an XML element
	 * 
	 * @param myElement
	 *          the element to read the data from
	 * @return a world area containing the read-in data
	 */
	public static WorldArea readInAreaFromXML(final Element myElement)
	{
		WorldArea theArea = null;
		NodeList theShortLocation = myElement.getElementsByTagName("shortLocation");

		int numShortLocation = theShortLocation.getLength();

		for (int i = 0; i < numShortLocation; i++)
		{
			// first the top left
			Element topL = (Element) theShortLocation.item(i);

			// and extract this corner
			WorldLocation thisCorner = extractShortLocation(topL);

			if (theArea == null)
				theArea = new WorldArea(thisCorner, thisCorner);
			else
				theArea.extend(thisCorner);
		}

		// try for the long location
		NodeList theLongLocation = myElement.getElementsByTagName("longLocation");

		int numLongLocation = theLongLocation.getLength();

		for (int i = 0; i < numLongLocation; i++)
		{
			// first the top left
			Element topL = (Element) theLongLocation.item(i);

			// and extract this corner
			WorldLocation thisCorner = extractLongLocation(topL);

			if (theArea == null)
				theArea = new WorldArea(thisCorner, thisCorner);
			else
				theArea.extend(thisCorner);
		}

		// and the relative location
		theLongLocation = myElement.getElementsByTagName("relativeLocation");

		numLongLocation = theLongLocation.getLength();

		for (int i = 0; i < numLongLocation; i++)
		{
			// first the top left
			Element topL = (Element) theLongLocation.item(i);

			// and extract this corner
			WorldLocation thisCorner = extractRelativeLocation(topL);

			if (theArea == null)
				theArea = new WorldArea(thisCorner, thisCorner);
			else
				theArea.extend(thisCorner);
		}
		return theArea;
	}

	/**
	 * utility method to read in a world location from within an XML element
	 * 
	 * @param myElement
	 *          the element to read the data from
	 * @return a world location representing the read-in data
	 */
	public static WorldLocation readInLocationFromXML(Element myElement,
			String locationName)
	{
		WorldLocation theRes = null;
		NodeList theLocation = myElement.getElementsByTagName(locationName);

		// have we found our location?
		if (theLocation.getLength() > 0)
		{
			myElement = (Element) theLocation.item(0);

			NodeList theShortLocation = myElement
					.getElementsByTagName("shortLocation");

			int numShortLocation = theShortLocation.getLength();

			for (int i = 0; i < numShortLocation; i++)
			{
				// first the top left
				Element topL = (Element) theShortLocation.item(i);

				// and extract this corner
				theRes = extractShortLocation(topL);
			}

			if (theRes == null)
			{
				// try for the long location
				NodeList theLongLocation = myElement
						.getElementsByTagName("longLocation");

				int numLongLocation = theLongLocation.getLength();

				for (int i = 0; i < numLongLocation; i++)
				{
					// first the top left
					Element topL = (Element) theLongLocation.item(i);

					// and extract this corner
					theRes = extractLongLocation(topL);
				}
			}

			if (theRes == null)
			{
				// try for the long location
				NodeList theLongLocation = myElement
						.getElementsByTagName("relativeLocation");

				int numLongLocation = theLongLocation.getLength();

				for (int i = 0; i < numLongLocation; i++)
				{
					// first the top left
					Element topL = (Element) theLongLocation.item(i);

					// and extract this corner
					theRes = extractRelativeLocation(topL);
				}
			}
		}
		return theRes;
	}

	private static WorldLocation extractShortLocation(Element topL)
	{
		String llat = topL.getAttribute("Lat");
		String llong = topL.getAttribute("Long");

		WorldLocation thisCorner = new WorldLocation(Double.parseDouble(llat),
				Double.parseDouble(llong), 0);
		return thisCorner;
	}

	private static WorldLocation extractRelativeLocation(Element topL)
	{
		// first get the North bit
		NodeList thisDirection = topL.getElementsByTagName("North");
		WorldDistance north = null;
		for (int i = 0; i < thisDirection.getLength(); i++)
		{
			// first the top left
			Element North = (Element) thisDirection.item(i);
			String units = North.getAttribute(UNITS);
			int theUnits = WorldDistance.getUnitIndexFor(units);
			String val = North.getAttribute(VALUE);
			north = new WorldDistance(Double.parseDouble(val), theUnits);
		}

		thisDirection = topL.getElementsByTagName("East");
		WorldDistance east = null;
		for (int i = 0; i < thisDirection.getLength(); i++)
		{
			// first the top left
			Element North = (Element) thisDirection.item(i);
			String units = North.getAttribute(UNITS);
			int theUnits = WorldDistance.getUnitIndexFor(units);
			String val = North.getAttribute(VALUE);
			east = new WorldDistance(Double.parseDouble(val), theUnits);
		}

		WorldLocation res = new WorldLocation.LocalLocation(north, east, 0);

		return res;
	}

	private static WorldLocation extractLongLocation(Element topL)
	{
		int latDeg = Integer.parseInt(topL.getAttribute("LatDeg"));
		int longDeg = Integer.parseInt(topL.getAttribute("LongDeg"));
		int latMin = Integer.parseInt(topL.getAttribute("LatMin"));
		int longMin = Integer.parseInt(topL.getAttribute("LongMin"));

		double latSec = Double.parseDouble(topL.getAttribute("LatSec"));
		double longSec = Double.parseDouble(topL.getAttribute("LongSec"));

		char latHemi = topL.getAttribute("LatHem").charAt(0);
		char longHemi = topL.getAttribute("LongHem").charAt(0);

		WorldLocation thisCorner = new WorldLocation(latDeg, latMin, latSec,
				latHemi, longDeg, longMin, longSec, longHemi, 0);

		return thisCorner;
	}

	public static int readRandomNumberModel(Element element)
	{
		String modelTxt = element.getAttribute("RandomModel");
		int res;
		// ok, get it
		res = getModelFromString(modelTxt);

		return res;
	}

	/**
	 * examine the string, and find out which model it refers to
	 * 
	 * @param modelTxt
	 *          the string to examine
	 * @return the model described
	 */
	public static int getModelFromString(String modelTxt)
	{
		int res;
		if (modelTxt.equals(RandomGenerator.UNIFORM_STR))
		{
			res = RandomGenerator.UNIFORM;
		}
		else if (modelTxt.equals(RandomGenerator.NORMAL_CONSTRAINED_STR))
		{
			res = RandomGenerator.NORMAL_CONSTRAINED;
		}
		else
			res = RandomGenerator.NORMAL;
		return res;
	}

	/**
	 * generate a random location within the provided area
	 * 
	 * @param area
	 *          containing area
	 * @param numberModel
	 *          number model to use
	 * @return new location
	 */
	public static WorldLocation generateRandomLocationInArea(WorldArea area,
			int numberModel)
	{
		WorldLocation res = null;
		double latV = RandomGenerator.generateRandomNumber(area.getBottomRight()
				.getLat(), area.getTopLeft().getLat(), numberModel);
		double longV = RandomGenerator.generateRandomNumber(area.getTopLeft()
				.getLong(), area.getBottomRight().getLong(), numberModel);

		res = new WorldLocation(latV, longV, 0);
		return res;
	}

	// //////////////////////////////////////////////////////////
	// exception classes
	// //////////////////////////////////////////////////////////

	/**
	 * exception used when our pattern matching fail s
	 */
	public static class MatchingException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * declare a pattern matching failure
		 * 
		 * @param pattern
		 *          a description of what we were trying to match
		 * @param cause
		 *          a parent exception - if applicable
		 */
		public MatchingException(String pattern, Throwable cause)
		{
			super("The expression:" + pattern + " failed to match any items", cause);
		}
	}

	/**
	 * exception used when our pattern matching fail s
	 */
	public static class IllegalExpressionException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * declare a pattern matching failure
		 * 
		 * @param pattern
		 *          a description of what we were trying to match
		 * @param cause
		 *          a parent exception - if applicable
		 */
		public IllegalExpressionException(String pattern, Throwable cause)
		{
			super("The expression:" + pattern + " is an invalid XPath", cause);
		}
	}

	public static class UniversalNamespaceResolver implements NamespaceContext
	{
		// the delegate
		private Document sourceDocument;

		/**
		 * This constructor stores the source document to search the namespaces in
		 * it.
		 * 
		 * @param document
		 *          source document
		 */
		public UniversalNamespaceResolver(Document document)
		{
			sourceDocument = document;
		}

		/**
		 * The lookup for the namespace uris is delegated to the stored document.
		 * 
		 * @param prefix
		 *          to search for
		 * @return uri
		 */
		public String getNamespaceURI(String prefix)
		{
			if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
			{
				return sourceDocument.lookupNamespaceURI(null);
			}
			return sourceDocument.lookupNamespaceURI(prefix);
		}

		/**
		 * This method is not needed in this context, but can be implemented in a
		 * similar way.
		 */
		public String getPrefix(String namespaceURI)
		{
			return sourceDocument.lookupPrefix(namespaceURI);
		}

		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespaceURI)
		{
			// not implemented yet
			return null;
		}

	}

	/**
	 * namespace resolver that receives a prefix and a URI - and gets the answer
	 * using a quick lookup
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class NamespaceContextProvider implements NamespaceContext
	{
		String boundPrefix, boundURI;

		private NamespaceContextProvider(String prefix, String URI)
		{
			boundPrefix = prefix;
			boundURI = URI;
		}

		/**
		 * convenience method that sorts out some xpath bits for us
		 * 
		 * @param theXPath
		 * @return
		 * @throws XPathExpressionException
		 */
		public static XPathExpression createPath(final String theXPath)
				throws XPathExpressionException
		{
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xp = xpf.newXPath();

			// tell it what schema to use for the indicated elements
			NamespaceContextProvider myResolver = new NamespaceContextProvider(
					NAMESPACE_PREFIX, "http://www.mwc.org/asset");
			xp.setNamespaceContext(myResolver);

			// note, we've got to stick the as bit in front of any matching xpath
			// items
			String myPath = myResolver.insertPrefixesTo(theXPath);

			return xp.compile(myPath);
		}

		/**
		 * we've got a no-namespace string here. We need to insert namespaces for
		 * the xpath resolver to match the items. stick them in after every / symbol
		 * 
		 * @param theXPath
		 *          the xpath regex received
		 * @return the same regex, but with as: inserted before every element name
		 */
		private String insertPrefixesTo(final String theXPath)
		{

			String theRes = new String(theXPath);
			String[] items = theRes.split("/");
			for (int i = 0; i < items.length; i++)
			{
				String thisA = items[i];
				if (thisA.length() > 2)
				{
					if (Character.isLetter(thisA.charAt(0)))
					{
						// right, we don't just to a straight-forward replace, since our
						// phrase may appear more
						// than once in the expression, or within another string.
						// so, we prepend the search with a slash, and include that slash
						// with the replacement string
						theRes = theRes.replace("/" + thisA, "/" + boundPrefix + ":"
								+ thisA);
					}
				}
			}

			return theRes;
		}

		public String getNamespaceURI(String prefix)
		{
			if (prefix.equals(boundPrefix))
			{
				return boundURI;
			}
			else if (prefix.equals(XMLConstants.XML_NS_PREFIX))
			{
				return XMLConstants.XML_NS_URI;
			}
			else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE))
			{
				return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
			}
			else
			{
				return XMLConstants.NULL_NS_URI;
			}
		}

		public String getPrefix(String namespaceURI)
		{
			if (namespaceURI.equals(boundURI))
			{
				return boundPrefix;
			}
			else if (namespaceURI.equals(XMLConstants.XML_NS_URI))
			{
				return XMLConstants.XML_NS_PREFIX;
			}
			else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
			{
				return XMLConstants.XMLNS_ATTRIBUTE;
			}
			else
			{
				return null;
			}
		}

		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespaceURI)
		{
			// not implemented for the example
			return null;
		}

	}

}
