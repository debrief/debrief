<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <xsl:template match="/">
        <html>
            <head />
            <body>
                <xsl:apply-templates />
            </body>
        </html>
    </xsl:template>
    <xsl:template match="description">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="intro">
        <span style="display:block; ">
            <xsl:apply-templates />
        </span>
    </xsl:template>
    <xsl:template match="item">
        <span style="display:block; font-family:Arial; margin-bottom:20px; ">
            <xsl:apply-templates />
        </span>
    </xsl:template>
</xsl:stylesheet>
