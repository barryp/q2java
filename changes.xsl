<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes"/>

  <!-- Copy any unmatched elements exactly from the XML to the output file -->
<!--
  <xsl:template match="*|@*|comment()|processing-instruction()|text()">
    <xsl:copy>
      BOGUS-START
      <xsl:apply-templates select="*|@*|comment()|processing-instruction()|text()"/>
      BOGUS-END
    </xsl:copy>
  </xsl:template>
-->
  <xsl:template match="changelog">
    <HTML>
      <HEAD>
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1" />
        <TITLE>Changes - v<xsl:value-of select="@version"/></TITLE>
      </HEAD>
      <BODY>
        <font face="Arial,Helvetica" size="+2"><b><i>      
        Changes in Q2Java <xsl:value-of select="@version"/>      
        </i></b>
        </font>
        <p/>
        
        <xsl:apply-templates select="comments"/>        
        
        <center></center>
        
        <table width="100%" cellpadding="8" cellspacing="2" border="0">
        
          <xsl:if test="dll">
            <tr><td colspan="2" align="center"><font face="Arial,Helvetica"><b><i>Changes to DLL</i></b></font></td></tr>
            <xsl:apply-templates select="dll"/>        
          </xsl:if>
          
          <xsl:if test="class">
            <tr><td colspan="2" align="center"><font face="Arial,Helvetica"><b><i>Changes in Java Classes</i></b></font></td></tr>
            <xsl:apply-templates select="class"/>        
          </xsl:if>
          
        </table>
        <hr/>
        <div align="right"><font size="-1"><a href="http://www.planetquake.com/q2java">Q2Java homepage</a></font></div>
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="comments">
    <xsl:apply-templates/>
    <p/>
  </xsl:template>
  
  <xsl:template match="para">
    <xsl:apply-templates/>
    <p/>
  </xsl:template>  

  <xsl:template match="dll">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="class">
    <tr><td colspan="2"><b><font face="Arial,Helvetica" size="+1"><xsl:value-of select="@name"/></font></b></td></tr>
    <xsl:apply-templates/>
    <tr><td colspan="2"></td></tr>
  </xsl:template>

  <xsl:template match="fix">
    <tr valign="top">
      <td bgcolor="#aa0000"><font color="#ffffff">Fix<br/>
      <font size="-2"><xsl:value-of select="@author"/></font></font></td>
      <td>
        <xsl:apply-templates/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="change">
    <tr valign="top">
      <td bgcolor="#0000cc"><font color="#ffffff">Change<br/>
      <font size="-2"><xsl:value-of select="@author"/></font></font></td>
      <td>
        <xsl:apply-templates/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="addition">
    <tr valign="top">
      <td bgcolor="#00aa00"><font color="#ffffff">Added<br/>
      <font size="-2"><xsl:value-of select="@author"/></font></font></td>
      <td>
        <xsl:apply-templates/>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template match="removed">
    <tr valign="top">
      <td bgcolor="#000000"><font color="#ffffff">Removed<br/>
      <font size="-2"><xsl:value-of select="@author"/></font></font></td>
      <td>
        <xsl:apply-templates/>
      </td>
    </tr>
  </xsl:template>
    
            
</xsl:stylesheet>