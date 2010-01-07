<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version='1.0'
  xmlns="http://www.w3.org/TR/xhtml1/transitional"
  exclude-result-prefixes="#default">

  <xsl:import href="../../contribs/docbook-xsl-1.72.0/eclipse/eclipse.xsl"/>

  <!-- Add other variable definitions here -->

  <xsl:param name="generate.section.toc.level" select="1"/>
  <xsl:param name="toc.section.depth">2</xsl:param>
  <xsl:param name="toc.max.depth">3</xsl:param>
  <xsl:param name="section.label.includes.component.label">1</xsl:param>

  <xsl:param name="admon.graphics.path">images/</xsl:param>
  <xsl:param name="admon.graphics.extension" select="'.gif'"/>
  <xsl:param name="admon.graphics" select="1"/>
  <xsl:param name="html.stylesheet" select="'header.css'"/>
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="use.id.as.filename" select="'1'"/>
  <xsl:param name="chunk.section.depth" select="1"/>
  <xsl:param name="navig.showtitles">1</xsl:param>
  <xsl:param name="root.filename" select="'index'"/>
  <xsl:param name="html.extra.head.links" select="1"/>
  <xsl:param name="chunker.output.method" select="'html'"/>
  <xsl:param name="base.dir" select="'html/legacy/'"/>
  <xsl:param name="chunk.quietly" select="1"/>
  <xsl:param name="chunk.first.sections" select="0"></xsl:param>

  <xsl:param name="generate.id.attributes" select="1"></xsl:param>
  <xsl:param name="show.revisionflag">1</xsl:param>

  <xsl:param name="navig.graphics">1</xsl:param>
  <xsl:param name="generate.index" select="1"/>
  <xsl:param name="navig.graphics.extension" select="'.gif'"/>
  <xsl:param name="navig.graphics.path">images/</xsl:param>

  <!-- stop the images being scaled -->
  <xsl:param name="ignore.image.scaling" select="1"/>

  <!--
  <xsl:template name="user.footer.navigation">
    <p class="UpdatedDTG">Last updated:11-January-2005 16:15</p>
  </xsl:template>  -->

	<xsl:param name="eclipse.plugin.name">ASSET User Guide</xsl:param>
	<xsl:param name="eclipse.plugin.provider">Maritime Warfare Centre</xsl:param>
	<xsl:param name="eclipse.plugin.id">org.mwc.asset.help</xsl:param>

</xsl:stylesheet>
