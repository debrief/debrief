<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:debrief="http://www.debrief.info/"
    exclude-result-prefixes="xs debrief"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"  doctype-public="-//OASIS//DTD DITA Map//EN" doctype-system="map.dtd"/>
    
    <debrief:order>
        <debrief:value>intro_composite.xml</debrief:value>
        <debrief:value>analysis_composite.xml</debrief:value>
    </debrief:order>
    
    <xsl:template match="/" name="root">
        <map>
            <title>Debrief Topic Map</title>
            <xsl:for-each select="collection(iri-to-uri('../cheat_sheets/?select=[a-zA-Z]*_composite.xml;recurse=yes'))">
                <xsl:sort  select="count(document('')//debrief:order/debrief:value[. = tokenize(document-uri(current()),'/')[last()]]/preceding-sibling::debrief:value)"/>
                
                <xsl:result-document href="dita/{tokenize(document-uri(/),'/')[last()]}.dita" doctype-public="-//OASIS//DTD DITA Task//EN" doctype-system="task.dtd">
                    <xsl:apply-templates select="/compositeCheatsheet"/>
                </xsl:result-document>
                
                <topicref href="dita/{tokenize(document-uri(/),'/')[last()]}.dita" type="task"/>
            </xsl:for-each>
        </map>
    </xsl:template>
    
    <xsl:template match="compositeCheatsheet">
        <xsl:apply-templates select="taskGroup"/>
    </xsl:template>
    
    <xsl:template match="taskGroup">
        <task id="{generate-id(.)}">
            <title>
                <xsl:apply-templates select="@name"/>
            </title>
            <shortdesc>
                <xsl:apply-templates select="intro"/>
            </shortdesc>
            <xsl:choose>
                <xsl:when test="taskGroup">
                    <xsl:apply-templates select="taskGroup"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="task">
                        <xsl:variable name="path" select="param[@name='path']/@value"/>
                        <task id="{generate-id(.)}">
                            <title>
                                <xsl:apply-templates select="document($path)//cheatsheet/@title"/>
                            </title>
                            <shortdesc>
                                <xsl:apply-templates select="document($path)//cheatsheet/intro/description"/>
                            </shortdesc>
                            <taskbody>             
                                <steps>
                                    <xsl:apply-templates select="document($path)//cheatsheet"/>                                    
                                </steps>
                                <result>
                                    <xsl:apply-templates select="onCompletion"/>
                                </result>
                            </taskbody>
                        </task>
                    </xsl:for-each>  
                </xsl:otherwise>
            </xsl:choose>
        </task>  
    </xsl:template>
    
    <xsl:template match="cheatsheet">     
        <xsl:for-each select="item">
            <step id="{generate-id(.)}">
                <cmd>
                    <xsl:apply-templates select="@title"/>
                </cmd>
                <info>
                    <xsl:apply-templates select="description"/>
                </info>
                <stepxmp>
                    <xsl:choose>
                        <xsl:when test="@skip = 'true'">optional</xsl:when>
                    </xsl:choose> 
                </stepxmp>
            </step>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="b">
        <b><xsl:apply-templates/></b>
    </xsl:template>
    
    <xsl:template match="text()">
        <xsl:copy/>
    </xsl:template>
    
    <xsl:template match="i[.= 'NoPrint']"/>
    
    <xsl:template match="text()[preceding-sibling::i[.='NoPrint'][1]][following-sibling::i[.='NoPrint'][1] ] | *[preceding-sibling::i[.='NoPrint'][1]][following-sibling::i[.='NoPrint'][1] ]"/>
    
    <xsl:template match="br">
        <ph/>
    </xsl:template>
    
    <xsl:template match="i">
        <xsl:apply-templates/>
    </xsl:template>
</xsl:stylesheet>