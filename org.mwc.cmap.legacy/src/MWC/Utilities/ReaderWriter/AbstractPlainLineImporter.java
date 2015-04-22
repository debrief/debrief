package MWC.Utilities.ReaderWriter;

import java.util.StringTokenizer;


public abstract class AbstractPlainLineImporter implements PlainLineImporter
{
	/**
	 * when importing the name, it may turn out to be quoted, in which case
	 * we consume the remaining tokens until we get another quote
	 * 
	 * @param st
	 *          the tokenised stream to read the name from
	 * @return a string containing the (possibly multi-word) track name
	 */
	public static String checkForQuotedName(final StringTokenizer st)
	{
		String theName = st.nextToken();
	
		// so, does the track name contain a quote character?
		final int quoteIndex = theName.indexOf("\"");
		if (quoteIndex >= 0)
		{
			// aah, but, we may have just read in all of the item. just check if
			// the
			// token contains
			// both speech marks...
			final int secondQuoteIndex = theName.indexOf("\"", quoteIndex + 1);
	
			if (secondQuoteIndex >= 0)
			{
				// yes, we have caught both quotes
				// just trim off the quote marks
				theName = theName.substring(1, theName.length() - 1);
			}
			else
			{
				// no, we just caught the first quote.
				// fish around for the second one.
	
				String lastPartOfName = st.nextToken(quoteDelimiter);
	
				// yup. the ne
				theName += lastPartOfName;
	
				// and trim away the quote
				theName = theName.substring(theName.indexOf("\"") + 1);
	
				// consume the trailing quote delimiter (note - we allow spaces
				// & tabs)
				lastPartOfName = st.nextToken(" \t");
			}
		}
		return theName;
	}

	protected String symbology;

	@Override
	public String getSymbology()
	{
		return symbology;
	}
}
