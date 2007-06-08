<?xml version="1.0" encoding="UTF-8"?>

<!--
   A simple XSL file from Tammo van Lessen
   Transforms Commitlog to xdoc
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:func="http://statcvs-xml.berlios.de/functions"
                xmlns:i18n="de.berlios.statcvs.xml.I18n"
                xmlns:ds="de.berlios.statcvs.xml.output.DocumentSuite"
				exclude-result-prefixes="func i18n ds">

  <xsl:param name="ext"/>
  <xsl:template match="document">
    <document>
       <properties>
         <title><xsl:value-of select="@title"/></title>
       </properties>
       <body>
       		<xsl:choose>
   				<xsl:when test="@name != 'index'">
   					<section name="{@title}">
                		<p><a href="index{$ext}">Back to Index Page</a></p>
                	</section>
		 		</xsl:when>
				<xsl:otherwise>
<!--					<section name="StatCvs-XML Report"/> -->
				</xsl:otherwise>
			</xsl:choose>
	     	<xsl:apply-templates select="pager"/>
	     	<xsl:apply-templates select="report"/>
       </body>
    </document>
  </xsl:template>

  <xsl:template match="report">
    <section name="{@name}">
	  <xsl:apply-templates select="*"/>
	</section>
  </xsl:template>

  <xsl:template match="container">
	<p>
		<xsl:apply-templates select="*"/>
	</p>
  </xsl:template>
  
  <xsl:template match="pager">
     <p>
     <xsl:if test="@current!=1">
         <xsl:element name="a">
             <!-- dont break the line -->
             <xsl:attribute name="href"><xsl:value-of select="page[@nr=(../@current)-1]/@filename"/><xsl:value-of select="$ext"/></xsl:attribute>
             <xsl:text>&lt;&lt;</xsl:text>
         </xsl:element>
     </xsl:if>
	 <xsl:for-each select="page">
		<xsl:if test="@nr != ../@current">
		  <a href="{@filename}{$ext}"><xsl:value-of select="@nr"/></a>
		</xsl:if>
		<xsl:if test="@nr = ../@current">
		  <xsl:value-of select="@nr"/>
		</xsl:if>
	 </xsl:for-each>
     <xsl:if test="@current!=@total">
	     <xsl:element name="a">
             <!-- dont break the line -->
		     <xsl:attribute name="href"><xsl:value-of select="page[@nr=(../@current)+1]/@filename"/><xsl:value-of select="$ext"/></xsl:attribute>
		     <xsl:text>&gt;&gt;</xsl:text>
	     </xsl:element>
     </xsl:if>
     </p>
  </xsl:template>
  
  <xsl:template name="link" match="link">
	<xsl:choose>
	<xsl:when test="substring(@ref,string-length(@ref),1)='/'">
	    <a href="{@ref}"><xsl:apply-templates /></a>
	</xsl:when>
	<xsl:otherwise>
	    <a href="{@ref}{$ext}"><xsl:apply-templates /></a>
	</xsl:otherwise>
	</xsl:choose>	
  </xsl:template>

  <xsl:template match="img">
    <p align="center">
    <xsl:element name="img">
      <xsl:attribute name="border">0</xsl:attribute>
      <xsl:copy-of select="@*"/>
    </xsl:element>
    </p>
  </xsl:template>

  <xsl:template match="map">
	<map>
    <xsl:copy-of select="*|@*"/>
    </map>
  </xsl:template>

  <xsl:template match="text">
    <div><xsl:value-of select="."/></div><!-- <br/> -->
  </xsl:template>

  <xsl:template match="period">
	<div><xsl:value-of select="@name"/>: <xsl:value-of select="@from"/>
	<xsl:if test="@to"><xsl:text> </xsl:text>
      <xsl:value-of select="i18n:tr('to')"/><xsl:text> </xsl:text><xsl:value-of select="@to"/>
    </xsl:if>
    </div>
    <!-- <br/> -->
  </xsl:template>
  
  <xsl:template match="value">
    <div><xsl:apply-templates/>: <xsl:value-of select="@value"/></div>
    <!-- <br/> -->
  </xsl:template>

  <!-- copy any other elements through -->
  <xsl:template match="*">
    <xsl:copy>
	  <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- EMPTY reports/elements -->
  <xsl:template name="ignore"/>
  
  <!-- FUNCTIONS -->
  <xsl:template name="func:make-link">
    <xsl:param name="url"/>
    <xsl:param name="text"/>
    <xsl:param name="localurl"/>
	<xsl:choose>
		<xsl:when test="$url">
              <xsl:element name="a">
                <xsl:attribute name="href"><xsl:value-of select="$url"/></xsl:attribute>
                <xsl:value-of select="$text"/>
              </xsl:element>
		</xsl:when>
		<xsl:when test="$localurl">
              <xsl:element name="a">
                <xsl:attribute name="href"><xsl:value-of select="$localurl"/><xsl:value-of select="$ext"/></xsl:attribute>
                <xsl:value-of select="$text"/>
              </xsl:element>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$text"/>
		</xsl:otherwise>
	</xsl:choose>
  </xsl:template>

  <xsl:template name="func:spacer">
    <xsl:param name="num"/>
	<xsl:if test="$num &gt; 0">
  	  <xsl:text disable-output-escaping="yes"><![CDATA[&#160;&#160;&#160;&#160;]]></xsl:text>
<!--	  <xsl:text><![CDATA[&#160;&#160;&#160;&#160;]]></xsl:text> -->
<!--	  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> -->
	  <xsl:call-template name="func:spacer">
	    <xsl:with-param name="num" select="number($num)-1"/>
	  </xsl:call-template>
	</xsl:if>    
  </xsl:template>  

  <xsl:template match="row">
     <tr><xsl:apply-templates /></tr>
  </xsl:template>  

  <xsl:template match="row/author">
    <td>
        <xsl:call-template name="func:make-link">
			<xsl:with-param name="text" select="@fullname"/>
			<xsl:with-param name="localurl" select="ds:getAuthorFilename(@name)"/>
        </xsl:call-template>
     </td>
  </xsl:template>

  <xsl:template match="row/commit">
       <td><div>      		
		  <b><xsl:value-of select="comment"/></b>
		  (<xsl:value-of select="@changedfiles"/><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('Files changed')"/>,
		  <xsl:value-of select="@changedlines"/><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('Lines changed')"/>)</div>
		  <!-- <br/> -->
		  <xsl:for-each select="files/file">
              <!--<xsl:element name="a">
                   <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                   <xsl:value-of select="@directory"/>
                   <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:value-of select="@revision"/>
              </xsl:element> -->
              <div>
              <xsl:call-template name="func:make-link">
				<xsl:with-param name="text">
				   <xsl:value-of select="@directory"/>
                   <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:value-of select="@revision"/>
				</xsl:with-param>
				<xsl:with-param name="url" select="@url"/>
              </xsl:call-template>
              
              <xsl:if test="@action = 'added'">
                <font color="green"><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('added')"/><xsl:text> </xsl:text><xsl:value-of select="@lines"/></font>
              </xsl:if>
              <xsl:if test="@action = 'deleted'">
                <font color="red"><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('removed')"/></font>
              </xsl:if>
              <xsl:if test="@action = 'changed'">
                 (+<xsl:value-of select="@added"/>
                 -<xsl:value-of select="@removed"/>)
              </xsl:if>
              </div>
              <!-- <br/> -->
		  </xsl:for-each>
       </td>
  </xsl:template>

  <xsl:template match="row/directory">
    <td>
        <xsl:call-template name="func:make-link">
			<xsl:with-param name="text" select="@name"/>
			<xsl:with-param name="localurl" select="ds:getDirectoryFilename(@name)"/>
        </xsl:call-template>
     </td>
  </xsl:template>

  <xsl:template match="row/directoryTree">
    <td>
      <xsl:call-template name="func:spacer">
      	<xsl:with-param name="num" select="@depth"/>
      </xsl:call-template>
      <xsl:choose>
         <xsl:when test="@removed"><img src="folder-deleted.png"/></xsl:when>
         <xsl:when test="@depth &lt; following::directoryTree[1]/@depth and not(@removed)"><img src="folder-open.png"/></xsl:when>
         <xsl:otherwise><img src="folder.png"/></xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="func:make-link">
 		<xsl:with-param name="text" select="@name"/>
		<xsl:with-param name="localurl" select="ds:getDirectoryFilename(@path)"/>
      </xsl:call-template>
     </td>
  </xsl:template>

  <xsl:template match="row/link">
    <td>
        <xsl:call-template name="func:make-link">
			<xsl:with-param name="text" select="."/>
			<xsl:with-param name="url" select="@url"/>
        </xsl:call-template>
     </td>
  </xsl:template>

  <xsl:template match="row/module">
    <td>
        <xsl:call-template name="func:make-link">
			<xsl:with-param name="text" select="@name"/>
			<xsl:with-param name="localurl" select="ds:getModuleFilename(@name)"/>
        </xsl:call-template>
     </td>
  </xsl:template>
  
  <xsl:template match="row/number">
    <td>
		<xsl:value-of select="."/> 
		<xsl:if test="@percentage != ''">
			(<xsl:value-of select="@percentage"/>%)
     	</xsl:if>
     </td>
  </xsl:template>

  <xsl:template match="row/string">
    <td>
		<xsl:value-of select="."/> 
     </td>
  </xsl:template>

  <xsl:template match="row/container">
    <td>
		<xsl:apply-templates select="*"/> 
     </td>
  </xsl:template>

  <xsl:template match="row/image">
    <td>
	    <p align="center"><img src="{@src}"/></p>
     </td>
  </xsl:template>

</xsl:stylesheet>