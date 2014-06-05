<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version='1.0'
  xmlns="http://www.w3.org/TR/xhtml1/transitional"
  exclude-result-prefixes="#default">

	<!--  pull in the parent style sheet -->
	<xsl:import href="../../contribs/docbook-xsl-1.78.1/webhelp/xsl/webhelp.xsl"/>
	
	<!-- override the parent settings -->
	<xsl:param name="chunk.first.sections" select="0" />
	<xsl:param name="chunk.section.depth" select="1" />
	<xsl:param name="section.autolabel" select="1" />
	<xsl:param name="chapter.autolabel" select="1" /> 
	 
	<xsl:param name="ignore.image.scaling" select="1"/>
	<xsl:param name="chunk.quietly" select="1"/>
	
	<xsl:param name="admon.graphics" select="1"/>
	<xsl:param name="admon.graphics.path">images/</xsl:param>
	<xsl:param name="admon.graphics.extension" select="'.gif'"/>	
	
</xsl:stylesheet>
