<?xml version="1.0"?>
<!--
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:str="http://ictk.sourceforge.net/namespace/string"
		xmlns:text="http://ictk.sourceforge.net/namespace/text"
		xmlns:redirect="http://xml.apache.org/xalan/redirect"
		xmlns:java="java"
		extension-element-prefixes="redirect"
		>


<!-- necessary because XSLTC redirect:write does not respect Ant:destdir -->
<xsl:param name="destpath"/>

<xsl:import href="string.xsl"/>
<xsl:import href="text.xsl"/>

<xsl:output method="text" 
            omit-xml-declaration="yes"/>

<xsl:template match="icsevtml">
   <xsl:apply-templates select="event"/>
</xsl:template>

<xsl:template match="event">
   <xsl:apply-templates select="parser"/>
</xsl:template>

<!-- main event -->
<xsl:template match="parser">
   <xsl:variable name="classname" select="concat(@protocol, 
                                                @name, 
					        'Parser')"
						/>
   <xsl:variable name="lc_protocol">
       <xsl:call-template name="str:toLower">
          <xsl:with-param name="input" select="@protocol"/>
       </xsl:call-template>
   </xsl:variable>

   <xsl:variable name="rel-filename" select="concat('../',
                                                $lc_protocol,
                                                '/event/',
                                                $classname,
					        '.java')"
						/>

   <xsl:variable name="filename" select="concat($destpath,
	   					'/',
	   					$rel-filename)"
						 />
   <!-- write out the filename so we can delete it later -->
   <xsl:value-of select="$rel-filename"/><xsl:text>
</xsl:text>

<redirect:write file="{$filename}">/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke &lt;ictk.jvarsoke [at] neverbox.com&gt;
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ictk.boardgame.chess.net.ics.<xsl:call-template name="str:toLower">
   <xsl:with-param name="input" select="@protocol"/>
   </xsl:call-template>.event;

/*--------------------------------------------------------------------------*
 * This file was auto-generated 
 * by $Id$
 * on <xsl:value-of select="java:util.Date.new()"/>
 *--------------------------------------------------------------------------*/

import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.*;
import java.util.regex.*;
import java.io.IOException;

/**
<xsl:apply-templates select="description" mode="text:comment-wrap">
   <xsl:with-param name="width" select="70"/>
   <xsl:with-param name="style" select="'C'"/>
   <xsl:with-param name="indent" select="1"/>
</xsl:apply-templates>
 */
public class <xsl:value-of select="$classname"/> extends <xsl:value-of select="@extends"/> {

   //static/////////////////////////////////////////////////////////////////
   public static <xsl:value-of select="$classname"/> singleton;
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^<xsl:if test="@detectFake='yes'">:?</xsl:if>(" //begin
<xsl:apply-templates select="regex" mode="java"/>
         + ")"  //end
         , Pattern.MULTILINE);

      singleton = new <xsl:value-of select="$classname"/>();
   }

   //instance///////////////////////////////////////////////////////////////
   protected <xsl:value-of select="$classname"/> () {
      super(masterPattern);
   }

   /* getInstance ***********************************************************/
   public static ICSEventParser getInstance () {
       return singleton;
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICS<xsl:value-of select="../@class"/>Event();
         <xsl:if test="@enum">evt.setEventType(ICSEvent.<xsl:value-of select="@enum"/>_EVENT);
         </xsl:if>assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICS<xsl:value-of select="../@class"/>Event evt = (ICS<xsl:value-of select="../@class"/>Event) event;

      if (Log.debug &amp;&amp; debug)
         Log.debug(DEBUG, "assigning matches", m);

      <xsl:if test="@detectFake='yes'">
      evt.setFake(detectFake(m.group(0)));
      </xsl:if>

      <xsl:apply-templates select="assignMatches"/>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICS<xsl:value-of select="../@class"/>Event evt = (ICS<xsl:value-of select="../@class"/>Event) event;
      StringBuffer sb = new StringBuffer(<xsl:value-of select="child::toNative/@avgLength"/>);
      <xsl:if test="@detectFake='yes'">
      if (evt.isFake()) sb.append(":");
      </xsl:if>

      <xsl:apply-templates select="toNative"/>

      return sb.toString();
   }
   <xsl:apply-templates select="code"/>
}
</redirect:write>
</xsl:template>

<!-- REGEX - process the regex to java code format -->
<!-- FIXME: if the regex is a single line for some reason it is lost -->
<xsl:template match="regex" mode="java">

   <xsl:call-template name="text:regex-format">
      <xsl:with-param name="input">

      <!-- change all "  to \" -->
      <xsl:call-template name="str:search-and-replace">
	 <xsl:with-param name="input">

	    <!-- convert all \ to \\ -->
	    <xsl:call-template name="str:search-and-replace">
	       <xsl:with-param name="input">
		  <xsl:apply-templates select="."/>
	       </xsl:with-param>
	       <xsl:with-param name="search" select="'\'"/>
	       <xsl:with-param name="replace" select="'\\'"/>
	    </xsl:call-template>

	 </xsl:with-param>
	 <xsl:with-param name="search" select="'&quot;'"/>
	 <xsl:with-param name="replace" select="'\&quot;'"/>
      </xsl:call-template>

      </xsl:with-param>
      <xsl:with-param name="indent" select="9"/>
   </xsl:call-template>
</xsl:template>

<xsl:template match="regex">
   <xsl:apply-templates select="regexref | text()"/>
</xsl:template>

<xsl:template match="regexref">
   <xsl:apply-templates select="id(@ref)"/>
</xsl:template>

<!-- assignMatches - put the regex matches into appropriate variables-->
<xsl:template match="assignMatches">
   <xsl:apply-templates select="regexgroup | text()" mode="assignMatches"/>
</xsl:template>

<xsl:template match="regexgroup" mode="assignMatches">
    <xsl:apply-templates select="id(@memberref)" mode="assignMatches">
       <xsl:with-param name="regexgroup" select="@num"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="member" mode="assignMatches">
   <xsl:param name="regexgroup"/>
   <xsl:variable name="varname">
      <xsl:choose>
         <xsl:when test="@varname">
            <xsl:value-of select="@varname"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="id(@typeref)/@varname"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:variable name="functname">
      <xsl:choose>
         <xsl:when test="@functname">
            <xsl:value-of select="@functname"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="id(@typeref)/@functname"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:variable name="type" select="id(@typeref)/@type"/>

   <xsl:choose>
      <!-- int -->
      <xsl:when test="$type='int'">
      try {
         evt.set<xsl:value-of select="$functname"/>
	 <xsl:text>(Integer.parseInt(m.group(</xsl:text>
	 <xsl:value-of select="$regexgroup"/>)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
            "Can't parse <xsl:value-of select="$varname"/> for: "
            + m.group(<xsl:value-of select="$regexgroup"/>) 
            + " of " + m.group(0));
         evt.setEventType(ICSEvent.UNKNOWN_EVENT);
         evt.setMessage(m.group(0));
	 if (Log.debug)
	    Log.debug(ICSEventParser.DEBUG, "regex", m);
         return;
      }<!-- -->
      </xsl:when>

      <!-- String -->
      <xsl:when test="$type='String'">
      evt.set<xsl:value-of select="$functname"/>(m.group(<xsl:value-of select="$regexgroup"/>));<!-- -->
      </xsl:when>

      <!-- AccountType | Rating -->
      <xsl:when test="$type='ICSAccountType' or $type='ICSRating'">
      evt.set<xsl:value-of select="$functname"/>
      <xsl:text>(parse</xsl:text>
      <xsl:value-of select="$type"/>
      <xsl:text>(m, </xsl:text>
      <xsl:value-of select="$regexgroup"/>));<!-- -->
      </xsl:when>

      <xsl:when test="$type='ICSResult'">
      evt.setResult(new ICSResult(m.group(<xsl:value-of 
      select="$regexgroup"/>)));<!-- -->
      </xsl:when>

      <xsl:when test="$type='ICSVariant'">
      evt.setVariant(new ICSVariant(m.group(<xsl:value-of 
      select="$regexgroup"/>)));<!-- -->
      </xsl:when>

      <!-- unknown -->
      <xsl:otherwise>
      //FIXME: don't know how to set type <xsl:value-of select="@type"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<!-- toNatvie - slap the code to convert the variables back into the natvie
     message here.
-->
<xsl:template match="toNative">
   <xsl:apply-templates select="code"/>
</xsl:template>

<xsl:template match="code">
   <xsl:if test="@format='java'">
      <xsl:value-of select="."/>
   </xsl:if>
</xsl:template>

</xsl:stylesheet>
