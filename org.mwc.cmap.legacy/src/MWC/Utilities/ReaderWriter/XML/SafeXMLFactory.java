/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.Utilities.ReaderWriter.XML;


import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


/**
 * Single place to obtain XML parsers for reading data files that may come from
 * an untrusted source (.dpf, .xml, .asf, .kml, .gpx).
 *
 * <p>
 * The parsers returned here reject DOCTYPE declarations outright, have
 * external general/parameter entities and external DTD loading switched off,
 * run with {@link XMLConstants#FEATURE_SECURE_PROCESSING} and XInclude
 * disabled, and refuse any external entity that still gets through. This
 * prevents XML External Entity (XXE) attacks (local file disclosure, outbound
 * requests) and entity-expansion bombs. No legitimate Debrief/ASSET data file
 * uses a DOCTYPE, so rejecting it does not affect real data.
 */
public final class SafeXMLFactory {

	/**
	 * Xerces feature: reject any document containing a DOCTYPE declaration
	 */
	public static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";

	public static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

	public static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

	public static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	/**
	 * entity resolver that refuses to resolve any external entity. Installed as a
	 * second line of defence, in case the parser implementation in use does not
	 * support one of the features above.
	 */
	public static final EntityResolver REJECT_EXTERNAL_ENTITIES = new EntityResolver() {
		@Override
		public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
			throw new SAXException(
					"External entities are not permitted in data files (publicId:" + publicId + " systemId:" + systemId + ")");
		}
	};

	private static void logUnsupported(final String what, final Exception e) {
		MWC.Utilities.Errors.Trace.trace("SafeXMLFactory: parser does not support " + what + ": " + e.getMessage(),
				false);
	}

	/**
	 * @return a DocumentBuilder from {@link #newDocumentBuilderFactory()} that also
	 *         refuses external entities
	 */
	public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		return newDocumentBuilder(false);
	}

	/**
	 * @param namespaceAware whether the builder should be namespace aware (needed
	 *                       for schema validation / JAXB)
	 * @return a DocumentBuilder from {@link #newDocumentBuilderFactory()} that also
	 *         refuses external entities
	 */
	public static DocumentBuilder newDocumentBuilder(final boolean namespaceAware)
			throws ParserConfigurationException {
		final DocumentBuilderFactory factory = newDocumentBuilderFactory();
		factory.setNamespaceAware(namespaceAware);
		final DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(REJECT_EXTERNAL_ENTITIES);
		return builder;
	}

	/**
	 * @return a DOM factory hardened against XXE and entity expansion
	 */
	public static DocumentBuilderFactory newDocumentBuilderFactory() {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final String[][] features = { { XMLConstants.FEATURE_SECURE_PROCESSING, "true" },
				{ DISALLOW_DOCTYPE, "true" }, { EXTERNAL_GENERAL_ENTITIES, "false" },
				{ EXTERNAL_PARAMETER_ENTITIES, "false" }, { LOAD_EXTERNAL_DTD, "false" } };
		for (final String[] feature : features) {
			try {
				factory.setFeature(feature[0], Boolean.parseBoolean(feature[1]));
			} catch (final ParserConfigurationException e) {
				logUnsupported(feature[0], e);
			}
		}
		setAttribute(factory, XMLConstants.ACCESS_EXTERNAL_DTD);
		setAttribute(factory, XMLConstants.ACCESS_EXTERNAL_SCHEMA);
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		return factory;
	}

	/**
	 * @return a SAX parser from {@link #newSAXParserFactory()}, with external
	 *         entities refused
	 */
	public static SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
		final SAXParser parser = newSAXParserFactory().newSAXParser();
		parser.getXMLReader().setEntityResolver(REJECT_EXTERNAL_ENTITIES);
		try {
			parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		} catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
			logUnsupported("ACCESS_EXTERNAL_DTD/SCHEMA", e);
		}
		return parser;
	}

	/**
	 * @return a SAX factory hardened against XXE and entity expansion
	 */
	public static SAXParserFactory newSAXParserFactory() {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final String[][] features = { { XMLConstants.FEATURE_SECURE_PROCESSING, "true" },
				{ DISALLOW_DOCTYPE, "true" }, { EXTERNAL_GENERAL_ENTITIES, "false" },
				{ EXTERNAL_PARAMETER_ENTITIES, "false" }, { LOAD_EXTERNAL_DTD, "false" } };
		for (final String[] feature : features) {
			try {
				factory.setFeature(feature[0], Boolean.parseBoolean(feature[1]));
			} catch (final ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {
				logUnsupported(feature[0], e);
			}
		}
		factory.setXIncludeAware(false);
		return factory;
	}

	/**
	 * @return a W3C XML Schema factory that will not fetch external DTDs or
	 *         schemas. Only suitable for schemas that are self-contained (e.g.
	 *         bundled .xsd resources)
	 */
	public static SchemaFactory newSchemaFactory() {
		final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
			logUnsupported(XMLConstants.FEATURE_SECURE_PROCESSING, e);
		}
		try {
			factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		} catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
			logUnsupported("ACCESS_EXTERNAL_DTD/SCHEMA", e);
		}
		return factory;
	}

	/**
	 * @return a validator for the schema that will not resolve external DTDs,
	 *         schemas or entities in the document being validated
	 */
	public static Validator newValidator(final Schema schema) {
		final Validator validator = schema.newValidator();
		try {
			validator.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
			logUnsupported(XMLConstants.FEATURE_SECURE_PROCESSING, e);
		}
		try {
			validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		} catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
			logUnsupported("ACCESS_EXTERNAL_DTD/SCHEMA", e);
		}
		return validator;
	}

	private static void setAttribute(final DocumentBuilderFactory factory, final String name) {
		try {
			factory.setAttribute(name, "");
		} catch (final IllegalArgumentException e) {
			logUnsupported(name, e);
		}
	}

	private SafeXMLFactory() {
		// static utility
	}

}
