<?xml version="1.0"?> 
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/TR/WD-xsl"
    xmlns="http://www.w3.org/TR/REC-html40"
    result-ns=""
    indent-result="yes">   


  <xsl:template match="gamelets">
    <HTML>
      <HEAD>
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1" />
        <TITLE>Q2Java Gamelets</TITLE>
      </HEAD>
      <BODY>
        <h1>Q2Java Gamelets</h1>
        <xsl:apply-templates select="gamelet"/>
        <hr/>
        <div align="right"><font size="-1"><a href="http://www.planetquake.com/q2java">Q2Java homepage</a></font></div>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="gamelet">
    <font face="Arial,Helvetica" size="+2"><xsl:value-of select="@class"/></font><br/>
    <font size="+1"><xsl:value-of select="title"/></font>  <xsl:apply-templates select="author"/>
    <p/>
    <xsl:apply-templates select="description"/>
    <p/>
  </xsl:template>
  
  <xsl:template match="author">
    <font size="-1"><a href="mailto:{@mail}"><xsl:apply-templates/></a></font>
  </xsl:template>
  
  <xsl:template match="description">  
    <xsl:apply-templates/>
  </xsl:template>
</xsl:stylesheet>